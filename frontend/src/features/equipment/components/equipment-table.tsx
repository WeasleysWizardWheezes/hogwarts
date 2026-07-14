import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/shared/components/ui/table";
import { Button } from "@/shared/components/ui/button";
import type { Equipment } from "../types";
import { useDeleteEquipment } from "../hooks/use-delete-equipment";
import { useNavigate } from "react-router-dom";

type EquipmentTableProps = {
  equipment: Equipment[];
  isLoading: boolean;
  error: unknown;
};

export function EquipmentTable({ equipment, isLoading, error }: EquipmentTableProps) {
  const deleteMutation = useDeleteEquipment();
  const navigate = useNavigate();

  if (isLoading) {
    return <div>Geräte werden geladen...</div>;
  }

  if (error) {
    return <div>Fehler beim Laden der Geräte</div>;
  }

  if (equipment.length === 0) {
    return <div>Keine Geräte vorhanden</div>;
  }

  const handleDelete = (id: string) => {
    if (window.confirm("Gerät wirklich löschen?")) {
      deleteMutation.mutate(id);
    }
  };

  const handleEdit = (id: string) => {
    navigate(`/equipment/${id}/edit`);
  };

  return (
    <div className="rounded-md border">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Name</TableHead>
            <TableHead>Seriennummer</TableHead>
            <TableHead>Typ</TableHead>
            <TableHead>Lagerort</TableHead>
            <TableHead>Aktionen</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {equipment.map((item) => (
            <TableRow key={item.id}>
              <TableCell>{item.name}</TableCell>
              <TableCell>{item.serialNumber}</TableCell>
              <TableCell>{item.type}</TableCell>
              <TableCell>{item.storageLocation}</TableCell>
              <TableCell>
                <div className="flex gap-2">
                  <Button variant="outline" size="sm" onClick={() => handleEdit(item.id)}>
                    Bearbeiten
                  </Button>
                  <Button variant="destructive" size="sm" onClick={() => handleDelete(item.id)}>
                    Löschen
                  </Button>
                </div>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}
