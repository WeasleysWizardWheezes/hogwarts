import { useState } from "react"
import { useEquipmentList } from "@/features/equipment/hooks/use-equipment-list"
import { useDeleteEquipment } from "@/features/equipment/hooks/use-delete-equipment"
import { EquipmentTable } from "@/features/equipment/components/equipment-table"
import { EquipmentFilterPanel } from "@/features/equipment/components/equipment-filter-panel"
import { Button } from "@/shared/components/ui/button"
import { Plus } from "lucide-react"
import { useNavigate } from "react-router"

export default function EquipmentListPage() {
  const [searchValue, setSearchValue] = useState("")
  const [locationValue, setLocationValue] = useState("")
  const navigate = useNavigate()

  const equipmentQuery = useEquipmentList(searchValue, locationValue)
  const deleteMutation = useDeleteEquipment()

  const handleEdit = (id: string) => {
    navigate(`/equipment/${id}/edit`)
  }

  const handleDelete = (id: string) => {
    deleteMutation.mutate(id)
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Geräte</h1>
          <p className="text-muted-foreground">
            Verwalte Ausrüstung, Standorte, Status und Prüffristen.
          </p>
        </div>
        <Button onClick={() => navigate("/equipment/new")}>
          <Plus className="mr-2 h-4 w-4" />
          Gerät hinzufügen
        </Button>
      </div>

      <EquipmentFilterPanel
        searchValue={searchValue}
        onSearchChange={setSearchValue}
        locationValue={locationValue}
        onLocationChange={setLocationValue}
      />

      <EquipmentTable
        equipment={equipmentQuery.data ?? []}
        isLoading={equipmentQuery.isLoading}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />
    </div>
  )
}
