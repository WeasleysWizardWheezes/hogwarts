import { useMutation, useQueryClient } from "@tanstack/react-query"
import { deleteDevice } from "@/features/equipment/api/equipment-api"

export function useDeleteEquipment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: deleteDevice,
    onSuccess: (_data, id) => {
      queryClient.invalidateQueries({ queryKey: ["equipment"] })
      queryClient.invalidateQueries({ queryKey: ["equipment", id] })
    },
  })
}
