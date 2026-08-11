import { useMutation, useQueryClient } from "@tanstack/react-query"
import { updateMember } from "../api/member-api"
import type { UpdateMemberRequest } from "../types"

export function useUpdateMember() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, body }: { id: string; body: UpdateMemberRequest }) => updateMember(id, body),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: ["members"] })
      queryClient.invalidateQueries({ queryKey: ["member", id] })
    },
  })
}
