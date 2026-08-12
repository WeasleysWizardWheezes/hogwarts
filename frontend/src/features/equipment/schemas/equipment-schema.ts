import * as v from "valibot"

export const equipmentCategories = [
  "BREATHING_APPARATUS",
  "RADIO", 
  "TOOL",
  "VEHICLE_EQUIPMENT",
  "PROTECTIVE_CLOTHING",
  "OTHER"
] as const

export const equipmentStatuses = [
  "AVAILABLE",
  "IN_USE",
  "MAINTENANCE",
  "DEFECTIVE",
  "MISSING"
] as const

export const createEquipmentSchema = v.object({
  name: v.pipe(
    v.string(),
    v.minLength(1, "Der Gerätename ist erforderlich")
  ),
  inventoryNumber: v.pipe(
    v.string(),
    v.minLength(1, "Die Inventarnummer ist erforderlich")
  ),
  category: v.pipe(
    v.string(),
    v.minLength(1, "Die Kategorie ist erforderlich")
  ),
  status: v.pipe(
    v.string(),
    v.minLength(1, "Der Status ist erforderlich")
  ),
  locationId: v.pipe(
    v.string(),
    v.uuid("Ungültige Standort-ID")
  ),
  vehicleId: v.optional(v.pipe(
    v.string(),
    v.uuid("Ungültige Fahrzeug-ID")
  ), "" as const),
  purchaseDate: v.optional(v.string(), "" as const),
  nextInspectionDate: v.optional(v.string(), "" as const),
  notes: v.optional(v.string(), "" as const),
})

export const updateEquipmentSchema = v.object({
  name: v.optional(v.pipe(
    v.string(),
    v.minLength(1, "Der Gerätename ist erforderlich")
  ), "" as const),
  inventoryNumber: v.optional(v.pipe(
    v.string(),
    v.minLength(1, "Die Inventarnummer ist erforderlich")
  ), "" as const),
  category: v.optional(v.pipe(
    v.string(),
    v.minLength(1, "Die Kategorie ist erforderlich")
  ), "" as const),
  status: v.optional(v.pipe(
    v.string(),
    v.minLength(1, "Der Status ist erforderlich")
  ), "" as const),
  locationId: v.optional(v.pipe(
    v.string(),
    v.uuid("Ungültige Standort-ID")
  ), "" as const),
  vehicleId: v.optional(v.pipe(
    v.string(),
    v.uuid("Ungültige Fahrzeug-ID")
  ), "" as const),
  purchaseDate: v.optional(v.string(), "" as const),
  nextInspectionDate: v.optional(v.string(), "" as const),
  notes: v.optional(v.string(), "" as const),
})

export type CreateEquipmentFormData = v.InferOutput<typeof createEquipmentSchema>
export type UpdateEquipmentFormData = v.InferOutput<typeof updateEquipmentSchema>
