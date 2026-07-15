import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/shared/components/ui/table"
import { Button } from "@/shared/components/ui/button"

interface EquipmentTableProps {
  equipment: Array<{
    id: string
    name: string
    serialNumber: string
    type: string
    location: string
  }>
  isLoading: boolean
  onEdit: (id: string) => void
  onDelete: (id: string) => void
}

export function EquipmentTable({
  equipment,
  isLoading,
  onEdit,
  onDelete,
}: EquipmentTableProps) {
  if (isLoading) {
    return (
      <div className="space-y-2">
        {[1, 2, 3, 4, 5].map((i) => (
          <div key={i} className="flex space-x-4">
            <div className="h-4 bg-muted w-24 rounded animate-pulse" />
            <div className="h-4 bg-muted w-32 rounded animate-pulse" />
            <div className="h-4 bg-muted w-20 rounded animate-pulse" />
            <div className="h-4 bg-muted w-32 rounded animate-pulse" />
            <div className="h-4 bg-muted w-16 rounded animate-pulse" />
          </div>
        ))}
      </div>
    )
  }

  if (equipment.length === 0) {
    return (
      <div className="text-center py-12">
        <p className="text-muted-foreground">Keine Geräte vorhanden.</p>
      </div>
    )
  }

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Name</TableHead>
          <TableHead>Seriennummer</TableHead>
          <TableHead>Typ</TableHead>
          <TableHead>Lagerort</TableHead>
          <TableHead className="text-right">Aktionen</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {equipment.map((device) => (
          <TableRow key={device.id}>
            <TableCell>{device.name}</TableCell>
            <TableCell>{device.serialNumber}</TableCell>
            <TableCell>{device.type}</TableCell>
            <TableCell>{device.location}</TableCell>
            <TableCell className="text-right">
              <Button variant="ghost" size="sm" onClick={() => onEdit(device.id)}>
                Bearbeiten
              </Button>
              <Button variant="destructive" size="sm" onClick={() => onDelete(device.id)}>
                Löschen
              </Button>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  )
}
