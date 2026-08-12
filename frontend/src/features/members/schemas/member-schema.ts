import * as v from "valibot"

export const createMemberSchema = v.object({
  firstName: v.pipe(
    v.string(),
    v.minLength(1, "Der Vorname ist erforderlich")
  ),
  lastName: v.pipe(
    v.string(),
    v.minLength(1, "Der Nachname ist erforderlich")
  ),
  email: v.pipe(
    v.string(),
    v.minLength(1, "Die E-Mail ist erforderlich"),
    v.email("Ungültige E-Mail-Adresse")
  ),
  phone: v.optional(v.string(), "" as const),
})

export const updateMemberSchema = v.object({
  firstName: v.optional(v.pipe(
    v.string(),
    v.minLength(1, "Der Vorname ist erforderlich")
  ), "" as const),
  lastName: v.optional(v.pipe(
    v.string(),
    v.minLength(1, "Der Nachname ist erforderlich")
  ), "" as const),
  email: v.optional(v.pipe(
    v.string(),
    v.minLength(1, "Die E-Mail ist erforderlich"),
    v.email("Ungültige E-Mail-Adresse")
  ), "" as const),
  phone: v.optional(v.string(), "" as const),
})

export type CreateMemberFormData = v.InferOutput<typeof createMemberSchema>
export type UpdateMemberFormData = v.InferOutput<typeof updateMemberSchema>
