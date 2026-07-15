import { useQuery } from "@tanstack/react-query"
import { getDeviceById } from "@/features/equipment/api/equipment-api"

export function useEquipmentDetail(id: string) {
  return useQuery({
    queryKey: ["equipment", id],
    queryFn: () => getDeviceById(id),
    enabled: !!id,
  })
}
