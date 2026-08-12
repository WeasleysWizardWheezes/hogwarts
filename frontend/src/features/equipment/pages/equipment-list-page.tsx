import { Link } from "react-router-dom"
import { Button } from "@/shared/components/ui/button"
import { Plus } from "lucide-react"

export default function EquipmentListPage() {
  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold">Geräteverwaltung</h1>
          <p className="text-muted-foreground">
            Verwalte alle Feuerwehrgeräte und Ausrüstung.
          </p>
        </div>
        <Link to="/equipment/create">
          <Button>
            <Plus className="mr-2 h-4 w-4" />
            Neues Gerät
          </Button>
        </Link>
      </div>

      <div className="bg-card rounded-lg p-6">
        <p className="text-muted-foreground">
          Geräteliste wird hier angezeigt. (In Entwicklung)
        </p>
      </div>
    </div>
  )
}
