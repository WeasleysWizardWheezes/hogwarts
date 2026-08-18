import { screen, waitFor, within } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { describe, it, expect, vi, beforeEach } from "vitest"
import { render } from "@/test/render"
import EquipmentDetailPage from "../pages/equipment-detail-page"

vi.mock("../api/equipment-api", () => ({
  useEquipment: vi.fn(),
  useEquipmentHistory: vi.fn(),
  useUpdateEquipment: vi.fn(),
  useArchiveEquipment: vi.fn(),
}))

vi.mock("../api/equipment-categories-api", () => ({
  useEquipmentCategories: vi.fn(),
}))

vi.mock("@/features/vehicles", () => ({
  useVehicles: vi.fn(),
}))

const mockNavigate = vi.fn()
vi.mock("react-router", async () => {
  const actual = await vi.importActual<typeof import("react-router")>("react-router")
  return {
    ...actual,
    useParams: () => ({ id: "equip-pa300-01" }),
    useNavigate: () => mockNavigate,
  }
})

import {
  useEquipment,
  useEquipmentHistory,
  useUpdateEquipment,
  useArchiveEquipment,
} from "../api/equipment-api"
import { useEquipmentCategories } from "../api/equipment-categories-api"
import { useVehicles } from "@/features/vehicles"

const mockUseEquipment = vi.mocked(useEquipment)
const mockUseEquipmentHistory = vi.mocked(useEquipmentHistory)
const mockUseUpdateEquipment = vi.mocked(useUpdateEquipment)
const mockUseArchiveEquipment = vi.mocked(useArchiveEquipment)
const mockUseEquipmentCategories = vi.mocked(useEquipmentCategories)
const mockUseVehicles = vi.mocked(useVehicles)

const mockEquipment = {
  id: "equip-pa300-01",
  name: "Pressluftatmer PA 300",
  inventoryNumber: "AGT-2024-0042",
  description: "Atemschutzgerät für Einsätze",
  categoryId: "cat-atemschutz-01",
  categoryName: "Atemschutz",
  vehicleId: "veh-hlf20-01",
  vehicleName: "01-HLF20-01",
  status: "VERFUEGBAR" as const,
  nextInspectionDate: undefined,
  nextMaintenanceDate: undefined,
}

const mockEquipmentOverdue = {
  ...mockEquipment,
  id: "equip-funk-03",
  name: "Funkgerät 3",
  inventoryNumber: "FUNK-2023-003",
  nextInspectionDate: "2025-06-01", // past date → overdue
}

