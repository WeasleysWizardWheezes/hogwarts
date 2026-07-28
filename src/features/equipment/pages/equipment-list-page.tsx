import { EquipmentTable } from "../components/equipment-table"
import { Button } from "@/shared/components/ui/button"
import { PlusIcon } from "lucide-react"

export default function EquipmentListPage() {
  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-2xl font-bold">Geräte</h1>
          <p className="text-muted-foreground">
            Verwalte Ausrüstung, Standorte, Status und Prüffristen.
          </p>
        </div>
        <Button>
          <PlusIcon className="mr-2 h-4 w-4" />
          Gerät hinzufügen
        </Button>
      </div>
      <EquipmentTable />
    </div>
  )
}
