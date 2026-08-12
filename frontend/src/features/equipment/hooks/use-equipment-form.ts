import { useForm } from "@tanstack/react-form"
import * as v from "valibot"
import { createEquipmentSchema, updateEquipmentSchema } from "../schemas/equipment-schema"
import type { CreateEquipmentFormData, UpdateEquipmentFormData } from "../schemas/equipment-schema"

export function useEquipmentForm(options: {
  initialValues?: Partial<UpdateEquipmentFormData>
  onSubmit: (data: CreateEquipmentFormData | UpdateEquipmentFormData) => void
}) {
  return useForm({
    defaultValues: {
      name: options.initialValues?.name || "",
      inventoryNumber: options.initialValues?.inventoryNumber || "",
      category: options.initialValues?.category || "",
      status: options.initialValues?.status || "",
      locationId: options.initialValues?.locationId || "",
      vehicleId: options.initialValues?.vehicleId || "",
      purchaseDate: options.initialValues?.purchaseDate || "",
      nextInspectionDate: options.initialValues?.nextInspectionDate || "",
      notes: options.initialValues?.notes || "",
    },
    validators: {
      onBlur: ({ value }) => {
        try {
          const schema = options.initialValues ? updateEquipmentSchema : createEquipmentSchema
          v.parse(schema, value)
          return { valid: true }
        } catch (error) {
          if (error instanceof Error) {
            return { valid: false, error: error.message }
          }
          return { valid: false, error: "Unbekannter Validierungsfehler" }
        }
      },
    },
    onSubmit: ({ value }) => {
      options.onSubmit(value)
    },
  })
}
