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

import VehicleGroupsPage from "../pages/vehicle-groups-page"

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

describe("VehicleGroupsPage API Integration", () => {
  beforeEach(() => {
    server.use(
      http.get(`${BASE_URL}/api/v1/vehicle-groups`, () => {
        return HttpResponse.json({
          data: mockVehicleGroups,
          page: { page: 0, size: 20, totalElements: 2, totalPages: 1 },
        })
      }),
    )
  })

  it("shows loading state initially", () => {
    renderWithProviders(<VehicleGroupsPage />)
    expect(screen.getByText("Laden...")).toBeInTheDocument()
  })

  it("renders vehicle groups after successful fetch", async () => {
    renderWithProviders(<VehicleGroupsPage />)

    expect(await screen.findByText("Löschfahrzeuge")).toBeInTheDocument()
    expect(screen.getByText("Drehleitern")).toBeInTheDocument()
    expect(screen.getByText("Alle Löschfahrzeuge des Löschzugs")).toBeInTheDocument()
    expect(screen.getByText("Hubrettungsfahrzeuge")).toBeInTheDocument()
  })

  it("shows empty state when no vehicle groups exist", async () => {
    server.use(
      http.get(`${BASE_URL}/api/v1/vehicle-groups`, () => {
        return HttpResponse.json({ data: [], page: { page: 0, size: 20, totalElements: 0, totalPages: 0 } })
      }),
    )

    renderWithProviders(<VehicleGroupsPage />)

    expect(await screen.findByText("Keine Einträge vorhanden.")).toBeInTheDocument()
  })

  it("shows error state on API failure", async () => {
    server.use(
      http.get(`${BASE_URL}/api/v1/vehicle-groups`, () => {
        return HttpResponse.json(
          { error: "INTERNAL_ERROR", message: "Server error" },
          { status: 500 },
        )
      }),
    )

    renderWithProviders(<VehicleGroupsPage />)

    expect(await screen.findByText("Fehler beim Laden.")).toBeInTheDocument()
  })

  it("creates a new vehicle group via API", async () => {
    const user = userEvent.setup()
    let createRequestBody: unknown = null

    server.use(
      http.post(`${BASE_URL}/api/v1/vehicle-groups`, async ({ request }) => {
        createRequestBody = await request.json()
        return HttpResponse.json(
          {
            id: "vg-new",
            name: "Einsatzleitwagen",
            beschreibung: "Führungsfahrzeuge",
            createdAt: "2026-08-17T10:00:00Z",
            updatedAt: "2026-08-17T10:00:00Z",
          },
          { status: 201 },
        )
      }),
    )

    renderWithProviders(<VehicleGroupsPage />)
    expect(await screen.findByText("Löschfahrzeuge")).toBeInTheDocument()

    await user.click(screen.getByRole("button", { name: /erstellen/i }))

    const nameInput = await screen.findByLabelText("Name")
    const beschreibungInput = screen.getByLabelText("Beschreibung")

    await user.type(nameInput, "Einsatzleitwagen")
    await user.type(beschreibungInput, "Führungsfahrzeuge")

    await user.click(screen.getByRole("button", { name: "Erstellen" }))

    await waitFor(() => {
      expect(createRequestBody).toEqual({
        name: "Einsatzleitwagen",
        beschreibung: "Führungsfahrzeuge",
      })
    })
  })

  it("updates an existing vehicle group via API", async () => {
    const user = userEvent.setup()
    let updateRequestBody: unknown = null

    server.use(
      http.put(`${BASE_URL}/api/v1/vehicle-groups/:vehicleGroupId`, async ({ request }) => {
        updateRequestBody = await request.json()
        return HttpResponse.json({
          id: "vg-loeschfahrzeuge",
          name: "Löschgruppenfahrzeuge",
          beschreibung: "Aktualisierte Beschreibung",
          createdAt: "2026-01-15T10:30:00Z",
          updatedAt: "2026-08-17T10:00:00Z",
        })
      }),
    )

    renderWithProviders(<VehicleGroupsPage />)
    expect(await screen.findByText("Löschfahrzeuge")).toBeInTheDocument()

    const editButtons = screen.getAllByRole("button", { name: "Bearbeiten" })
    await user.click(editButtons[0])

    const nameInput = await screen.findByLabelText("Name")
    await user.clear(nameInput)
    await user.type(nameInput, "Löschgruppenfahrzeuge")

    const beschreibungInput = screen.getByLabelText("Beschreibung")
    await user.clear(beschreibungInput)
    await user.type(beschreibungInput, "Aktualisierte Beschreibung")

    await user.click(screen.getByRole("button", { name: "Speichern" }))

    await waitFor(() => {
      expect(updateRequestBody).toEqual({
        name: "Löschgruppenfahrzeuge",
        beschreibung: "Aktualisierte Beschreibung",
      })
    })
  })

  it("deletes a vehicle group via API", async () => {
    const user = userEvent.setup()
    let deleteWasCalled = false

    server.use(
      http.delete(`${BASE_URL}/api/v1/vehicle-groups/:vehicleGroupId`, () => {
        deleteWasCalled = true
        return new HttpResponse(null, { status: 204 })
      }),
    )

    renderWithProviders(<VehicleGroupsPage />)
    expect(await screen.findByText("Löschfahrzeuge")).toBeInTheDocument()

    const deleteButtons = screen.getAllByRole("button", { name: "Löschen" })
    await user.click(deleteButtons[0])

    const confirmButton = await screen.findByRole("button", { name: "Löschen" })
    await user.click(confirmButton)

    await waitFor(() => {
      expect(deleteWasCalled).toBe(true)
    })
  })
})
