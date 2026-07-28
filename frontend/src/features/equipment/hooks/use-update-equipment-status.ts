import { useMutation, useQueryClient } from "@tanstack/react-query"
import { partialUpdateEquipment } from "../api/equipment-api"

export function useUpdateEquipmentStatus() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: partialUpdateEquipment,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["equipment"] })
    },
  })
}
