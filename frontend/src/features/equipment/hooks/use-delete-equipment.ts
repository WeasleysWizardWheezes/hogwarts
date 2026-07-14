import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deleteEquipment } from "../api/equipment-api";

export function useDeleteEquipment() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteEquipment,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["equipment"] });
    },
  });
}
