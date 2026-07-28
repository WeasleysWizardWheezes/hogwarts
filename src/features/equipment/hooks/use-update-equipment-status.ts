import { useMutation, useQueryClient } from "@tanstack/react-query"
import { updateEquipmentStatus } from "../api/equipment-api"

export function useUpdateEquipmentStatus() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: updateEquipmentStatus,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["equipment"] })
    },
  })
}
