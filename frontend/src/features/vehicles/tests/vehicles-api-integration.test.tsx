import { screen, waitFor } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { describe, it, expect, vi, beforeEach } from "vitest"
import { renderWithProviders, server } from "@/test"
import { http, HttpResponse } from "msw"

// Mock the openapi-fetch client to use globalThis.fetch (patched by MSW)
vi.mock("@/shared/api/client", async () => {
  const { default: createClient } = await import("openapi-fetch")
  return {
    api: createClient({
      baseUrl: import.meta.env.VITE_API_URL || "http://localhost:3000",
      fetch: (...args: Parameters<typeof globalThis.fetch>) => globalThis.fetch(...args),
    }),
  }
})

import VehiclesPage from "../pages/vehicles-page"

const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:3000"

const mockVehicleGroups = [
  {
    id: "vg-loeschfahrzeuge",
    name: "Löschfahrzeuge",
    beschreibung: "Alle Löschfahrzeuge des Löschzugs",
    createdAt: "2026-01-15T10:30:00Z",
    updatedAt: "2026-01-15T10:30:00Z",
  },
  {
    id: "vg-drehleiter",
    name: "Drehleitern",
    beschreibung: "Hubrettungsfahrzeuge",
    createdAt: "2026-02-10T08:00:00Z",
    updatedAt: "2026-02-10T08:00:00Z",
  },
]

const mockVehicles = [
  {
    id: "veh-hlf20-01",
    name: "01-HLF20-01",
    funkrufname: "Florian Monheim 01-HLF20-01",
    kennzeichen: "ME-FM 219",
    baujahr: 2019,
    beschreibung: "Hilfeleistungslöschgruppenfahrzeug 20",
    status: "VERFUEGBAR",
    vehicleGroupId: "vg-loeschfahrzeuge",
    vehicleGroupName: "Löschfahrzeuge",
    createdAt: "2026-01-15T10:30:00Z",
    updatedAt: "2026-01-15T10:30:00Z",
  },
  {
    id: "veh-dlak-01",
    name: "01-DLK23-01",
    funkrufname: "Florian Monheim 01-DLK23-01",
    kennzeichen: "ME-FM 220",
    baujahr: 2021,
    beschreibung: "Drehleiter mit Korb 23m",
    status: "WARTUNG",
    vehicleGroupId: "vg-drehleiter",
    vehicleGroupName: "Drehleitern",
    createdAt: "2026-02-10T08:00:00Z",
    updatedAt: "2026-02-10T08:00:00Z",
  },
  {
    id: "veh-mtf-01",
    name: "01-MTF-01",
    funkrufname: "Florian Monheim 01-MTF-01",
    kennzeichen: "ME-FM 221",
    baujahr: 2022,
    beschreibung: "Mannschaftstransportfahrzeug",
    status: "IM_EINSATZ",
    vehicleGroupId: "vg-loeschfahrzeuge",
    vehicleGroupName: "Löschfahrzeuge",
    createdAt: "2026-03-05T14:00:00Z",
    updatedAt: "2026-03-05T14:00:00Z",
  },
]

function setupDefaultHandlers() {
  server.use(
    http.get(`${BASE_URL}/api/v1/vehicles`, () => {
      return HttpResponse.json({
        data: mockVehicles,
        page: { page: 0, size: 20, totalElements: 3, totalPages: 1 },
      })
    }),
    http.get(`${BASE_URL}/api/v1/vehicle-groups`, () => {
      return HttpResponse.json({
        data: mockVehicleGroups,
        page: { page: 0, size: 20, totalElements: 2, totalPages: 1 },
      })
    }),
  )
}

