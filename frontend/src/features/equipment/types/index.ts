export type EquipmentStatus = "AVAILABLE" | "IN_USE" | "MAINTENANCE" | "DEFECTIVE" | "MISSING"

export type EquipmentCategory = "BREATHING_APPARATUS" | "RADIO" | "TOOL" | "VEHICLE_EQUIPMENT" | "PROTECTIVE_CLOTHING" | "OTHER"

export type EquipmentFormData = {
  name: string
  inventoryNumber: string
  category: EquipmentCategory
  status: EquipmentStatus
  locationId: string
  vehicleId?: string
  purchaseDate?: string
  nextInspectionDate?: string
  notes?: string
}

export type Equipment = EquipmentFormData & {
  id: string
  createdAt: string
  updatedAt: string
}
