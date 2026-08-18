import { screen, waitFor } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { describe, it, expect, vi, beforeEach } from "vitest"
import { renderWithProviders, server, http, HttpResponse } from "@/test"

vi.mock("@/shared/api/client", async () => {
  const { default: createClient } = await import("openapi-fetch")
  return {
    api: createClient({
      baseUrl: import.meta.env.VITE_API_URL || "http://localhost:3000",
      fetch: (...args: Parameters<typeof globalThis.fetch>) => globalThis.fetch(...args),
    }),
  }
})

const mockNavigate = vi.fn()
vi.mock("react-router", async () => {
  const actual = await vi.importActual<typeof import("react-router")>("react-router")
  return {
    ...actual,
    useParams: () => ({ id: "equip-pa300-01" }),
    useNavigate: () => mockNavigate,
  }
})

import EquipmentDetailPage from "../pages/equipment-detail-page"

const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:3000"

const mockEquipment = {
  id: "equip-pa300-01",
  name: "Pressluftatmer PA 300",
  inventoryNumber: "AGT-2024-0042",
  description: "Atemschutzgerät für Einsätze",
  categoryId: "cat-atemschutz-01",
  categoryName: "Atemschutz",
  vehicleId: "veh-hlf20-01",
  vehicleName: "01-HLF20-01",
  status: "VERFUEGBAR",
  nextInspectionDate: null,
  nextMaintenanceDate: null,
}

const mockHistory = [
  {
    id: "history-001",
    changedAt: "2026-03-10T14:30:00Z",
    previousStatus: "VERFUEGBAR",
    newStatus: "WARTUNG",
  },
  {
    id: "history-002",
    changedAt: "2026-04-15T09:00:00Z",
    previousStatus: "WARTUNG",
    newStatus: "VERFUEGBAR",
  },
]

const mockCategories = [{ id: "cat-atemschutz-01", name: "Atemschutz" }]
const mockVehicles = [{ id: "veh-hlf20-01", name: "01-HLF20-01" }]

function setupDefaultHandlers() {
  server.use(
    http.get(`${BASE_URL}/api/v1/equipment/equip-pa300-01`, () => {
      return HttpResponse.json(mockEquipment)
    }),
    http.get(`${BASE_URL}/api/v1/equipment/equip-pa300-01/history`, () => {
      return HttpResponse.json(mockHistory)
    }),
    http.get(`${BASE_URL}/api/v1/equipment-categories`, () => {
      return HttpResponse.json({
        data: mockCategories,
        page: { page: 0, size: 100, totalElements: 1, totalPages: 1 },
      })
    }),
    http.get(`${BASE_URL}/api/v1/vehicles`, () => {
      return HttpResponse.json({
        data: mockVehicles,
        page: { page: 0, size: 20, totalElements: 1, totalPages: 1 },
      })
    }),
  )
}