const mockHistoryItems = [
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

const idleMutation = { mutateAsync: vi.fn(), isPending: false }

function setupMocks(
  equipmentOverride?: Partial<ReturnType<typeof useEquipment>>,
  historyOverride?: Partial<ReturnType<typeof useEquipmentHistory>>,
) {
  mockUseEquipment.mockReturnValue({
    data: mockEquipment,
    isLoading: false,
    isError: false,
    ...equipmentOverride,
  } as unknown as ReturnType<typeof useEquipment>)
  mockUseEquipmentHistory.mockReturnValue({
    data: mockHistoryItems,
    isLoading: false,
    ...historyOverride,
  } as unknown as ReturnType<typeof useEquipmentHistory>)
  mockUseEquipmentCategories.mockReturnValue({
    data: { data: [{ id: "cat-atemschutz-01", name: "Atemschutz" }] },
    isLoading: false,
    isError: false,
  } as unknown as ReturnType<typeof useEquipmentCategories>)
  mockUseVehicles.mockReturnValue({
    data: { data: [{ id: "veh-hlf20-01", name: "01-HLF20-01" }] },
    isLoading: false,
    isError: false,
  } as unknown as ReturnType<typeof useVehicles>)
  mockUseUpdateEquipment.mockReturnValue(
    idleMutation as unknown as ReturnType<typeof useUpdateEquipment>,
  )
  mockUseArchiveEquipment.mockReturnValue(
    idleMutation as unknown as ReturnType<typeof useArchiveEquipment>,
  )
}

describe("EquipmentDetailPage", () => {
  beforeEach(() => {
    vi.clearAllMocks()
    setupMocks()
  })

  it("shows loading state without content", () => {
    setupMocks({ data: undefined, isLoading: true })
    render(<EquipmentDetailPage />)
    expect(screen.queryByText("Pressluftatmer PA 300")).not.toBeInTheDocument()
  })

  it("shows error state", () => {
    setupMocks({ data: undefined, isError: true })
    render(<EquipmentDetailPage />)
    expect(screen.getByText(/konnte nicht geladen werden/i)).toBeInTheDocument()
  })

  it("shows equipment name and inventory number", () => {
    render(<EquipmentDetailPage />)
    expect(screen.getByRole("heading", { name: "Pressluftatmer PA 300" })).toBeInTheDocument()
    expect(screen.getByText("AGT-2024-0042")).toBeInTheDocument()
  })

  it("shows status, category and vehicle", () => {
    render(<EquipmentDetailPage />)
    expect(screen.getAllByText("Verfügbar").length).toBeGreaterThanOrEqual(1)
    expect(screen.getAllByText("Atemschutz").length).toBeGreaterThanOrEqual(1)
    expect(screen.getByText("01-HLF20-01")).toBeInTheDocument()
  })

  it("shows description when present", () => {
    render(<EquipmentDetailPage />)
    expect(screen.getByText("Atemschutzgerät für Einsätze")).toBeInTheDocument()
  })

  it("shows change history with status transitions", () => {
    render(<EquipmentDetailPage />)
    expect(screen.getByRole("heading", { name: "Änderungshistorie" })).toBeInTheDocument()
    expect(screen.getAllByText("Verfügbar").length).toBeGreaterThanOrEqual(1)
    expect(screen.getAllByText("Wartung").length).toBeGreaterThanOrEqual(1)
  })

  it("shows empty history message when no history exists", () => {
    setupMocks(undefined, { data: [] })
    render(<EquipmentDetailPage />)
    expect(screen.getByText(/Keine Änderungen vorhanden/i)).toBeInTheDocument()
  })

  it("highlights overdue inspection date in red", () => {
    mockUseEquipment.mockReturnValue({
      data: mockEquipmentOverdue,
      isLoading: false,
      isError: false,
    } as unknown as ReturnType<typeof useEquipment>)

    render(<EquipmentDetailPage />)

    const overdueText = screen.getByText(/1\.6\.2025/)
    expect(overdueText).toHaveClass("text-destructive")
    expect(overdueText).toHaveTextContent(/überfällig/i)
  })

  it("shows edit and archive buttons", () => {
    render(<EquipmentDetailPage />)
    expect(screen.getByRole("button", { name: "Gerät bearbeiten" })).toBeInTheDocument()
    expect(screen.getByRole("button", { name: "Gerät archivieren" })).toBeInTheDocument()
  })

  it("opens edit dialog with prefilled values", async () => {
    const user = userEvent.setup()
    render(<EquipmentDetailPage />)

    await user.click(screen.getByRole("button", { name: "Gerät bearbeiten" }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Gerät bearbeiten" })).toBeInTheDocument()
    })

    expect(screen.getByLabelText("Name *")).toHaveValue("Pressluftatmer PA 300")
    expect(screen.getByLabelText("Inventarnummer *")).toHaveValue("AGT-2024-0042")
  })

  it("disables save button when required fields are cleared", async () => {
    const user = userEvent.setup()
    render(<EquipmentDetailPage />)

    await user.click(screen.getByRole("button", { name: "Gerät bearbeiten" }))

    await waitFor(() => {
      expect(screen.getByLabelText("Name *")).toBeInTheDocument()
    })

    await user.clear(screen.getByLabelText("Name *"))

    expect(screen.getByRole("button", { name: "Speichern" })).toBeDisabled()
  })

  it("opens archive confirmation dialog with equipment name", async () => {
    const user = userEvent.setup()
    render(<EquipmentDetailPage />)

    await user.click(screen.getByRole("button", { name: "Gerät archivieren" }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Gerät archivieren" })).toBeInTheDocument()
    })
    const dialog = screen.getByRole("alertdialog")
    expect(within(dialog).getByText(/Pressluftatmer PA 300/)).toBeInTheDocument()
  })

  it("closes archive dialog when cancel is clicked", async () => {
    const user = userEvent.setup()
    render(<EquipmentDetailPage />)

    await user.click(screen.getByRole("button", { name: "Gerät archivieren" }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Gerät archivieren" })).toBeInTheDocument()
    })

    await user.click(screen.getByRole("button", { name: "Abbrechen" }))

    await waitFor(() => {
      expect(
        screen.queryByRole("heading", { name: "Gerät archivieren" }),
      ).not.toBeInTheDocument()
    })
  })

  it("navigates back to equipment list when back button is clicked", async () => {
    const user = userEvent.setup()
    render(<EquipmentDetailPage />)

    await user.click(screen.getByRole("button", { name: "Zurück zur Geräteliste" }))

    expect(mockNavigate).toHaveBeenCalledWith("/equipment")
  })

  it("shows history loading state", () => {
    setupMocks(undefined, { data: undefined, isLoading: true })
    render(<EquipmentDetailPage />)
    // history section exists but no table rows yet
    expect(screen.getByRole("heading", { name: "Änderungshistorie" })).toBeInTheDocument()
    expect(screen.queryByRole("row")).not.toBeInTheDocument()
  })
})
