import { screen, waitFor, within } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { describe, it, expect, beforeEach, vi } from "vitest"
import { http, HttpResponse } from "msw"
import { MemoryRouter, Route, Routes } from "react-router"
import { renderWithProviders, server } from "@/test"
import EquipmentPage from "../pages/equipment-page"
import EquipmentCategoriesPage from "../pages/equipment-categories-page"
import EquipmentDetailPage from "../pages/equipment-detail-page"

vi.mock("@/shared/api/client", async () => {
  const { default: createClient } = await import("openapi-fetch")
  return {
    api: createClient({
      baseUrl: import.meta.env.VITE_API_URL || "http://localhost:3000",
      fetch: (...args: Parameters<typeof globalThis.fetch>) => globalThis.fetch(...args),
    }),
  }
})

const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:3000"

const categories = [
  { id: "category-breathing", name: "Atemschutz", description: "Atemschutzgeräte" },
  { id: "category-radio", name: "Funk", description: "Funkgeräte" },
]

const vehicles = [
  { id: "vehicle-hlf-20", name: "01-HLF20-01", funkrufname: "Florian Monheim 01-HLF20-01", kennzeichen: "ME-FM 219" },
]

const equipment = [
  {
    id: "equipment-radio-3",
    name: "Funkgerät 3",
    inventoryNumber: "F-003",
    description: "Handfunkgerät für den Einsatzstellenfunk",
    status: "DEFEKT" as const,
    categoryId: "category-radio",
    categoryName: "Funk",
    vehicleId: "vehicle-hlf-20",
    vehicleName: "01-HLF20-01",
    nextInspectionDate: "2026-06-12",
    nextMaintenanceDate: "2026-07-10",
    createdAt: "2026-01-15T10:30:00Z",
    updatedAt: "2026-01-15T10:30:00Z",
  },
  {
    id: "equipment-mask-1",
    name: "Pressluftatmer PA 300",
    inventoryNumber: "AGT-0042",
    description: "Pressluftatmer mit 300 bar Flasche",
    status: "VERFUEGBAR" as const,
    categoryId: "category-breathing",
    categoryName: "Atemschutz",
    vehicleId: null,
    vehicleName: null,
    nextInspectionDate: "2026-12-01",
    nextMaintenanceDate: null,
    createdAt: "2026-01-15T10:30:00Z",
    updatedAt: "2026-01-15T10:30:00Z",
  },
]

function defaultHandlers() {
  server.use(
    http.get(`${BASE_URL}/api/v1/equipment`, () => HttpResponse.json({
      data: equipment,
      page: { page: 0, size: 20, totalElements: 2, totalPages: 1 },
    })),
    http.get(`${BASE_URL}/api/v1/equipment-categories`, () => HttpResponse.json({
      data: categories,
      page: { page: 0, size: 100, totalElements: 2, totalPages: 1 },
    })),
    http.get(`${BASE_URL}/api/v1/vehicles`, () => HttpResponse.json({
      data: vehicles,
      page: { page: 0, size: 20, totalElements: 1, totalPages: 1 },
    })),
  )
}

function renderEquipmentPage() {
  return renderWithProviders(
    <MemoryRouter>
      <EquipmentPage />
    </MemoryRouter>,
  )
}

