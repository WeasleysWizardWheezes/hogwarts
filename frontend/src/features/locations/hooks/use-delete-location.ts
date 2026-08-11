import { useMutation, useQueryClient } from "@tanstack/react-query"
import { deleteLocation } from "../api/location-api"

export function useDeleteLocation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: deleteLocation,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["locations"] })
    },
  })
}
