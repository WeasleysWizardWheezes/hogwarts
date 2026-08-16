import { describe, it, expect, vi, beforeEach } from "vitest"
import { render, screen, fireEvent } from "@testing-library/react"
import { EquipmentFilterPanel } from "./equipment-filter-panel"

describe("EquipmentFilterPanel", () => {
  const mockOnSearchChange = vi.fn()
  const mockOnLocationChange = vi.fn()

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it("renders search input with correct placeholder", () => {
    render(
      <EquipmentFilterPanel
        searchValue=""
        onSearchChange={mockOnSearchChange}
        locationValue=""
        onLocationChange={mockOnLocationChange}
      />,
    )

    const searchInput = screen.getByPlaceholderText("Gerätename oder Seriennummer...")
    expect(searchInput).toBeInTheDocument()
  })

  it("renders location input with correct placeholder", () => {
    render(
      <EquipmentFilterPanel
        searchValue=""
        onSearchChange={mockOnSearchChange}
        locationValue=""
        onLocationChange={mockOnLocationChange}
      />,
    )

    const locationInput = screen.getByPlaceholderText("Lagerort filtern...")
    expect(locationInput).toBeInTheDocument()
  })

  it("calls onSearchChange when search input value changes", () => {
    render(
      <EquipmentFilterPanel
        searchValue=""
        onSearchChange={mockOnSearchChange}
        locationValue=""
        onLocationChange={mockOnLocationChange}
      />,
    )

    const searchInput = screen.getByPlaceholderText("Gerätename oder Seriennummer...")
    fireEvent.change(searchInput, { target: { value: "Gerätename" } })

    expect(mockOnSearchChange).toHaveBeenCalledWith("Gerätename")
  })

  it("calls onLocationChange when location input value changes", () => {
    render(
      <EquipmentFilterPanel
        searchValue=""
        onSearchChange={mockOnSearchChange}
        locationValue=""
        onLocationChange={mockOnLocationChange}
      />,
    )

    const locationInput = screen.getByPlaceholderText("Lagerort filtern...")
    fireEvent.change(locationInput, { target: { value: "Lager A" } })

    expect(mockOnLocationChange).toHaveBeenCalledWith("Lager A")
  })

  it("displays search input with provided searchValue", () => {
    render(
      <EquipmentFilterPanel
        searchValue="Suchbegriff"
        onSearchChange={mockOnSearchChange}
        locationValue=""
        onLocationChange={mockOnLocationChange}
      />,
    )

    const searchInput = screen.getByPlaceholderText("Gerätename oder Seriennummer...")
    expect(searchInput).toHaveValue("Suchbegriff")
  })

  it("displays location input with provided locationValue", () => {
    render(
      <EquipmentFilterPanel
        searchValue=""
        onSearchChange={mockOnSearchChange}
        locationValue="Lager B"
        onLocationChange={mockOnLocationChange}
      />,
    )

    const locationInput = screen.getByPlaceholderText("Lagerort filtern...")
    expect(locationInput).toHaveValue("Lager B")
  })

  it("renders labels for search and location inputs", () => {
    render(
      <EquipmentFilterPanel
        searchValue=""
        onSearchChange={mockOnSearchChange}
        locationValue=""
        onLocationChange={mockOnLocationChange}
      />,
    )

    expect(screen.getByText("Suche (Name/Seriennummer)")).toBeInTheDocument()
    expect(screen.getByText("Lagerort")).toBeInTheDocument()
  })

  it("displays inputs in flex-col on mobile (default)", () => {
    const { container } = render(
      <EquipmentFilterPanel
        searchValue=""
        onSearchChange={mockOnSearchChange}
        locationValue=""
        onLocationChange={mockOnLocationChange}
      />,
    )

    const containerElement = container.firstChild as HTMLElement
    expect(containerElement).toHaveClass("flex-col")
    expect(containerElement).toHaveClass("sm:flex-row")
  })
})