import { useMutation, useQueryClient } from "@tanstack/react-query"
import { updateLocation } from "../api/location-api"
import type { UpdateLocationRequest } from "../types"

export function useUpdateLocation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, body }: { id: string; body: UpdateLocationRequest }) => updateLocation(id, body),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: ["locations"] })
      queryClient.invalidateQueries({ queryKey: ["location", id] })
    },
  })
}
