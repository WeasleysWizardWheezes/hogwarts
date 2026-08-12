import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { MemberTable } from "./member-table"
import { useMembers } from "../hooks/use-members"
import { useLocations } from "@/features/locations/hooks/use-locations"

// Mock der Hooks
vi.mock("../hooks/use-members")
vi.mock("@/features/locations/hooks/use-locations")

describe("MemberTable", () => {
  const mockUseMembers = useMembers as vi.Mock
  const mockUseLocations = useLocations as vi.Mock

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it("sollte den Titel und den Standortfilter anzeigen", () => {
    mockUseMembers.mockReturnValue({
      isLoading: false,
      error: null,
      data: { data: [], page: { totalPages: 0 } }
    })

    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: null,
      data: { data: [] }
    })

    render(<MemberTable />)

    expect(screen.getByRole("heading", { name: "Mitglieder" })).toBeInTheDocument()
    expect(screen.getByRole("combobox", { name: "Alle Standorte" })).toBeInTheDocument()
  })

  it("sollte Ladezustand anzeigen, wenn Mitglieder geladen werden", () => {
    mockUseMembers.mockReturnValue({
      isLoading: true,
      error: null,
      data: null
    })

    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: null,
      data: { data: [] }
    })

    render(<MemberTable />)

    expect(screen.getByText("Mitglieder werden geladen...")).toBeInTheDocument()
  })

  it("sollte Fehler anzeigen, wenn Mitglieder nicht geladen werden können", () => {
    mockUseMembers.mockReturnValue({
      isLoading: false,
      error: { message: "Netzwerkfehler" },
      data: null
    })

    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: null,
      data: { data: [] }
    })

    render(<MemberTable />)

    expect(screen.getByText("Fehler beim Laden der Mitglieder: Netzwerkfehler")).toBeInTheDocument()
  })

  it("sollte Mitglieder in einer Tabelle anzeigen", () => {
    mockUseMembers.mockReturnValue({
      isLoading: false,
      error: null,
      data: {
        data: [
          {
            id: "member-1",
            firstName: "Harry",
            lastName: "Potter",
            email: "harry@example.com",
            location: { id: "location-1", name: "Hauptwache" }
          },
          {
            id: "member-2",
            firstName: "Hermine",
            lastName: "Granger",
            email: "hermine@example.com",
            location: null
          }
        ],
        page: { totalPages: 1 }
      }
    })

    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: null,
      data: { data: [] }
    })

    render(<MemberTable />)

    expect(screen.getByRole("cell", { name: "Harry Potter" })).toBeInTheDocument()
    expect(screen.getByRole("cell", { name: "harry@example.com" })).toBeInTheDocument()
    expect(screen.getByRole("cell", { name: "Hauptwache" })).toBeInTheDocument()
    expect(screen.getByRole("cell", { name: "Hermine Granger" })).toBeInTheDocument()
    expect(screen.getByRole("cell", { name: "hermine@example.com" })).toBeInTheDocument()
    expect(screen.getByRole("cell", { name: "-" })).toBeInTheDocument()
  })

  it("sollte Bearbeiten-Buttons für jedes Mitglied anzeigen", () => {
    mockUseMembers.mockReturnValue({
      isLoading: false,
      error: null,
      data: {
        data: [
          {
            id: "member-1",
            firstName: "Harry",
            lastName: "Potter",
            email: "harry@example.com",
            location: null
          }
        ],
        page: { totalPages: 1 }
      }
    })

    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: null,
      data: { data: [] }
    })

    render(<MemberTable />)

    expect(screen.getByRole("button", { name: "Bearbeiten" })).toBeInTheDocument()
  })

  it("sollte Standortfilter mit verfügbaren Standorten füllen", () => {
    mockUseMembers.mockReturnValue({
      isLoading: false,
      error: null,
      data: {
        data: [],
        page: { totalPages: 0 }
      }
    })

    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: null,
      data: {
        data: [
          { id: "location-1", name: "Hauptwache" },
          { id: "location-2", name: "Gerätedepot" }
        ]
      }
    })

    render(<MemberTable />)

    const select = screen.getByRole("combobox", { name: "Alle Standorte" })
    expect(select).toBeInTheDocument()
  })

  it("sollte den Standortfilter ändern können", async () => {
    const user = userEvent.setup()
    
    mockUseMembers.mockReturnValue({
      isLoading: false,
      error: null,
      data: {
        data: [],
        page: { totalPages: 0 }
      }
    })

    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: null,
      data: {
        data: [
          { id: "location-1", name: "Hauptwache" }
        ]
      }
    })

    render(<MemberTable />)

    const select = screen.getByRole("combobox", { name: "Alle Standorte" })
    await user.click(select)
    await user.click(screen.getByRole("option", { name: "Hauptwache" }))

    // Überprüfen, dass useMembers mit dem neuen locationId aufgerufen wurde
    expect(mockUseMembers).toHaveBeenCalledWith(0, 10, "location-1")
  })

  it("sollte Pagination anzeigen, wenn mehrere Seiten vorhanden sind", () => {
    mockUseMembers.mockReturnValue({
      isLoading: false,
      error: null,
      data: {
        data: [],
        page: { totalPages: 5 }
      }
    })

    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: null,
      data: { data: [] }
    })

    render(<MemberTable />)

    expect(screen.getByRole("navigation", { name: "Pagination" })).toBeInTheDocument()
  })

  it("sollte leere Tabelle anzeigen, wenn keine Mitglieder vorhanden sind", () => {
    mockUseMembers.mockReturnValue({
      isLoading: false,
      error: null,
      data: {
        data: [],
        page: { totalPages: 0 }
      }
    })

    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: null,
      data: { data: [] }
    })

    render(<MemberTable />)

    expect(screen.queryByRole("row")).not.toBeInTheDocument()
  })
})
