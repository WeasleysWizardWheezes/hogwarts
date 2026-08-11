import * as v from "valibot"

export const locationTypes = ["FIRE_STATION", "EQUIPMENT_DEPOT", "TRAINING_CENTER"] as const

export const createLocationSchema = v.object({
  name: v.pipe(
    v.string(),
    v.minLength(1, "Der Name ist erforderlich")
  ),
  address: v.optional(v.string()),
  type: v.pipe(
    v.string(),
    v.minLength(1, "Der Typ ist erforderlich"),
    v.includes(locationTypes, "Ungültiger Standorttyp")
  ),
})

export const updateLocationSchema = v.object({
  name: v.optional(v.pipe(
    v.string(),
    v.minLength(1, "Der Name ist erforderlich")
  )),
  address: v.optional(v.string()),
  type: v.optional(v.pipe(
    v.string(),
    v.minLength(1, "Der Typ ist erforderlich"),
    v.includes(locationTypes, "Ungültiger Standorttyp")
  )),
})

export type CreateLocationFormData = v.InferOutput<typeof createLocationSchema>
export type UpdateLocationFormData = v.InferOutput<typeof updateLocationSchema>
