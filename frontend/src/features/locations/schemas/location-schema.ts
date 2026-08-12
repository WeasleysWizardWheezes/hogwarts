import * as v from "valibot"

export const locationTypes = ["FIRE_STATION", "EQUIPMENT_DEPOT", "TRAINING_CENTER"] as const

export const createLocationSchema = v.object({
  name: v.pipe(
    v.string(),
    v.minLength(1, "Der Name ist erforderlich")
  ),
  address: v.optional(v.string(), "" as const),
  type: v.pipe(
    v.string(),
    v.minLength(1, "Der Typ ist erforderlich")
  ),
})

export const updateLocationSchema = v.object({
  name: v.optional(v.pipe(
    v.string(),
    v.minLength(1, "Der Name ist erforderlich")
  ), "" as const),
  address: v.optional(v.string(), "" as const),
  type: v.optional(v.pipe(
    v.string(),
    v.minLength(1, "Der Typ ist erforderlich")
  ), "" as const),
})

export type CreateLocationFormData = v.InferOutput<typeof createLocationSchema>
export type UpdateLocationFormData = v.InferOutput<typeof updateLocationSchema>
