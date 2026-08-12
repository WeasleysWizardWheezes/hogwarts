import { useNavigate } from "react-router-dom"
import { EquipmentForm } from "../components/equipment-form"
import { Button } from "@/shared/components/ui/button"
import { ArrowLeft } from "lucide-react"
import type { CreateEquipmentFormData } from "../schemas/equipment-schema"

export default function EquipmentCreatePage() {
  const navigate = useNavigate()

  const handleSubmit = (data: CreateEquipmentFormData) => {
    console.log("Gerät wird erstellt:", data)
    // Hier würde die API-Anbindung erfolgen
    // createMutation.mutate(data, {
    //   onSuccess: () => {
    //     navigate("/equipment")
    //   },
    // })
    navigate("/equipment")
  }

  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold">Neues Gerät erstellen</h1>
          <p className="text-muted-foreground">
            Erstelle ein neues Gerät für die Feuerwehr.
          </p>
        </div>
        <Button
          variant="outline"
          size="icon"
          onClick={() => navigate("/equipment")}
        >
          <ArrowLeft className="h-4 w-4" />
        </Button>
      </div>

      <div className="max-w-4xl">
        <EquipmentForm onSubmit={handleSubmit} />
      </div>
    </div>
  )
}
