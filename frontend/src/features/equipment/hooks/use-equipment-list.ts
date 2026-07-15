import { useQuery } from "@tanstack/react-query"
import { getDeviceList } from "@/features/equipment/api/equipment-api"

export function useEquipmentList(search?: string, location?: string) {
  const searchParams = new URLSearchParams()
  if (search) searchParams.set("search", search)
  if (location) searchParams.set("location", location)

  return useQuery({
    queryKey: ["equipment", "filters", search, location],
    queryFn: () => getDeviceList(),
  })
}
