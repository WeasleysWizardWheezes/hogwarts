import { screen, waitFor } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { describe, it, expect, vi, beforeEach } from "vitest"
import { render } from "@/test/render"
import VehicleGroupsPage from "../pages/vehicle-groups-page"

// Mock the API hooks
vi.mock("../api/vehicle-groups-api", () => ({
  useVehicleGroups: vi.fn(),
  useCreateVehicleGroup: vi.fn(),
  useUpdateVehicleGroup: vi.fn(),
  useDeleteVehicleGroup: vi.fn(),
}))

import {
  useVehicleGroups,
  useCreateVehicleGroup,
  useUpdateVehicleGroup,
  useDeleteVehicleGroup,
} from "../api/vehicle-groups-api"

const mockUseVehicleGroups = vi.mocked(useVehicleGroups)
const mockUseCreateVehicleGroup = vi.mocked(useCreateVehicleGroup)
const mockUseUpdateVehicleGroup = vi.mocked(useUpdateVehicleGroup)
const mockUseDeleteVehicleGroup = vi.mocked(useDeleteVehicleGroup)

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

const mockMutationIdle = {
  mutateAsync: vi.fn(),
  isPending: false,
} as unknown as ReturnType<typeof useCreateVehicleGroup>

function setupMocks(overrides?: Partial<ReturnType<typeof useVehicleGroups>>) {
  mockUseVehicleGroups.mockReturnValue({
    data: { data: mockVehicleGroups },
    isLoading: false,
    isError: false,
    ...overrides,
  } as unknown as ReturnType<typeof useVehicleGroups>)

  mockUseCreateVehicleGroup.mockReturnValue(mockMutationIdle)
  mockUseUpdateVehicleGroup.mockReturnValue(mockMutationIdle as unknown as ReturnType<typeof useUpdateVehicleGroup>)
  mockUseDeleteVehicleGroup.mockReturnValue(mockMutationIdle as unknown as ReturnType<typeof useDeleteVehicleGroup>)
}

describe("VehicleGroupsPage", () => {
  beforeEach(() => {
    vi.clearAllMocks()
    setupMocks()
  })

  it("renders the page title", () => {
    render(<VehicleGroupsPage />)
    expect(screen.getByRole("heading", { name: "Fahrzeuggruppen" })).toBeInTheDocument()
  })

  it("shows the create button", () => {
    render(<VehicleGroupsPage />)
    expect(screen.getByRole("button", { name: /erstellen/i })).toBeInTheDocument()
  })

  it("shows a table with vehicle group data", () => {
    render(<VehicleGroupsPage />)
    expect(screen.getByText("Löschfahrzeuge")).toBeInTheDocument()
    expect(screen.getByText("Alle Löschfahrzeuge des Löschzugs")).toBeInTheDocument()
    expect(screen.getByText("Drehleitern")).toBeInTheDocument()
    expect(screen.getByText("Hubrettungsfahrzeuge")).toBeInTheDocument()
  })

  it("shows loading state", () => {
    setupMocks({ data: undefined, isLoading: true })
    render(<VehicleGroupsPage />)
    expect(screen.getByText("Laden...")).toBeInTheDocument()
  })

  it("shows error state", () => {
    setupMocks({ data: undefined, isError: true })
    render(<VehicleGroupsPage />)
    expect(screen.getByText("Fehler beim Laden.")).toBeInTheDocument()
  })

  it("shows empty state when no vehicle groups exist", () => {
    setupMocks({ data: { data: [] } })
    render(<VehicleGroupsPage />)
    expect(screen.getByText("Keine Einträge vorhanden.")).toBeInTheDocument()
  })

  it("opens create dialog when create button is clicked", async () => {
    const user = userEvent.setup()
    render(<VehicleGroupsPage />)

    await user.click(screen.getByRole("button", { name: /erstellen/i }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Fahrzeuggruppe erstellen" })).toBeInTheDocument()
    })
  })

  it("opens edit dialog when edit button is clicked", async () => {
    const user = userEvent.setup()
    render(<VehicleGroupsPage />)

    const editButtons = screen.getAllByRole("button", { name: "Bearbeiten" })
    await user.click(editButtons[0])

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Fahrzeuggruppe bearbeiten" })).toBeInTheDocument()
    })
  })

  it("opens delete dialog when delete button is clicked", async () => {
    const user = userEvent.setup()
    render(<VehicleGroupsPage />)

    const deleteButtons = screen.getAllByRole("button", { name: "Löschen" })
    await user.click(deleteButtons[0])

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Fahrzeuggruppe löschen" })).toBeInTheDocument()
    })
    expect(screen.getByText(/Möchten Sie .+Löschfahrzeuge.+ wirklich löschen/)).toBeInTheDocument()
  })
})
