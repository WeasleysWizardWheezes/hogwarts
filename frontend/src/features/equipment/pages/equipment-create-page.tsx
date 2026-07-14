import { useCreateEquipment } from "../hooks/use-create-equipment";
import { EquipmentForm } from "../components/equipment-form";
import { useNavigate } from "react-router-dom";

export function EquipmentCreatePage() {
  const createMutation = useCreateEquipment();
  const navigate = useNavigate();

  const handleSubmit = (data: any) => {
    createMutation.mutate(data, {
      onSuccess: () => {
        navigate("/equipment");
      },
    });
  };

  return (
    <div className="container mx-auto py-8 max-w-2xl">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Neues Gerät anlegen</h1>
      </div>

      <EquipmentForm
        onSubmit={handleSubmit}
        isSubmitting={createMutation.isPending}
      />
    </div>
  );
}
