import { useEquipmentList } from "../hooks/use-equipment-list"
import { Button } from "@/shared/components/ui/button"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/shared/components/ui/table"
import { LoadingState } from "@/shared/components/feedback/loading-state"
import { ErrorState } from "@/shared/components/feedback/error-state"
import { EmptyState } from "@/shared/components/feedback/empty-state"

export function EquipmentTable() {
  const equipmentQuery = useEquipmentList()

  if (equipmentQuery.isLoading) {
    return <LoadingState message="Geräte werden geladen." />
  }

  if (equipmentQuery.error) {
    return <ErrorState message="Geräte konnten nicht geladen werden." />
  }

  if (!equipmentQuery.data || equipmentQuery.data.length === 0) {
    return (
      <EmptyState
        title="Keine Geräte vorhanden"
        description="Lege das erste Gerät an, um mit der Inventarverwaltung zu beginnen."
      />
    )
  }

  return (
    <div className="rounded-md border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Name</TableHead>
            <TableHead>Kategorie</TableHead>
            <TableHead>Status</TableHead>
            <TableHead>Standort</TableHead>
            <TableHead>Aktionen</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {equipmentQuery.data.map((equipment) => (
            <TableRow key={equipment.id}>
              <TableCell>{equipment.name}</TableCell>
              <TableCell>{equipment.category}</TableCell>
              <TableCell>{equipment.status}</TableCell>
              <TableCell>{equipment.location}</TableCell>
              <TableCell>
                <Button variant="outline" size="sm">
                  Bearbeiten
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}
