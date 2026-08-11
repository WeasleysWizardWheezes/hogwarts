import { useQuery } from "@tanstack/react-query"
import { getMemberById } from "../api/member-api"

export function useMember(id: string) {
  return useQuery({
    queryKey: ["member", id],
    queryFn: () => getMemberById(id),
    enabled: !!id,
  })
}
