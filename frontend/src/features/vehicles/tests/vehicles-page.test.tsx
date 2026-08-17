import { screen, waitFor } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { describe, it, expect, vi, beforeEach } from "vitest"
import { render } from "@/test/render"
import VehiclesPage from "../pages/vehicles-page"

// Mock the API hooks
vi.mock("../api/vehicles-api", () => ({
  useVehicles: vi.fn(),
  useCreateVehicle: vi.fn(),
  useUpdateVehicle: vi.fn(),
  useDeleteVehicle: vi.fn(),
}))

vi.mock("@/features/vehicle-groups", () => ({
  useVehicleGroups: vi.fn(),
}))

import {
  useVehicles,
  useCreateVehicle,
  useUpdateVehicle,
  useDeleteVehicle,
} from "../api/vehicles-api"
import { useVehicleGroups } from "@/features/vehicle-groups"

const mockUseVehicles = vi.mocked(useVehicles)
const mockUseCreateVehicle = vi.mocked(useCreateVehicle)
const mockUseUpdateVehicle = vi.mocked(useUpdateVehicle)
const mockUseDeleteVehicle = vi.mocked(useDeleteVehicle)
const mockUseVehicleGroups = vi.mocked(useVehicleGroups)

const mockVehicles = [
  {
    id: "veh-hlf20-01",
    name: "01-HLF20-01",
    funkrufname: "Florian Monheim 01-HLF20-01",
    kennzeichen: "ME-FM 219",
    baujahr: 2019,
    beschreibung: "Hilfeleistungslöschgruppenfahrzeug 20",
    status: "VERFUEGBAR" as const,
    vehicleGroupId: "vg-loeschfahrzeuge",
    vehicleGroupName: "Löschfahrzeuge",
    createdAt: "2026-01-15T10:30:00Z",
    updatedAt: "2026-01-15T10:30:00Z",
  },
  {
    id: "veh-dlak-01",
    name: "01-DLK23-01",
    funkrufname: "Florian Monheim 01-DLK23-01",
    kennzeichen: "ME-FM 220",
    baujahr: 2021,
    beschreibung: "Drehleiter mit Korb 23m",
    status: "WARTUNG" as const,
    vehicleGroupId: "vg-drehleiter",
    vehicleGroupName: "Drehleitern",
    createdAt: "2026-02-10T08:00:00Z",
    updatedAt: "2026-02-10T08:00:00Z",
  },
]

const mockVehicleGroupsList = [
  { id: "vg-loeschfahrzeuge", name: "Löschfahrzeuge", beschreibung: "Alle Löschfahrzeuge" },
  { id: "vg-drehleiter", name: "Drehleitern", beschreibung: "Hubrettungsfahrzeuge" },
]

const mockMutationIdle = {
  mutateAsync: vi.fn(),
  isPending: false,
} as unknown as ReturnType<typeof useCreateVehicle>

function setupMocks(overrides?: Partial<ReturnType<typeof useVehicles>>) {
  mockUseVehicles.mockReturnValue({
    data: { data: mockVehicles },
    isLoading: false,
    isError: false,
    ...overrides,
  } as unknown as ReturnType<typeof useVehicles>)

  mockUseVehicleGroups.mockReturnValue({
    data: { data: mockVehicleGroupsList },
    isLoading: false,
    isError: false,
  } as unknown as ReturnType<typeof useVehicleGroups>)

  mockUseCreateVehicle.mockReturnValue(mockMutationIdle)
  mockUseUpdateVehicle.mockReturnValue(mockMutationIdle as unknown as ReturnType<typeof useUpdateVehicle>)
  mockUseDeleteVehicle.mockReturnValue(mockMutationIdle as unknown as ReturnType<typeof useDeleteVehicle>)
}

describe("VehiclesPage", () => {
  beforeEach(() => {
    vi.clearAllMocks()
    setupMocks()
  })

  it("renders the page title", () => {
    render(<VehiclesPage />)
    expect(screen.getByRole("heading", { name: "Fahrzeuge" })).toBeInTheDocument()
  })

  it("shows the create button", () => {
    render(<VehiclesPage />)
    expect(screen.getByRole("button", { name: /erstellen/i })).toBeInTheDocument()
  })

  it("shows a table with vehicle data", () => {
    render(<VehiclesPage />)
    expect(screen.getByText("01-HLF20-01")).toBeInTheDocument()
    expect(screen.getByText("Florian Monheim 01-HLF20-01")).toBeInTheDocument()
    expect(screen.getByText("ME-FM 219")).toBeInTheDocument()
    expect(screen.getByText("Löschfahrzeuge")).toBeInTheDocument()
    expect(screen.getByText("01-DLK23-01")).toBeInTheDocument()
  })

  it("shows status badges", () => {
    render(<VehiclesPage />)
    expect(screen.getByText("Verfügbar")).toBeInTheDocument()
    expect(screen.getByText("Wartung")).toBeInTheDocument()
  })

  it("shows filter selects", () => {
    render(<VehiclesPage />)
    expect(screen.getByText("Alle Gruppen")).toBeInTheDocument()
    expect(screen.getByText("Alle Status")).toBeInTheDocument()
  })

  it("shows loading state", () => {
    setupMocks({ data: undefined, isLoading: true })
    render(<VehiclesPage />)
    expect(screen.getByText("Laden...")).toBeInTheDocument()
  })

  it("shows error state", () => {
    setupMocks({ data: undefined, isError: true })
    render(<VehiclesPage />)
    expect(screen.getByText("Fehler beim Laden.")).toBeInTheDocument()
  })

  it("shows empty state when no vehicles exist", () => {
    setupMocks({ data: { data: [] } })
    render(<VehiclesPage />)
    expect(screen.getByText("Keine Einträge vorhanden.")).toBeInTheDocument()
  })

  it("opens create dialog when create button is clicked", async () => {
    const user = userEvent.setup()
    render(<VehiclesPage />)

    await user.click(screen.getByRole("button", { name: /erstellen/i }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Fahrzeug erstellen" })).toBeInTheDocument()
    })
  })

  it("opens edit dialog when edit button is clicked", async () => {
    const user = userEvent.setup()
    render(<VehiclesPage />)

    const editButtons = screen.getAllByRole("button", { name: "Bearbeiten" })
    await user.click(editButtons[0])

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Fahrzeug bearbeiten" })).toBeInTheDocument()
    })
  })

  it("opens delete dialog when delete button is clicked", async () => {
    const user = userEvent.setup()
    render(<VehiclesPage />)

    const deleteButtons = screen.getAllByRole("button", { name: "Löschen" })
    await user.click(deleteButtons[0])

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Fahrzeug löschen" })).toBeInTheDocument()
    })
    expect(screen.getByText(/Möchten Sie .+01-HLF20-01.+ wirklich löschen/)).toBeInTheDocument()
  })
})
