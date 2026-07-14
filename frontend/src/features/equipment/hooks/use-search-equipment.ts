import { useQuery } from "@tanstack/react-query";
import { searchEquipment } from "../api/equipment-api";

export function useSearchEquipment(params: {
  name?: string;
  serialNumber?: string;
  storageLocation?: string;
}) {
  return useQuery({
    queryKey: ["equipment", "search", params],
    queryFn: () => searchEquipment(params),
  });
}
