import { useNavigate } from "react-router-dom"
import { LocationForm } from "../components/location-form"
import { useCreateLocation } from "../hooks/use-create-location"
import { Button } from "@/shared/components/ui/button"
import { ArrowLeft } from "lucide-react"

export default function LocationCreatePage() {
  const navigate = useNavigate()
  const createMutation = useCreateLocation()

  const handleSubmit = (data: any) => {
    createMutation.mutate(data, {
      onSuccess: () => {
        navigate("/locations")
      },
    })
  }

  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold">Standort erstellen</h1>
          <p className="text-muted-foreground">
            Erstelle einen neuen Standort für die Feuerwehr.
          </p>
        </div>
        <Button
          variant="outline"
          size="icon"
          onClick={() => navigate("/locations")}
        >
          <ArrowLeft className="h-4 w-4" />
        </Button>
      </div>

      <div className="max-w-2xl">
        <LocationForm onSubmit={handleSubmit} isLoading={createMutation.isPending} />
      </div>
    </div>
  )
}
