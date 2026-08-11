import { useState } from "react"
import { Link } from "react-router-dom"
import { Button } from "@/shared/components/ui/button"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/shared/components/ui/table"
import { Badge } from "@/shared/components/ui/badge"
import { Pagination } from "@/shared/components/ui/pagination"
import { useLocations } from "../hooks/use-locations"
import { useDeleteLocation } from "../hooks/use-delete-location"
import { ConfirmActionDialog } from "@/shared/components/ui/confirm-action-dialog"
import type { LocationType } from "../types"

export function LocationTable() {
  const [page, setPage] = useState(0)
  const [size] = useState(10)
  const [deleteId, setDeleteId] = useState<string | null>(null)
  
  const locationsQuery = useLocations(page, size)
  const deleteMutation = useDeleteLocation()

  const handleDelete = (id: string) => {
    setDeleteId(id)
  }

  const confirmDelete = () => {
    if (deleteId) {
      deleteMutation.mutate(deleteId)
      setDeleteId(null)
    }
  }

  const getLocationTypeLabel = (type: LocationType) => {
    switch (type) {
      case "FIRE_STATION":
        return "Feuerwache"
      case "EQUIPMENT_DEPOT":
        return "Gerätedepot"
      case "TRAINING_CENTER":
        return "Ausbildungszentrum"
      default:
        return type
    }
  }

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Standorte</h2>
        <Link to="/locations/create">
          <Button>Standort erstellen</Button>
        </Link>
      </div>

      {locationsQuery.isLoading ? (
        <div>Standorte werden geladen...</div>
      ) : locationsQuery.error ? (
        <div className="text-destructive">
          Fehler beim Laden der Standorte: {locationsQuery.error.message}
        </div>
      ) : (
        <div className="space-y-4">
          <div className="border rounded-lg overflow-hidden">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Name</TableHead>
                  <TableHead>Adresse</TableHead>
                  <TableHead>Typ</TableHead>
                  <TableHead>Aktionen</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {locationsQuery.data?.data?.map((location: any) => (
                  <TableRow key={location.id}>
                    <TableCell>{location.name}</TableCell>
                    <TableCell>{location.address || "-"}</TableCell>
                    <TableCell>
                      <Badge variant="secondary">
                        {getLocationTypeLabel(location.type)}
                      </Badge>
                    </TableCell>
                    <TableCell className="space-x-2">
                      <Link to={`/locations/${location.id}`}>
                        <Button variant="outline" size="sm">
                          Bearbeiten
                        </Button>
                      </Link>
                      <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => handleDelete(location.id)}
                      >
                        Löschen
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>

          <Pagination
            page={page}
            totalPages={locationsQuery.data?.page?.totalPages || 0}
            onPageChange={setPage}
          />
        </div>
      )}

      <ConfirmActionDialog
        open={!!deleteId}
        onOpenChange={() => setDeleteId(null)}
        title="Standort löschen?"
        description="Dieser Standort wird dauerhaft gelöscht. Alle Zuordnungen von Mitgliedern zu diesem Standort werden entfernt."
        confirmLabel="Standort löschen"
        onConfirm={confirmDelete}
      />
    </div>
  )
}
