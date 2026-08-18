import { screen, waitFor, within } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { describe, it, expect, vi, beforeEach } from "vitest"
import { render } from "@/test/render"
import EquipmentCategoriesPage from "../pages/equipment-categories-page"

// Mock API hooks – this is a component test, not an API integration test
vi.mock("../api/equipment-categories-api", () => ({
  useEquipmentCategories: vi.fn(),
  useCreateEquipmentCategory: vi.fn(),
  useUpdateEquipmentCategory: vi.fn(),
  useArchiveEquipmentCategory: vi.fn(),
}))

import {
  useEquipmentCategories,
  useCreateEquipmentCategory,
  useUpdateEquipmentCategory,
  useArchiveEquipmentCategory,
} from "../api/equipment-categories-api"

const mockUseEquipmentCategories = vi.mocked(useEquipmentCategories)
const mockUseCreateEquipmentCategory = vi.mocked(useCreateEquipmentCategory)
const mockUseUpdateEquipmentCategory = vi.mocked(useUpdateEquipmentCategory)
const mockUseArchiveEquipmentCategory = vi.mocked(useArchiveEquipmentCategory)

const mockCategories = [
  { id: "cat-atemschutz-01", name: "Atemschutz", description: "Atemschutzgeräte" },
  { id: "cat-funk-01", name: "Funk", description: undefined },
]

const idleMutation = { mutateAsync: vi.fn(), isPending: false }

function setupMocks(overrides?: Partial<ReturnType<typeof useEquipmentCategories>>) {
  mockUseEquipmentCategories.mockReturnValue({
    data: { data: mockCategories },
    isLoading: false,
    isError: false,
    ...overrides,
  } as unknown as ReturnType<typeof useEquipmentCategories>)
  mockUseCreateEquipmentCategory.mockReturnValue(
    idleMutation as unknown as ReturnType<typeof useCreateEquipmentCategory>,
  )
  mockUseUpdateEquipmentCategory.mockReturnValue(
    idleMutation as unknown as ReturnType<typeof useUpdateEquipmentCategory>,
  )
  mockUseArchiveEquipmentCategory.mockReturnValue(
    idleMutation as unknown as ReturnType<typeof useArchiveEquipmentCategory>,
  )
}

describe("EquipmentCategoriesPage", () => {
  beforeEach(() => {
    vi.clearAllMocks()
    setupMocks()
  })

  it("shows the page heading", () => {
    render(<EquipmentCategoriesPage />)
    expect(screen.getByRole("heading", { name: "Gerätekategorien" })).toBeInTheDocument()
  })

  it("shows the create button", () => {
    render(<EquipmentCategoriesPage />)
    expect(
      screen.getByRole("button", { name: /Kategorie erstellen/i }),
    ).toBeInTheDocument()
  })

  it("shows loading state", () => {
    setupMocks({ data: undefined, isLoading: true })
    render(<EquipmentCategoriesPage />)
    // Skeleton renders, no table content yet
    expect(screen.queryByText("Atemschutz")).not.toBeInTheDocument()
  })

  it("shows error state", () => {
    setupMocks({ data: undefined, isError: true })
    render(<EquipmentCategoriesPage />)
    expect(
      screen.getByText(/konnten nicht geladen werden/i),
    ).toBeInTheDocument()
  })

  it("shows empty state when no categories exist", () => {
    setupMocks({ data: { data: [] } })
    render(<EquipmentCategoriesPage />)
    expect(screen.getByText(/Keine Kategorien vorhanden/i)).toBeInTheDocument()
  })

  it("renders category list with name and description", () => {
    render(<EquipmentCategoriesPage />)
    expect(screen.getByText("Atemschutz")).toBeInTheDocument()
    expect(screen.getByText("Atemschutzgeräte")).toBeInTheDocument()
    expect(screen.getByText("Funk")).toBeInTheDocument()
  })

  it("shows edit and archive buttons per row", () => {
    render(<EquipmentCategoriesPage />)
    const row = screen.getByRole("row", { name: /Atemschutz/i })
    expect(
      within(row).getByRole("button", { name: /Atemschutz bearbeiten/i }),
    ).toBeInTheDocument()
    expect(
      within(row).getByRole("button", { name: /Atemschutz archivieren/i }),
    ).toBeInTheDocument()
  })

  it("opens create dialog when create button is clicked", async () => {
    const user = userEvent.setup()
    render(<EquipmentCategoriesPage />)

    await user.click(screen.getByRole("button", { name: /Kategorie erstellen/i }))

    await waitFor(() => {
      expect(
        screen.getByRole("heading", { name: "Kategorie erstellen" }),
      ).toBeInTheDocument()
    })
  })

  it("disables submit button when name is empty in create dialog", async () => {
    const user = userEvent.setup()
    render(<EquipmentCategoriesPage />)

    await user.click(screen.getByRole("button", { name: /Kategorie erstellen/i }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Kategorie erstellen" })).toBeInTheDocument()
    })

    expect(screen.getByRole("button", { name: "Erstellen" })).toBeDisabled()
  })

  it("enables submit button when name is filled", async () => {
    const user = userEvent.setup()
    render(<EquipmentCategoriesPage />)

    await user.click(screen.getByRole("button", { name: /Kategorie erstellen/i }))

    await waitFor(() => {
      expect(screen.getByLabelText("Name *")).toBeInTheDocument()
    })

    await user.type(screen.getByLabelText("Name *"), "Hydraulik")

    expect(screen.getByRole("button", { name: "Erstellen" })).not.toBeDisabled()
  })

  it("prefills form when edit button is clicked", async () => {
    const user = userEvent.setup()
    render(<EquipmentCategoriesPage />)

    const row = screen.getByRole("row", { name: /Atemschutz/i })
    await user.click(within(row).getByRole("button", { name: /Atemschutz bearbeiten/i }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Kategorie bearbeiten" })).toBeInTheDocument()
    })

    expect(screen.getByLabelText("Name *")).toHaveValue("Atemschutz")
    expect(screen.getByLabelText("Beschreibung")).toHaveValue("Atemschutzgeräte")
  })

  it("opens archive confirmation dialog", async () => {
    const user = userEvent.setup()
    render(<EquipmentCategoriesPage />)

    const row = screen.getByRole("row", { name: /Atemschutz/i })
    await user.click(within(row).getByRole("button", { name: /Atemschutz archivieren/i }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Kategorie archivieren" })).toBeInTheDocument()
    })
    expect(screen.getByText(/Atemschutz/)).toBeInTheDocument()
    expect(screen.getByText(/archivieren/i)).toBeInTheDocument()
  })

  it("closes archive dialog when cancel is clicked", async () => {
    const user = userEvent.setup()
    render(<EquipmentCategoriesPage />)

    const row = screen.getByRole("row", { name: /Atemschutz/i })
    await user.click(within(row).getByRole("button", { name: /Atemschutz archivieren/i }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Kategorie archivieren" })).toBeInTheDocument()
    })

    await user.click(screen.getByRole("button", { name: "Abbrechen" }))

    await waitFor(() => {
      expect(
        screen.queryByRole("heading", { name: "Kategorie archivieren" }),
      ).not.toBeInTheDocument()
    })
  })
})
