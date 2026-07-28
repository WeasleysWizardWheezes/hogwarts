import { useQuery } from "@tanstack/react-query"
import { getEquipmentList } from "../api/equipment-api"

export function useEquipmentList() {
  return useQuery({
    queryKey: ["equipment"],
    queryFn: getEquipmentList,
  })
}