describe("VehiclesPage API Integration", () => {
  beforeEach(() => {
    setupDefaultHandlers()
  })

  it("shows loading state initially", () => {
    renderWithProviders(<VehiclesPage />)
    expect(screen.getByText("Laden...")).toBeInTheDocument()
  })

  it("renders vehicles with status badges after successful fetch", async () => {
    renderWithProviders(<VehiclesPage />)

    expect(await screen.findByText("01-HLF20-01")).toBeInTheDocument()
    expect(screen.getByText("01-DLK23-01")).toBeInTheDocument()
    expect(screen.getByText("01-MTF-01")).toBeInTheDocument()
    expect(screen.getByText("ME-FM 219")).toBeInTheDocument()
    expect(screen.getByText("Verfügbar")).toBeInTheDocument()
    expect(screen.getByText("Wartung")).toBeInTheDocument()
    expect(screen.getByText("Im Einsatz")).toBeInTheDocument()
  })

  it("shows empty state when no vehicles exist", async () => {
    server.use(
      http.get(`${BASE_URL}/api/v1/vehicles`, () => {
        return HttpResponse.json({ data: [], page: { page: 0, size: 20, totalElements: 0, totalPages: 0 } })
      }),
    )

    renderWithProviders(<VehiclesPage />)

    expect(await screen.findByText("Keine Einträge vorhanden.")).toBeInTheDocument()
  })

  it("shows error state on API failure", async () => {
    server.use(
      http.get(`${BASE_URL}/api/v1/vehicles`, () => {
        return HttpResponse.json(
          { error: "INTERNAL_ERROR", message: "Server error" },
          { status: 500 },
        )
      }),
    )

    renderWithProviders(<VehiclesPage />)

    expect(await screen.findByText("Fehler beim Laden.")).toBeInTheDocument()
  })

  it("creates a new vehicle via API with vehicleGroupId", async () => {
    const user = userEvent.setup()
    let createRequestBody: unknown = null

    server.use(
      http.post(`${BASE_URL}/api/v1/vehicles`, async ({ request }) => {
        createRequestBody = await request.json()
        return HttpResponse.json(
          {
            id: "veh-new",
            name: "01-RW-01",
            funkrufname: "Florian Monheim 01-RW-01",
            kennzeichen: "ME-FM 222",
            baujahr: 2023,
            status: "VERFUEGBAR",
            vehicleGroupId: "vg-loeschfahrzeuge",
            vehicleGroupName: "Löschfahrzeuge",
            createdAt: "2026-08-17T10:00:00Z",
            updatedAt: "2026-08-17T10:00:00Z",
          },
          { status: 201 },
        )
      }),
    )

    renderWithProviders(<VehiclesPage />)
    expect(await screen.findByText("01-HLF20-01")).toBeInTheDocument()

    await user.click(screen.getByRole("button", { name: /erstellen/i }))

    const nameInput = await screen.findByLabelText("Name")
    const funkrufnameInput = screen.getByLabelText("Funkrufname")
    const kennzeichenInput = screen.getByLabelText("Kennzeichen")
    const baujahrInput = screen.getByLabelText("Baujahr")

    await user.type(nameInput, "01-RW-01")
    await user.type(funkrufnameInput, "Florian Monheim 01-RW-01")
    await user.type(kennzeichenInput, "ME-FM 222")
    await user.type(baujahrInput, "2023")

    // Select a vehicle group from the dropdown
    const vehicleGroupSelect = screen.getByLabelText("Fahrzeuggruppe")
    await user.click(vehicleGroupSelect)
    const groupOption = await screen.findByRole("option", { name: "Löschfahrzeuge" })
    await user.click(groupOption)

    await user.click(screen.getByRole("button", { name: "Erstellen" }))

    await waitFor(
      () => {
        expect(createRequestBody).toMatchObject({
          name: "01-RW-01",
          funkrufname: "Florian Monheim 01-RW-01",
          kennzeichen: "ME-FM 222",
          vehicleGroupId: "vg-loeschfahrzeuge",
        })
      },
      { timeout: 5000 },
    )
  }, 10000)

  it("filters vehicles by vehicleGroupId", async () => {
    const user = userEvent.setup()
    let lastRequestUrl: URL | null = null

    server.use(
      http.get(`${BASE_URL}/api/v1/vehicles`, ({ request }) => {
        lastRequestUrl = new URL(request.url)
        const groupId = lastRequestUrl.searchParams.get("vehicleGroupId")
        const filteredVehicles = groupId
          ? mockVehicles.filter((v) => v.vehicleGroupId === groupId)
          : mockVehicles
        return HttpResponse.json({
          data: filteredVehicles,
          page: { page: 0, size: 20, totalElements: filteredVehicles.length, totalPages: 1 },
        })
      }),
    )

    renderWithProviders(<VehiclesPage />)
    expect(await screen.findByText("01-HLF20-01")).toBeInTheDocument()

    // Click the group filter trigger
    const groupFilterTrigger = screen.getByText("Alle Gruppen").closest("button") ?? screen.getByText("Alle Gruppen")
    await user.click(groupFilterTrigger)
    const option = await screen.findByRole("option", { name: "Löschfahrzeuge" })
    await user.click(option)

    await waitFor(() => {
      expect(lastRequestUrl?.searchParams.get("vehicleGroupId")).toBe("vg-loeschfahrzeuge")
    })
  })

  it("filters vehicles by status", async () => {
    const user = userEvent.setup()
    let lastRequestUrl: URL | null = null

    server.use(
      http.get(`${BASE_URL}/api/v1/vehicles`, ({ request }) => {
        lastRequestUrl = new URL(request.url)
        const status = lastRequestUrl.searchParams.get("status")
        const filteredVehicles = status
          ? mockVehicles.filter((v) => v.status === status)
          : mockVehicles
        return HttpResponse.json({
          data: filteredVehicles,
          page: { page: 0, size: 20, totalElements: filteredVehicles.length, totalPages: 1 },
        })
      }),
    )

    renderWithProviders(<VehiclesPage />)
    expect(await screen.findByText("01-HLF20-01")).toBeInTheDocument()

    // Click the status filter trigger
    const statusFilterTrigger = screen.getByText("Alle Status").closest("button") ?? screen.getByText("Alle Status")
    await user.click(statusFilterTrigger)
    const option = await screen.findByRole("option", { name: "Wartung" })
    await user.click(option)

    await waitFor(() => {
      expect(lastRequestUrl?.searchParams.get("status")).toBe("WARTUNG")
    })
  })
})
