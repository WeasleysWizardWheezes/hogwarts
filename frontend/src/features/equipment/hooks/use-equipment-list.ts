import { useQuery } from "@tanstack/react-query"
import { getDeviceList } from "@/features/equipment/api/equipment-api"

export function useEquipmentList(search?: string, location?: string) {
  return useQuery({
    queryKey: ["equipment", "filters", search, location],
    queryFn: () => getDeviceList(),
  })
}