describe("EquipmentDetailPage API Integration", () => {
  beforeEach(() => {
    vi.clearAllMocks()
    setupDefaultHandlers()
  })

  it("shows loading state initially", () => {
    renderWithProviders(<EquipmentDetailPage />)
    expect(screen.queryByText("Pressluftatmer PA 300")).not.toBeInTheDocument()
  })

  it("shows error state on API failure", async () => {
    server.use(
      http.get(`${BASE_URL}/api/v1/equipment/equip-pa300-01`, () => {
        return HttpResponse.json({ message: "Not found" }, { status: 404 })
      }),
    )
    renderWithProviders(<EquipmentDetailPage />)
    expect(await screen.findByText(/konnte nicht geladen werden/i)).toBeInTheDocument()
  })

  it("renders equipment details after successful fetch", async () => {
    renderWithProviders(<EquipmentDetailPage />)
    expect(await screen.findByRole("heading", { name: "Pressluftatmer PA 300" })).toBeInTheDocument()
    expect(screen.getByText("AGT-2024-0042")).toBeInTheDocument()
    expect(screen.getAllByText("Verfügbar").length).toBeGreaterThanOrEqual(1)
    expect(screen.getAllByText("Atemschutz").length).toBeGreaterThanOrEqual(1)
    expect(screen.getByText("01-HLF20-01")).toBeInTheDocument()
    expect(screen.getByText("Atemschutzgerät für Einsätze")).toBeInTheDocument()
  })

  it("renders change history after successful fetch", async () => {
    renderWithProviders(<EquipmentDetailPage />)
    expect(await screen.findByRole("heading", { name: "Änderungshistorie" })).toBeInTheDocument()
    expect(screen.getAllByText("Wartung").length).toBeGreaterThanOrEqual(1)
  })

  it("shows empty history message when no history exists", async () => {
    server.use(
      http.get(`${BASE_URL}/api/v1/equipment/equip-pa300-01/history`, () => {
        return HttpResponse.json([])
      }),
    )
    renderWithProviders(<EquipmentDetailPage />)
    expect(await screen.findByText(/Keine Änderungen vorhanden/i)).toBeInTheDocument()
  })

  it("highlights overdue inspection date visually", async () => {
    server.use(
      http.get(`${BASE_URL}/api/v1/equipment/equip-pa300-01`, () => {
        return HttpResponse.json({
          ...mockEquipment,
          nextInspectionDate: "2025-06-01",
        })
      }),
    )
    renderWithProviders(<EquipmentDetailPage />)
    await screen.findByRole("heading", { name: "Pressluftatmer PA 300" })

    const overdueSpan = screen.getByText(/1\.6\.2025/)
    expect(overdueSpan).toHaveClass("text-destructive")
    expect(overdueSpan).toHaveTextContent(/überfällig/i)
  })

  it("updates equipment via API and closes the dialog", async () => {
    const user = userEvent.setup()
    let requestBody: unknown = null

    server.use(
      http.put(`${BASE_URL}/api/v1/equipment/equip-pa300-01`, async ({ request }) => {
        requestBody = await request.json()
        return HttpResponse.json({ ...mockEquipment, name: "Pressluftatmer PA 300 revidiert" })
      }),
    )

    renderWithProviders(<EquipmentDetailPage />)
    expect(await screen.findByRole("button", { name: "Gerät bearbeiten" })).toBeInTheDocument()

    await user.click(screen.getByRole("button", { name: "Gerät bearbeiten" }))

    await waitFor(() => {
      expect(screen.getByLabelText("Name *")).toBeInTheDocument()
    })

    const nameInput = screen.getByLabelText("Name *")
    await user.clear(nameInput)
    await user.type(nameInput, "Pressluftatmer PA 300 revidiert")

    await user.click(screen.getByRole("button", { name: "Speichern" }))

    await waitFor(() => {
      expect(requestBody).toMatchObject({ name: "Pressluftatmer PA 300 revidiert" })
    })

    await waitFor(() => {
      expect(screen.queryByRole("heading", { name: "Gerät bearbeiten" })).not.toBeInTheDocument()
    })
  })

  it("shows HTTP 409 toast error on duplicate inventory number when editing", async () => {
    const user = userEvent.setup()

    // Suppress the unhandled rejection from mutateAsync throwing on 409
    const rejectionHandler = () => { /* swallow */ }
    process.on("unhandledRejection", rejectionHandler)

    server.use(
      http.put(`${BASE_URL}/api/v1/equipment/equip-pa300-01`, () => {
        return HttpResponse.json({ message: "Inventory number already exists" }, { status: 409 })
      }),
    )

    renderWithProviders(<EquipmentDetailPage />)
    expect(await screen.findByRole("button", { name: "Gerät bearbeiten" })).toBeInTheDocument()

    await user.click(screen.getByRole("button", { name: "Gerät bearbeiten" }))

    await waitFor(() => {
      expect(screen.getByLabelText("Inventarnummer *")).toBeInTheDocument()
    })

    const invInput = screen.getByLabelText("Inventarnummer *")
    await user.clear(invInput)
    await user.type(invInput, "FUNK-2023-003")

    await user.click(screen.getByRole("button", { name: "Speichern" }))

    // onError → toast.error; dialog stays open for retry
    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Gerät bearbeiten" })).toBeInTheDocument()
    })

    process.off("unhandledRejection", rejectionHandler)
  })

  it("archives equipment via API and navigates back to list", async () => {
    const user = userEvent.setup()
    let deletedId: string | null = null

    server.use(
      http.delete(`${BASE_URL}/api/v1/equipment/:equipmentId`, ({ params }) => {
        deletedId = params.equipmentId as string
        return new HttpResponse(null, { status: 204 })
      }),
    )

    renderWithProviders(<EquipmentDetailPage />)
    expect(await screen.findByRole("button", { name: "Gerät archivieren" })).toBeInTheDocument()

    await user.click(screen.getByRole("button", { name: "Gerät archivieren" }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Gerät archivieren" })).toBeInTheDocument()
    })

    await user.click(screen.getByRole("button", { name: "Archivieren" }))

    await waitFor(() => {
      expect(deletedId).toBe("equip-pa300-01")
    })

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith("/equipment")
    })
  })
})
