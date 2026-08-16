import { describe, it, expect, vi, beforeEach } from "vitest"
import { render, screen } from "@testing-library/react"
import * as routerModule from "react-router"
import EquipmentListPage from "../pages/equipment-list-page"

vi.mock("@/features/equipment/hooks/use-equipment-list", () => ({
  useEquipmentList: vi.fn(() => ({
    data: [
      {
        id: "1",
        name: "Gerät A",
        serialNumber: "SN-001",
        type: "Typ A",
        location: "Lager 1",
      },
    ],
    isLoading: false,
  })),
}))

vi.mock("@/features/equipment/hooks/use-delete-equipment", () => ({
  useDeleteEquipment: vi.fn(() => ({
    mutate: vi.fn(),
  })),
}))

vi.mock("react-router")

describe("EquipmentListPage", () => {
  const mockNavigate = vi.fn()

  beforeEach(() => {
    vi.clearAllMocks()
    vi.mocked(routerModule.useNavigate).mockReturnValue(mockNavigate)
  })

  const renderPage = () => {
    render(
      <MemoryRouter initialEntries={["/equipment"]}>
        <EquipmentListPage />
      </MemoryRouter>,
    )
  }

  it("renders page title and description", () => {
    renderPage()

    expect(screen.getByText("Geräte")).toBeInTheDocument()
    expect(screen.getByText("Verwalte Ausrüstung, Standorte, Status und Prüffristen.")).toBeInTheDocument()
  })

  it("renders 'Add Device' button", () => {
    renderPage()

    const addButton = screen.getByRole("button", { name: /Gerät hinzufügen/i })
    expect(addButton).toBeInTheDocument()
  })

  it("navigates to create page when add button is clicked", async () => {
    const user = await import("@testing-library/user-event")
    const userEvent = user.default

    renderPage()

    const addButton = screen.getByRole("button", { name: /Gerät hinzufügen/i })
    await userEvent.click(addButton)

    expect(mockNavigate).toHaveBeenCalledWith("/equipment/new")
  })

  it("renders filter inputs", () => {
    renderPage()

    expect(screen.getByPlaceholderText("Gerätename oder Seriennummer...")).toBeInTheDocument()
    expect(screen.getByPlaceholderText("Lagerort filtern...")).toBeInTheDocument()
  })

  it("renders equipment table headers", () => {
    renderPage()

    expect(screen.getByText("Name")).toBeInTheDocument()
    expect(screen.getByText("Seriennummer")).toBeInTheDocument()
    expect(screen.getByText("Typ")).toBeInTheDocument()
    expect(screen.getAllByText("Lagerort").length).toBeGreaterThan(0)
    expect(screen.getByText("Aktionen")).toBeInTheDocument()
  })

  it("renders filter labels", () => {
    renderPage()

    expect(screen.getByText("Suche (Name/Seriennummer)")).toBeInTheDocument()
    expect(screen.getAllByText("Lagerort").length).toBeGreaterThan(0)
  })
})

function MemoryRouter({ children, initialEntries }: { children: React.ReactNode; initialEntries: string[] }) {
  return <div>{children}</div>
}