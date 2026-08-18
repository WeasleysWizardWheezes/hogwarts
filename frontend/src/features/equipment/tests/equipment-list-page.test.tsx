import { screen, waitFor, within } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { describe, it, expect, vi, beforeEach } from "vitest"
import { render } from "@/test/render"
import EquipmentListPage from "../pages/equipment-list-page"

// Mock API hooks – component test only; API integration is covered separately
vi.mock("../api/equipment-api", () => ({
  useEquipmentList: vi.fn(),
  useCreateEquipment: vi.fn(),
  useUpdateEquipment: vi.fn(),
  useArchiveEquipment: vi.fn(),
}))

vi.mock("../api/equipment-categories-api", () => ({
  useEquipmentCategories: vi.fn(),
}))

vi.mock("@/features/vehicles", () => ({
  useVehicles: vi.fn(),
}))

vi.mock("react-router", async () => {
  const actual = await vi.importActual<typeof import("react-router")>("react-router")
  return {
    ...actual,
    useNavigate: () => vi.fn(),
  }
})

import {
  useEquipmentList,
  useCreateEquipment,
  useUpdateEquipment,
  useArchiveEquipment,
} from "../api/equipment-api"
import { useEquipmentCategories } from "../api/equipment-categories-api"
import { useVehicles } from "@/features/vehicles"

const mockUseEquipmentList = vi.mocked(useEquipmentList)
const mockUseCreateEquipment = vi.mocked(useCreateEquipment)
const mockUseUpdateEquipment = vi.mocked(useUpdateEquipment)
const mockUseArchiveEquipment = vi.mocked(useArchiveEquipment)
const mockUseEquipmentCategories = vi.mocked(useEquipmentCategories)
const mockUseVehicles = vi.mocked(useVehicles)

const mockItems = [
  {
    id: "equip-pa300-01",
    name: "Pressluftatmer PA 300",
    inventoryNumber: "AGT-2024-0042",
    categoryId: "cat-atemschutz-01",
    categoryName: "Atemschutz",
    vehicleId: "veh-hlf20-01",
    vehicleName: "01-HLF20-01",
    status: "VERFUEGBAR" as const,
    nextInspectionDate: undefined,
  },
  {
    id: "equip-funk-03",
    name: "Funkgerät 3",
    inventoryNumber: "FUNK-2023-003",
    categoryId: "cat-funk-01",
    categoryName: "Funk",
    status: "DEFEKT" as const,
    nextInspectionDate: "2025-06-01", // deliberately past → overdue
  },
]

const mockCategories = [
  { id: "cat-atemschutz-01", name: "Atemschutz" },
  { id: "cat-funk-01", name: "Funk" },
]

const mockVehicles = [{ id: "veh-hlf20-01", name: "01-HLF20-01" }]

const idleMutation = { mutateAsync: vi.fn(), isPending: false }

function setupMocks(overrides?: Partial<ReturnType<typeof useEquipmentList>>) {
  mockUseEquipmentList.mockReturnValue({
    data: { data: mockItems },
    isLoading: false,
    isError: false,
    ...overrides,
  } as unknown as ReturnType<typeof useEquipmentList>)
  mockUseEquipmentCategories.mockReturnValue({
    data: { data: mockCategories },
    isLoading: false,
    isError: false,
  } as unknown as ReturnType<typeof useEquipmentCategories>)
  mockUseVehicles.mockReturnValue({
    data: { data: mockVehicles },
    isLoading: false,
    isError: false,
  } as unknown as ReturnType<typeof useVehicles>)
  mockUseCreateEquipment.mockReturnValue(
    idleMutation as unknown as ReturnType<typeof useCreateEquipment>,
  )
  mockUseUpdateEquipment.mockReturnValue(
    idleMutation as unknown as ReturnType<typeof useUpdateEquipment>,
  )
  mockUseArchiveEquipment.mockReturnValue(
    idleMutation as unknown as ReturnType<typeof useArchiveEquipment>,
  )
}

