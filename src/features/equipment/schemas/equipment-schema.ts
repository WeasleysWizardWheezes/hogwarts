import * as v from "valibot"

export const createEquipmentSchema = v.object({
  name: v.pipe(v.string(), v.minLength(1, "Gerätename ist erforderlich")),
  category: v.pipe(v.string(), v.minLength(1, "Kategorie ist erforderlich")),
  locationId: v.pipe(v.string(), v.uuid("Ungültiger Standort")),
  status: v.pipe(v.string(), v.minLength(1, "Status ist erforderlich")),
  notes: v.optional(v.string()),
})

export type CreateEquipmentFormData = v.InferOutput<typeof createEquipmentSchema>
