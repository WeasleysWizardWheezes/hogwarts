import { useMutation, useQueryClient } from "@tanstack/react-query"
import { updateLocation } from "../api/location-api"

export function useUpdateLocation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, body }: { id: string; body: any }) => updateLocation(id, body),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: ["locations"] })
      queryClient.invalidateQueries({ queryKey: ["location", id] })
    },
  })
}
