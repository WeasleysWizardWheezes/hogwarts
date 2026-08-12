import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { MemberLocationAssignment } from "./member-location-assignment"
import { useLocations } from "../hooks/use-locations"
import { useAssignMemberToLocation } from "../hooks/use-assign-member-to-location"

// Mock der Hooks
vi.mock("../hooks/use-locations")
vi.mock("../hooks/use-assign-member-to-location")

describe("MemberLocationAssignment", () => {
  const mockUseLocations = useLocations as jest.Mock
  const mockUseAssignMemberToLocation = useAssignMemberToLocation as jest.Mock

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it("sollte eine Nachricht anzeigen, wenn memberId nicht vorhanden ist", () => {
    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: null,
      data: { data: [] }
    })

    render(<MemberLocationAssignment memberId={undefined} />)

    expect(screen.getByText("Standortzuordnung verfügbar nach dem Speichern des Mitglieds.")).toBeInTheDocument()
  })

  it("sollte Ladezustand anzeigen, wenn Standorte geladen werden", () => {
    mockUseLocations.mockReturnValue({
      isLoading: true,
      error: null,
      data: null
    })

    render(<MemberLocationAssignment memberId="member-1" />)

    expect(screen.getByText("Standorte werden geladen...")).toBeInTheDocument()
  })

  it("sollte Fehler anzeigen, wenn Standorte nicht geladen werden können", () => {
    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: { message: "Netzwerkfehler" },
      data: null
    })

    render(<MemberLocationAssignment memberId="member-1" />)

    expect(screen.getByText("Fehler beim Laden der Standorte")).toBeInTheDocument()
  })

  it("sollte Standortauswahl und Zuordnen-Button anzeigen, wenn Standorte verfügbar sind", () => {
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

    mockUseAssignMemberToLocation.mockReturnValue({
      mutate: vi.fn(),
      isPending: false
    })

    render(<MemberLocationAssignment memberId="member-1" />)

    expect(screen.getByRole("combobox", { name: "Standort auswählen" })).toBeInTheDocument()
    expect(screen.getByRole("button", { name: "Zuordnen" })).toBeInTheDocument()
  })

  it("sollte den Zuordnen-Button deaktivieren, wenn kein Standort ausgewählt ist", () => {
    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: null,
      data: {
        data: [
          { id: "location-1", name: "Hauptwache" }
        ]
      }
    })

    mockUseAssignMemberToLocation.mockReturnValue({
      mutate: vi.fn(),
      isPending: false
    })

    render(<MemberLocationAssignment memberId="member-1" />)

    expect(screen.getByRole("button", { name: "Zuordnen" })).toBeDisabled()
  })

  it("sollte den Zuordnen-Button aktivieren, wenn ein Standort ausgewählt ist", async () => {
    const user = userEvent.setup()
    
    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: null,
      data: {
        data: [
          { id: "location-1", name: "Hauptwache" }
        ]
      }
    })

    const mutateMock = vi.fn()
    mockUseAssignMemberToLocation.mockReturnValue({
      mutate: mutateMock,
      isPending: false
    })

    render(<MemberLocationAssignment memberId="member-1" />)

    const select = screen.getByRole("combobox", { name: "Standort auswählen" })
    await user.click(select)
    await user.click(screen.getByRole("option", { name: "Hauptwache" }))

    expect(screen.getByRole("button", { name: "Zuordnen" })).toBeEnabled()
  })

  it("sollte die Zuordnung aufrufen, wenn der Button geklickt wird", async () => {
    const user = userEvent.setup()
    
    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: null,
      data: {
        data: [
          { id: "location-1", name: "Hauptwache" }
        ]
      }
    })

    const mutateMock = vi.fn()
    mockUseAssignMemberToLocation.mockReturnValue({
      mutate: mutateMock,
      isPending: false
    })

    render(<MemberLocationAssignment memberId="member-1" />)

    const select = screen.getByRole("combobox", { name: "Standort auswählen" })
    await user.click(select)
    await user.click(screen.getByRole("option", { name: "Hauptwache" }))

    const assignButton = screen.getByRole("button", { name: "Zuordnen" })
    await user.click(assignButton)

    expect(mutateMock).toHaveBeenCalledWith(
      { memberId: "member-1", locationId: "location-1" },
      { onSuccess: expect.any(Function) }
    )
  })

  it("sollte den Zuordnen-Button deaktivieren, wenn die Zuordnung läuft", () => {
    mockUseLocations.mockReturnValue({
      isLoading: false,
      error: null,
      data: {
        data: [
          { id: "location-1", name: "Hauptwache" }
        ]
      }
    })

    mockUseAssignMemberToLocation.mockReturnValue({
      mutate: vi.fn(),
      isPending: true
    })

    render(<MemberLocationAssignment memberId="member-1" currentLocationId="location-1" />)

    expect(screen.getByRole("button", { name: "Wird gespeichert..." })).toBeDisabled()
  })

  it("sollte den ausgewählten Standort anzeigen, wenn currentLocationId übergeben wird", () => {
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

    mockUseAssignMemberToLocation.mockReturnValue({
      mutate: vi.fn(),
      isPending: false
    })

    render(<MemberLocationAssignment memberId="member-1" currentLocationId="location-2" />)

    expect(screen.getByRole("combobox", { name: "Standort auswählen" })).toHaveValue("location-2")
  })
})
