import { useQuery } from "@tanstack/react-query";
import { getEquipmentById } from "../api/equipment-api";

export function useEquipmentDetail(id: string) {
  return useQuery({
    queryKey: ["equipment", id],
    queryFn: () => getEquipmentById(id),
  });
}
