import { useQuery } from "@tanstack/react-query";
import { getAllEquipment } from "../api/equipment-api";

export function useEquipmentList() {
  return useQuery({
    queryKey: ["equipment"],
    queryFn: getAllEquipment,
  });
}
