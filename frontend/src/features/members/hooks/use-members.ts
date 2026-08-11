import { useQuery } from "@tanstack/react-query"
import { getMembers } from "../api/member-api"

export function useMembers(page: number = 0, size: number = 10, locationId?: string) {
  return useQuery({
    queryKey: ["members", page, size, locationId],
    queryFn: () => getMembers(page, size, locationId),
  })
}
