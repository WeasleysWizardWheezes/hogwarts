import * as v from "valibot"

export const createDeviceSchema = v.object({
  name: v.pipe(v.string(), v.minLength(1, "Gerätename ist erforderlich")),
  serialNumber: v.pipe(v.string(), v.minLength(1, "Seriennummer ist erforderlich")),
  type: v.pipe(v.string(), v.minLength(1, "Typ ist erforderlich")),
  location: v.pipe(v.string(), v.minLength(1, "Lagerort ist erforderlich")),
})

export const updateDeviceSchema = v.partial({
  name: v.pipe(v.string(), v.minLength(1, "Gerätename ist erforderlich")),
  serialNumber: v.pipe(v.string(), v.minLength(1, "Seriennummer ist erforderlich")),
  type: v.pipe(v.string(), v.minLength(1, "Typ ist erforderlich")),
  location: v.pipe(v.string(), v.minLength(1, "Lagerort ist erforderlich")),
})

export type CreateDeviceFormData = v.InferOutput<typeof createDeviceSchema>
export type UpdateDeviceFormData = v.InferOutput<typeof updateDeviceSchema>
