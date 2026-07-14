import { useEquipmentList } from "../hooks/use-equipment-list";
import { EquipmentTable } from "../components/equipment-table";
import { Button } from "@/shared/components/ui/button";
import { useNavigate } from "react-router-dom";

export function EquipmentListPage() {
  const equipmentQuery = useEquipmentList();
  const navigate = useNavigate();

  const handleCreate = () => {
    navigate("/equipment/create");
  };

  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Material- und Geräteinventar</h1>
        <Button onClick={handleCreate}>Neues Gerät anlegen</Button>
      </div>

      <EquipmentTable
        equipment={equipmentQuery.data ?? []}
        isLoading={equipmentQuery.isLoading}
        error={equipmentQuery.error}
      />
    </div>
  );
}
