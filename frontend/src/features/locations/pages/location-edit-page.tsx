import { useParams, useNavigate } from "react-router-dom"
import { LocationForm } from "../components/location-form"
import { useLocation } from "../hooks/use-location"
import { useUpdateLocation } from "../hooks/use-update-location"
import { Button } from "@/shared/components/ui/button"
import { ArrowLeft } from "lucide-react"

export default function LocationEditPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  
  const locationQuery = useLocation(id || "")
  const updateMutation = useUpdateLocation()

  const handleSubmit = (data: any) => {
    if (id) {
      updateMutation.mutate(
        { id, body: data },
        {
          onSuccess: () => {
            navigate("/locations")
          },
        }
      )
    }
  }

  if (locationQuery.isLoading) {
    return <div>Standort wird geladen...</div>
  }

  if (locationQuery.error) {
    return (
      <div className="text-destructive">
        Fehler beim Laden des Standorts: {locationQuery.error.message}
      </div>
    )
  }

  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold">Standort bearbeiten</h1>
          <p className="text-muted-foreground">
            Bearbeite die Standortinformationen.
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
        <LocationForm
          initialValues={locationQuery.data}
          onSubmit={handleSubmit}
          isLoading={updateMutation.isPending}
        />
      </div>
    </div>
  )
}
