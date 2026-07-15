import { useMutation, useQueryClient } from "@tanstack/react-query"
import { createDevice } from "@/features/equipment/api/equipment-api"

export function useCreateEquipment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: createDevice,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["equipment"] })
    },
  })
}
