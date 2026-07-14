import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createEquipment } from "../api/equipment-api";

export function useCreateEquipment() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createEquipment,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["equipment"] });
    },
  });
}
