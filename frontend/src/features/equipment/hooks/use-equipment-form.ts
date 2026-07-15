import { useForm } from "@tanstack/react-form"
import * as v from "valibot"
import { createDeviceSchema, updateDeviceSchema } from "@/features/equipment/schemas/equipment-schema"
import type { CreateDeviceFormData, UpdateDeviceFormData } from "@/features/equipment/schemas/equipment-schema"

export function useEquipmentForm(options: { 
  onSubmit: (data: CreateDeviceFormData | UpdateDeviceFormData) => void,
  initialValues?: Partial<CreateDeviceFormData>
}) {
  return useForm({
    defaultValues: {
      name: options.initialValues?.name || "",
      serialNumber: options.initialValues?.serialNumber || "",
      type: options.initialValues?.type || "",
      location: options.initialValues?.location || "",
    },
    validators: {
      onBlur: options.initialValues ? updateDeviceSchema : createDeviceSchema,
    },
    onSubmit: ({ value }) => {
      options.onSubmit(value)
    },
  })
}