describe("EquipmentPage API integration", () => {
  beforeEach(() => {
    defaultHandlers()
  })

  it("renders equipment with status, vehicle and due date", async () => {
    renderEquipmentPage()

    expect(await screen.findByText("Funkgerät 3")).toBeInTheDocument()
    expect(screen.getByText("Defekt")).toBeInTheDocument()
    expect(screen.getByText("01-HLF20-01")).toBeInTheDocument()
    expect(screen.getByText("2026-06-12")).toBeInTheDocument()
  })

  it("shows an empty state when no equipment matches", async () => {
    server.use(http.get(`${BASE_URL}/api/v1/equipment`, () => HttpResponse.json({
      data: [], page: { page: 0, size: 20, totalElements: 0, totalPages: 0 },
    })))

    renderEquipmentPage()

    expect(await screen.findByText("Keine Geräte für die gewählten Filter gefunden.")).toBeInTheDocument()
  })

  it("shows a friendly error when the equipment request fails", async () => {
    server.use(http.get(`${BASE_URL}/api/v1/equipment`, () => HttpResponse.json(
      { error: "INTERNAL_ERROR", message: "Server error" }, { status: 500 },
    )))

    renderEquipmentPage()

    expect(await screen.findByText("Die Geräteliste konnte nicht geladen werden.")).toBeInTheDocument()
  })

  it("sends search, status and dueBefore filters to the API", async () => {
    const user = userEvent.setup()
    let lastRequestUrl: URL | undefined
    server.use(http.get(`${BASE_URL}/api/v1/equipment`, ({ request }) => {
      lastRequestUrl = new URL(request.url)
      return HttpResponse.json({ data: [], page: { page: 0, size: 20, totalElements: 0, totalPages: 0 } })
    }))

    renderEquipmentPage()
    const search = await screen.findByRole("textbox", { name: "Geräte suchen" })
    await user.clear(search)
    await user.paste("Funkgerät")
    const statusTrigger = screen.getAllByRole("combobox")[2]
    await user.click(statusTrigger)
    await user.click(await screen.findByRole("option", { name: "Defekt" }))
    await user.type(screen.getByLabelText("Fällig bis"), "2026-07-01")

    await waitFor(() => {
      expect(lastRequestUrl?.searchParams.get("search")).toBe("Funkgerät")
      expect(lastRequestUrl?.searchParams.get("status")).toBe("DEFEKT")
      expect(lastRequestUrl?.searchParams.get("dueBefore")).toBe("2026-07-01")
    })
  })

  it("creates equipment with required relationships and dates", async () => {
    const user = userEvent.setup()
    let requestBody: unknown
    server.use(http.post(`${BASE_URL}/api/v1/equipment`, async ({ request }) => {
      requestBody = await request.json()
      return HttpResponse.json({ ...equipment[1], id: "equipment-new" }, { status: 201 })
    }))

    renderEquipmentPage()
    await screen.findByText("Funkgerät 3")
    await user.click(screen.getByRole("button", { name: /gerät hinzufügen/i }))
    await user.type(await screen.findByLabelText("Name *"), "Tragbare Pumpe")
    await user.type(screen.getByLabelText("Inventarnummer *"), "P-017")

    const categoryTrigger = screen.getAllByRole("combobox")[0]
    await user.click(categoryTrigger)
    await user.click(await screen.findByRole("option", { name: "Atemschutz" }))
    await user.type(screen.getByLabelText("Nächste Prüfung"), "2026-11-15")
    await user.click(screen.getByRole("button", { name: "Anlegen" }))

    await waitFor(() => expect(requestBody).toEqual({
      name: "Tragbare Pumpe",
      inventoryNumber: "P-017",
      description: undefined,
      status: "VERFUEGBAR",
      categoryId: "category-breathing",
      vehicleId: null,
      nextInspectionDate: "2026-11-15",
      nextMaintenanceDate: null,
    }))
  })

  it("exposes an accessible name for the required category selector", async () => {
    const user = userEvent.setup()
    renderEquipmentPage()
    await screen.findByText("Funkgerät 3")
    await user.click(screen.getByRole("button", { name: /gerät hinzufügen/i }))

    expect(await screen.findByRole("combobox", { name: "Kategorie *" })).toBeInTheDocument()
  })

  it("archives equipment after confirmation", async () => {
    const user = userEvent.setup()
    let deletedId: string | undefined
    server.use(http.delete(`${BASE_URL}/api/v1/equipment/:equipmentId`, ({ params }) => {
      deletedId = String(params.equipmentId)
      return new HttpResponse(null, { status: 204 })
    }))

    renderEquipmentPage()
    await screen.findByText("Funkgerät 3")
    const row = screen.getByRole("row", { name: /Funkgerät 3/ })
    await user.click(within(row).getByRole("button", { name: "Archivieren" }))
    expect(await screen.findByRole("heading", { name: "Gerät archivieren?" })).toBeInTheDocument()
    await user.click(screen.getByRole("button", { name: "Archivieren" }))

    await waitFor(() => expect(deletedId).toBe("equipment-radio-3"))
  })
})

describe("Equipment detail API integration", () => {
  it("renders status history for a selected equipment", async () => {
    server.use(
      http.get(`${BASE_URL}/api/v1/equipment/equipment-radio-3`, () => HttpResponse.json(equipment[0])),
      http.get(`${BASE_URL}/api/v1/equipment/equipment-radio-3/history`, () => HttpResponse.json([
        { id: "history-1", equipmentId: "equipment-radio-3", previousStatus: "VERFUEGBAR", newStatus: "WARTUNG", changedAt: "2026-06-01T10:00:00Z" },
        { id: "history-2", equipmentId: "equipment-radio-3", previousStatus: "WARTUNG", newStatus: "DEFEKT", changedAt: "2026-06-12T12:00:00Z" },
      ])),
    )

    renderWithProviders(
      <MemoryRouter initialEntries={["/equipment/equipment-radio-3"]}>
        <Routes>
          <Route path="/equipment/:id" element={<EquipmentDetailPage />} />
        </Routes>
      </MemoryRouter>,
    )

    expect(await screen.findByRole("heading", { name: "Funkgerät 3" })).toBeInTheDocument()
    expect(screen.getByText("Funk", { exact: true })).toBeInTheDocument()
    expect(screen.getByText("Verfügbar → Wartung")).toBeInTheDocument()
    expect(screen.getByText("Wartung → Defekt")).toBeInTheDocument()
  })
})

describe("EquipmentCategoriesPage API integration", () => {
  beforeEach(() => {
    defaultHandlers()
  })

  it("creates a category through the API", async () => {
    const user = userEvent.setup()
    let requestBody: unknown
    server.use(http.post(`${BASE_URL}/api/v1/equipment-categories`, async ({ request }) => {
      requestBody = await request.json()
      return HttpResponse.json({ id: "category-new", name: "Pumpen" }, { status: 201 })
    }))

    renderWithProviders(<EquipmentCategoriesPage />)
    await screen.findByText("Atemschutz")
    await user.click(screen.getByRole("button", { name: /kategorie hinzufügen/i }))
    await user.type(await screen.findByLabelText("Name *"), "Pumpen")
    await user.click(screen.getByRole("button", { name: "Speichern" }))

    await waitFor(() => expect(requestBody).toEqual({ name: "Pumpen", description: undefined }))
  })
})