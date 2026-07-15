import { useMutation, useQueryClient } from "@tanstack/react-query"
import { updateDevice } from "@/features/equipment/api/equipment-api"

export function useUpdateEquipment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, ...data }: { id: string }) => updateDevice(id, data),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ["equipment"] })
      queryClient.invalidateQueries({ queryKey: ["equipment", variables.id] })
    },
  })
}