describe("EquipmentListPage", () => {
  beforeEach(() => {
    vi.clearAllMocks()
    setupMocks()
  })

  it("shows the page heading", () => {
    render(<EquipmentListPage />)
    expect(screen.getByRole("heading", { name: "Geräte" })).toBeInTheDocument()
  })

  it("shows the create button", () => {
    render(<EquipmentListPage />)
    expect(screen.getByRole("button", { name: /Gerät erstellen/i })).toBeInTheDocument()
  })

  it("shows filter inputs for search, category, vehicle and status", () => {
    render(<EquipmentListPage />)
    expect(screen.getByRole("textbox", { name: /Geräte suchen/i })).toBeInTheDocument()
    expect(screen.getByText("Alle Kategorien")).toBeInTheDocument()
    expect(screen.getByText("Alle Fahrzeuge")).toBeInTheDocument()
    expect(screen.getByText("Alle Status")).toBeInTheDocument()
  })

  it("shows loading state without table content", () => {
    setupMocks({ data: undefined, isLoading: true })
    render(<EquipmentListPage />)
    expect(screen.queryByText("Pressluftatmer PA 300")).not.toBeInTheDocument()
  })

  it("shows error state", () => {
    setupMocks({ data: undefined, isError: true })
    render(<EquipmentListPage />)
    expect(screen.getByText(/konnten nicht geladen werden/i)).toBeInTheDocument()
  })

  it("shows empty state when no items exist", () => {
    setupMocks({ data: { data: [] } })
    render(<EquipmentListPage />)
    expect(screen.getByText(/Keine Geräte gefunden/i)).toBeInTheDocument()
  })

  it("renders equipment list with name, inventory number, category and status", () => {
    render(<EquipmentListPage />)
    expect(screen.getByText("Pressluftatmer PA 300")).toBeInTheDocument()
    expect(screen.getByText("AGT-2024-0042")).toBeInTheDocument()
    expect(screen.getAllByText("Atemschutz")[0]).toBeInTheDocument()
    expect(screen.getByText("Verfügbar")).toBeInTheDocument()
    expect(screen.getByText("Funkgerät 3")).toBeInTheDocument()
    expect(screen.getByText("Defekt")).toBeInTheDocument()
  })

  it("highlights overdue inspection date visually", () => {
    render(<EquipmentListPage />)
    // The overdue date cell should have destructive styling (text-destructive)
    const overdueCell = screen.getByText(/1\.6\.2025/)
    expect(overdueCell).toHaveClass("text-destructive")
  })

  it("opens create dialog with empty form", async () => {
    const user = userEvent.setup()
    render(<EquipmentListPage />)

    await user.click(screen.getByRole("button", { name: /Gerät erstellen/i }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Gerät erstellen" })).toBeInTheDocument()
    })
  })

  it("disables submit when required fields are empty in create dialog", async () => {
    const user = userEvent.setup()
    render(<EquipmentListPage />)

    await user.click(screen.getByRole("button", { name: /Gerät erstellen/i }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Gerät erstellen" })).toBeInTheDocument()
    })

    expect(screen.getByRole("button", { name: "Erstellen" })).toBeDisabled()
  })

  it("enables submit when all required fields are filled", async () => {
    const user = userEvent.setup()
    render(<EquipmentListPage />)

    await user.click(screen.getByRole("button", { name: /Gerät erstellen/i }))

    await waitFor(() => {
      expect(screen.getByLabelText("Name *")).toBeInTheDocument()
    })

    await user.type(screen.getByLabelText("Name *"), "Wärmebildkamera")
    await user.type(screen.getByLabelText("Inventarnummer *"), "WBK-2026-001")

    // Select category
    await user.click(screen.getByRole("combobox", { name: /Kategorie/i }))
    await user.click(await screen.findByRole("option", { name: "Atemschutz" }))

    expect(screen.getByRole("button", { name: "Erstellen" })).not.toBeDisabled()
  })

  it("prefills form when edit button is clicked", async () => {
    const user = userEvent.setup()
    render(<EquipmentListPage />)

    const row = screen.getByRole("row", { name: /Pressluftatmer PA 300/i })
    await user.click(
      within(row).getByRole("button", { name: /Pressluftatmer PA 300 bearbeiten/i }),
    )

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Gerät bearbeiten" })).toBeInTheDocument()
    })

    expect(screen.getByLabelText("Name *")).toHaveValue("Pressluftatmer PA 300")
    expect(screen.getByLabelText("Inventarnummer *")).toHaveValue("AGT-2024-0042")
  })

  it("opens archive confirmation dialog with equipment name", async () => {
    const user = userEvent.setup()
    render(<EquipmentListPage />)

    const row = screen.getByRole("row", { name: /Funkgerät 3/i })
    await user.click(within(row).getByRole("button", { name: /Funkgerät 3 archivieren/i }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Gerät archivieren" })).toBeInTheDocument()
    })
    const dialog = screen.getByRole("alertdialog")
    expect(within(dialog).getByText(/Funkgerät 3/)).toBeInTheDocument()
  })

  it("closes archive dialog when cancel is clicked", async () => {
    const user = userEvent.setup()
    render(<EquipmentListPage />)

    const row = screen.getByRole("row", { name: /Funkgerät 3/i })
    await user.click(within(row).getByRole("button", { name: /Funkgerät 3 archivieren/i }))

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
})
