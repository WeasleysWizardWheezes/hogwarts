import { describe, it, expect, vi } from "vitest"
import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { EquipmentTable } from "./equipment-table"

const mockEquipment = [
  {
    id: "equip-1",
    name: "Funkgerät 1",
    serialNumber: "SN-001",
    type: "Funk",
    location: "Lager A",
  },
  {
    id: "equip-2",
    name: "Schlauch 20m",
    serialNumber: "SN-002",
    type: "Schlauch",
    location: "Lager B",
  },
]

describe("EquipmentTable", () => {
  const onEdit = vi.fn()
  const onDelete = vi.fn()

  it("zeigt Tabellenüberschriften an", () => {
    render(
      <EquipmentTable
        equipment={mockEquipment}
        isLoading={false}
        onEdit={onEdit}
        onDelete={onDelete}
      />
    )

    expect(screen.getByRole("columnheader", { name: "Name" })).toBeInTheDocument()
    expect(screen.getByRole("columnheader", { name: "Seriennummer" })).toBeInTheDocument()
    expect(screen.getByRole("columnheader", { name: "Typ" })).toBeInTheDocument()
    expect(screen.getByRole("columnheader", { name: "Lagerort" })).toBeInTheDocument()
    expect(screen.getByRole("columnheader", { name: "Aktionen" })).toBeInTheDocument()
  })

  it("zeigt Gerätedaten an", () => {
    render(
      <EquipmentTable
        equipment={mockEquipment}
        isLoading={false}
        onEdit={onEdit}
        onDelete={onDelete}
      />
    )

    expect(screen.getByText("Funkgerät 1")).toBeInTheDocument()
    expect(screen.getByText("SN-001")).toBeInTheDocument()
    expect(screen.getByText("Funk")).toBeInTheDocument()
    expect(screen.getByText("Lager A")).toBeInTheDocument()

    expect(screen.getByText("Schlauch 20m")).toBeInTheDocument()
    expect(screen.getByText("SN-002")).toBeInTheDocument()
    expect(screen.getByText("Schlauch")).toBeInTheDocument()
    expect(screen.getByText("Lager B")).toBeInTheDocument()
  })

  it("zeigt Loading-Skeleton an", () => {
    render(
      <EquipmentTable equipment={[]} isLoading={true} onEdit={onEdit} onDelete={onDelete} />
    )

    const pulseElements = document.querySelectorAll(".animate-pulse")
    expect(pulseElements.length).toBeGreaterThan(0)
  })

  it("zeigt Leerzustand an", () => {
    render(
      <EquipmentTable equipment={[]} isLoading={false} onEdit={onEdit} onDelete={onDelete} />
    )

    expect(screen.getByText("Keine Geräte vorhanden.")).toBeInTheDocument()
  })

  it("ruft onEdit beim Klick auf 'Bearbeiten' auf", async () => {
    const user = userEvent.setup()
    render(
      <EquipmentTable
        equipment={mockEquipment}
        isLoading={false}
        onEdit={onEdit}
        onDelete={onDelete}
      />
    )

    const editButtons = screen.getAllByRole("button", { name: "Bearbeiten" })
    await user.click(editButtons[0])

    expect(onEdit).toHaveBeenCalledWith("equip-1")
  })

  it("ruft onDelete beim Klick auf 'Löschen' auf", async () => {
    const user = userEvent.setup()
    render(
      <EquipmentTable
        equipment={mockEquipment}
        isLoading={false}
        onEdit={onEdit}
        onDelete={onDelete}
      />
    )

    const deleteButtons = screen.getAllByRole("button", { name: "Löschen" })
    await user.click(deleteButtons[0])

    expect(onDelete).toHaveBeenCalledWith("equip-1")
  })

  it("zeigt mehrere 'Bearbeiten'-Buttons an", () => {
    render(
      <EquipmentTable
        equipment={mockEquipment}
        isLoading={false}
        onEdit={onEdit}
        onDelete={onDelete}
      />
    )

    const editButtons = screen.getAllByRole("button", { name: "Bearbeiten" })
    expect(editButtons.length).toBe(2)
  })

  it("zeigt mehrere 'Löschen'-Buttons an", () => {
    render(
      <EquipmentTable
        equipment={mockEquipment}
        isLoading={false}
        onEdit={onEdit}
        onDelete={onDelete}
      />
    )

    const deleteButtons = screen.getAllByRole("button", { name: "Löschen" })
    expect(deleteButtons.length).toBe(2)
  })
})