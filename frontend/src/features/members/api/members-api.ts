import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { api } from "@/shared/api"
import type { components } from "@/shared/api"
import { toast } from "sonner"

export type MemberResponse = components["schemas"]["MemberResponse"]
export type MemberLocationAssignmentRequest = components["schemas"]["MemberLocationAssignmentRequest"]

export const memberKeys = {
  all: ["members"] as const,
  lists: () => [...memberKeys.all, "list"] as const,
  list: (page: number, size: number, locationId?: string) => [...memberKeys.lists(), { page, size, locationId }] as const,
  details: () => [...memberKeys.all, "detail"] as const,
  detail: (id: string) => [...memberKeys.details(), id] as const,
}

export function useMembers(filters: { page?: number; size?: number; locationId?: string } = {}) {
  const { page = 0, size = 20, locationId } = filters
  return useQuery({
    queryKey: memberKeys.list(page, size, locationId),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/members", {
        params: { query: { page, size, locationId } },
      })
      if (error) throw error
      return data
    },
  })
}

export function useAssignMemberToLocation() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({ memberId, body }: { memberId: string; body: MemberLocationAssignmentRequest }) => {
      const { data, error } = await api.POST("/api/v1/members/{memberId}/locations", {
        params: { path: { memberId } },
        body,
      })
      if (error) throw error
      return data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: memberKeys.lists() })
      toast.success("Mitglied erfolgreich Standort zugewiesen")
    },
    onError: () => {
      toast.error("Fehler beim Zuweisen")
    },
  })
}