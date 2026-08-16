import { describe, it, expect, vi } from "vitest"
import { render, screen } from "@testing-library/react"
import { EquipmentFilterPanel } from "./equipment-filter-panel"

describe("EquipmentFilterPanel", () => {
  const onSearchChange = vi.fn()
  const onLocationChange = vi.fn()

  it("zeigt Suchfeld für Name/Seriennummer an", () => {
    render(
      <EquipmentFilterPanel
        searchValue=""
        onSearchChange={onSearchChange}
        locationValue=""
        onLocationChange={onLocationChange}
      />
    )

    expect(screen.getByPlaceholderText("Gerätename oder Seriennummer...")).toBeInTheDocument()
  })

  it("zeigt Filterfeld für Lagerort an", () => {
    render(
      <EquipmentFilterPanel
        searchValue=""
        onSearchChange={onSearchChange}
        locationValue=""
        onLocationChange={onLocationChange}
      />
    )

    expect(screen.getByPlaceholderText("Lagerort filtern...")).toBeInTheDocument()
  })

  it("zeigt aktuelle Werte in den Feldern an", () => {
    render(
      <EquipmentFilterPanel
        searchValue="Funkgerät"
        onSearchChange={onSearchChange}
        locationValue="Lager A"
        onLocationChange={onLocationChange}
      />
    )

    expect(screen.getByDisplayValue("Funkgerät")).toBeInTheDocument()
    expect(screen.getByDisplayValue("Lager A")).toBeInTheDocument()
  })

  it("zeigt beide Label an", () => {
    render(
      <EquipmentFilterPanel
        searchValue=""
        onSearchChange={onSearchChange}
        locationValue=""
        onLocationChange={onLocationChange}
      />
    )

    expect(screen.getByText("Suche (Name/Seriennummer)")).toBeInTheDocument()
    expect(screen.getByText("Lagerort")).toBeInTheDocument()
  })

  it("zeigt die Komponente in zwei Spalten an", () => {
    render(
      <EquipmentFilterPanel
        searchValue=""
        onSearchChange={onSearchChange}
        locationValue=""
        onLocationChange={onLocationChange}
      />
    )

    // Der Container sollte flex-col oder sm:flex-row haben
    const container = document.querySelector(".flex.flex-col.sm\\:flex-row")
    expect(container).toBeInTheDocument()
  })
})