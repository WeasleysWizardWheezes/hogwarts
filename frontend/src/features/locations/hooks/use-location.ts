import { useQuery } from "@tanstack/react-query"
import { getLocationById } from "../api/location-api"

export function useLocation(id: string) {
  return useQuery({
    queryKey: ["location", id],
    queryFn: () => getLocationById(id),
    enabled: !!id,
  })
}
