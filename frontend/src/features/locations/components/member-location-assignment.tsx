import { useState } from "react"
import { Button } from "@/shared/components/ui/button"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/shared/components/ui/select"
import { useLocations } from "../hooks/use-locations"
import { useAssignMemberToLocation } from "../hooks/use-assign-member-to-location"
import type { components } from "@/shared/api"

export function MemberLocationAssignment({
  memberId,
  currentLocationId,
}: {
  memberId?: string
  currentLocationId?: string
}) {
  const [selectedLocationId, setSelectedLocationId] = useState<string | null>(currentLocationId || null)
  
  const locationsQuery = useLocations(0, 100)
  const assignMutation = useAssignMemberToLocation()

  const handleAssign = () => {
    if (selectedLocationId && memberId) {
      assignMutation.mutate(
        { memberId, locationId: selectedLocationId },
        {
          onSuccess: () => {
            // Erfolgreich zugeordnet
          },
        }
      )
    }
  }

  return (
    <div className="space-y-4">
      <h3 className="text-lg font-medium">Standortzuordnung</h3>

      {!memberId ? (
        <div className="text-muted-foreground">
          Standortzuordnung verfügbar nach dem Speichern des Mitglieds.
        </div>
      ) : locationsQuery.isLoading ? (
        <div>Standorte werden geladen...</div>
      ) : locationsQuery.error ? (
        <div className="text-destructive">
          Fehler beim Laden der Standorte
        </div>
      ) : (
        <div className="flex items-center gap-4">
          <Select
            value={selectedLocationId || ""}
            onValueChange={(value) => setSelectedLocationId(value)}
            disabled={assignMutation.isPending}
          >
            <SelectTrigger className="w-[200px]">
              <SelectValue placeholder="Standort auswählen" />
            </SelectTrigger>
            <SelectContent>
              {locationsQuery.data?.data?.map((location: any) => (
                <SelectItem key={location.id} value={location.id}>
                  {location.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Button
            onClick={handleAssign}
            disabled={!selectedLocationId || assignMutation.isPending}
          >
            {assignMutation.isPending ? "Wird gespeichert..." : "Zuordnen"}
          </Button>
        </div>
      )}
    </div>
  )
}
