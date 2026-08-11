import { useQuery } from "@tanstack/react-query"
import { getMemberById } from "../api/member-api"
import type { components } from "@/shared/api"

export function useMember(id: string) {
  return useQuery({
    queryKey: ["member", id],
    queryFn: () => getMemberById(id),
    enabled: !!id,
  })
}
