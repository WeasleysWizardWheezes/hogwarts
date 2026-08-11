import { useQuery } from "@tanstack/react-query"
import { getLocations } from "../api/location-api"

export function useLocations(page: number = 0, size: number = 10) {
  return useQuery({
    queryKey: ["locations", page, size],
    queryFn: () => getLocations(page, size),
  })
}
