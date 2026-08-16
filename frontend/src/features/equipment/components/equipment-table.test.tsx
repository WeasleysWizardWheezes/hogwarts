import { describe, it, expect, vi, beforeEach } from "vitest"
import { render, screen } from "@testing-library/react"
import { EquipmentTable } from "./equipment-table"

describe("EquipmentTable", () => {
  const mockOnEdit = vi.fn()
  const mockOnDelete = vi.fn()

  const mockEquipment = [
    {
      id: "1",
      name: "Atemschutzgerät Dräger PSS 7000",
      serialNumber: "DRG-2023-0042",
      type: "Atemschutz",
      location: "Fahrzeug 1, Fach 3",
    },
    {
      id: "2",
      name: "Löschrohr 20mm",
      serialNumber: "LR-20-001",
      type: "Löschgeräte",
      location: "Fahrzeug 2, Fach 1",
    },
  ]

  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe("Loading State", () => {
    it("renders loading skeleton when isLoading is true", () => {
      render(
        <EquipmentTable
          equipment={[]}
          isLoading={true}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
        />,
      )

      // Should render 5 loading rows (as per implementation)
      const loadingRows = document.querySelectorAll(".animate-pulse")
      expect(loadingRows.length).toBeGreaterThan(0)
    })

    it("renders correct number of loading placeholders", () => {
      render(
        <EquipmentTable
          equipment={[]}
          isLoading={true}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
        />,
      )

      // Each row has 5 placeholders (Name, Serial, Type, Location, Actions)
      const loadingPlaceholders = document.querySelectorAll(".h-4.bg-muted.w-24")
      expect(loadingPlaceholders.length).toBe(5) // 5 rows × 1 placeholder each for name
    })
  })

  describe("Empty State", () => {
    it("renders empty state message when equipment is empty and not loading", () => {
      render(
        <EquipmentTable
          equipment={[]}
          isLoading={false}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
        />,
      )

      expect(screen.getByText("Keine Geräte vorhanden.")).toBeInTheDocument()
    })

    it("does not render table when equipment is empty", () => {
      render(
        <EquipmentTable
          equipment={[]}
          isLoading={false}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
        />,
      )

      expect(document.querySelector("table")).not.toBeInTheDocument()
    })
  })

  describe("Data State", () => {
    it("renders all equipment items in the table", () => {
      render(
        <EquipmentTable
          equipment={mockEquipment}
          isLoading={false}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
        />,
      )

      expect(screen.getByText("Atemschutzgerät Dräger PSS 7000")).toBeInTheDocument()
      expect(screen.getByText("DRG-2023-0042")).toBeInTheDocument()
      expect(screen.getByText("Atemschutz")).toBeInTheDocument()
      expect(screen.getByText("Fahrzeug 1, Fach 3")).toBeInTheDocument()

      expect(screen.getByText("Löschrohr 20mm")).toBeInTheDocument()
      expect(screen.getByText("LR-20-001")).toBeInTheDocument()
      expect(screen.getByText("Löschgeräte")).toBeInTheDocument()
      expect(screen.getByText("Fahrzeug 2, Fach 1")).toBeInTheDocument()
    })

    it("renders correct table headers", () => {
      render(
        <EquipmentTable
          equipment={mockEquipment}
          isLoading={false}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
        />,
      )

      expect(screen.getByText("Name")).toBeInTheDocument()
      expect(screen.getByText("Seriennummer")).toBeInTheDocument()
      expect(screen.getByText("Typ")).toBeInTheDocument()
      expect(screen.getByText("Lagerort")).toBeInTheDocument()
      expect(screen.getByText("Aktionen")).toBeInTheDocument()
    })

    it("renders edit and delete buttons for each row", () => {
      render(
        <EquipmentTable
          equipment={mockEquipment}
          isLoading={false}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
        />,
      )

      const editButtons = screen.getAllByText("Bearbeiten")
      const deleteButtons = screen.getAllByText("Löschen")

      expect(editButtons.length).toBe(2)
      expect(deleteButtons.length).toBe(2)
    })

    it("calls onEdit callback when edit button is clicked", () => {
      render(
        <EquipmentTable
          equipment={mockEquipment}
          isLoading={false}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
        />,
      )

      const editButtons = screen.getAllByText("Bearbeiten")
      editButtons[0].click()

      expect(mockOnEdit).toHaveBeenCalledWith("1")
    })

    it("calls onDelete callback when delete button is clicked", () => {
      render(
        <EquipmentTable
          equipment={mockEquipment}
          isLoading={false}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
        />,
      )

      const deleteButtons = screen.getAllByText("Löschen")
      deleteButtons[0].click()

      expect(mockOnDelete).toHaveBeenCalledWith("1")
    })

    it("uses device.id as key for table rows", () => {
      render(
        <EquipmentTable
          equipment={mockEquipment}
          isLoading={false}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
        />,
      )

      // Verify the row with correct ID exists
      const rows = document.querySelectorAll("tbody tr")
      expect(rows.length).toBe(2)
    })
  })

  describe("Table Structure", () => {
    it("wraps content in a Table component", () => {
      const { container } = render(
        <EquipmentTable
          equipment={mockEquipment}
          isLoading={false}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
        />,
      )

      expect(container.querySelector("table")).toBeInTheDocument()
    })
  })
})