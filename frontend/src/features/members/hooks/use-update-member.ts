import { useMutation, useQueryClient } from "@tanstack/react-query"
import { updateMember } from "../api/member-api"

export function useUpdateMember() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, body }: { id: string; body: any }) => updateMember(id, body),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: ["members"] })
      queryClient.invalidateQueries({ queryKey: ["member", id] })
    },
  })
}
