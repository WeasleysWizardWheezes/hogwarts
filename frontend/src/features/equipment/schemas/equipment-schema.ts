import * as v from "valibot";

export const createEquipmentSchema = v.object({
  name: v.pipe(
    v.string(),
    v.minLength(1, "Gerätename ist erforderlich")
  ),
  serialNumber: v.pipe(
    v.string(),
    v.minLength(1, "Seriennummer ist erforderlich")
  ),
  type: v.pipe(
    v.string(),
    v.minLength(1, "Typ ist erforderlich")
  ),
  storageLocation: v.pipe(
    v.string(),
    v.minLength(1, "Lagerort ist erforderlich")
  ),
  description: v.optional(v.string()),
  acquisitionDate: v.optional(v.string()),
  manufacturer: v.optional(v.string()),
});

export type CreateEquipmentFormData = v.InferOutput<typeof createEquipmentSchema>;
