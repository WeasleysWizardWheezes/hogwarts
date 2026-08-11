import { useMutation, useQueryClient } from "@tanstack/react-query"
import { assignMemberToLocation } from "../api/location-api"

export function useAssignMemberToLocation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ memberId, locationId }: { memberId: string; locationId: string }) =>
      assignMemberToLocation(memberId, locationId),
    onSuccess: (_, { memberId }) => {
      queryClient.invalidateQueries({ queryKey: ["member", memberId] })
      queryClient.invalidateQueries({ queryKey: ["members"] })
    },
  })
}
