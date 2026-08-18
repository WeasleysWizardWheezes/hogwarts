export {
  useEquipmentList,
  useEquipment,
  useEquipmentHistory,
  useCreateEquipment,
  useUpdateEquipment,
  useArchiveEquipment,
  equipmentKeys,
} from "./api/equipment-api"
export type {
  EquipmentResponse,
  EquipmentStatus,
  EquipmentHistoryResponse,
} from "./api/equipment-api"

export {
  useEquipmentCategories,
  useCreateEquipmentCategory,
  useUpdateEquipmentCategory,
  useArchiveEquipmentCategory,
  equipmentCategoryKeys,
} from "./api/equipment-categories-api"
export type { EquipmentCategoryResponse } from "./api/equipment-categories-api"
