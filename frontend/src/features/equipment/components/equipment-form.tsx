import { useForm } from "@tanstack/react-form"
import * as v from "valibot"
import { Button } from "@/shared/components/ui/button"
import { Input } from "@/shared/components/ui/input"
import { Label } from "@/shared/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/shared/components/ui/select"
import { Textarea } from "@/shared/components/ui/textarea"
import { createEquipmentSchema, updateEquipmentSchema } from "../schemas/equipment-schema"
import type { EquipmentCategory, EquipmentStatus } from "../types"

export function EquipmentForm({
  initialValues,
  onSubmit,
  isLoading = false,
}: {
  initialValues?: {
    name?: string
    inventoryNumber?: string
    category?: EquipmentCategory
    status?: EquipmentStatus
    locationId?: string
    vehicleId?: string
    purchaseDate?: string
    nextInspectionDate?: string
    notes?: string
  }
  onSubmit: (data: any) => void
  isLoading?: boolean
}) {
  const form = useForm({
    defaultValues: {
      name: initialValues?.name || "",
      inventoryNumber: initialValues?.inventoryNumber || "",
      category: initialValues?.category || "",
      status: initialValues?.status || "",
      locationId: initialValues?.locationId || "",
      vehicleId: initialValues?.vehicleId || "",
      purchaseDate: initialValues?.purchaseDate || "",
      nextInspectionDate: initialValues?.nextInspectionDate || "",
      notes: initialValues?.notes || "",
    },
    validators: {
      onBlur: ({ value }) => {
        try {
          const schema = initialValues ? updateEquipmentSchema : createEquipmentSchema
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
      onSubmit(value)
    },
  })

  const equipmentCategories: { value: EquipmentCategory; label: string }[] = [
    { value: "BREATHING_APPARATUS", label: "Atemschutzgerät" },
    { value: "RADIO", label: "Funkgerät" },
    { value: "TOOL", label: "Werkzeug" },
    { value: "VEHICLE_EQUIPMENT", label: "Fahrzeugausrüstung" },
    { value: "PROTECTIVE_CLOTHING", label: "Schutzausrüstung" },
    { value: "OTHER", label: "Sonstiges" },
  ]

  const equipmentStatuses: { value: EquipmentStatus; label: string }[] = [
    { value: "AVAILABLE", label: "Verfügbar" },
    { value: "IN_USE", label: "In Gebrauch" },
    { value: "MAINTENANCE", label: "In Wartung" },
    { value: "DEFECTIVE", label: "Defekt" },
    { value: "MISSING", label: "Fehlend" },
  ]

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault()
        e.stopPropagation()
        form.handleSubmit()
      }}
      className="space-y-6"
    >
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <form.Field
          name="name"
          children={(field) => (
            <div className="space-y-2">
              <Label htmlFor={field.name}>Gerätename *</Label>
              <Input
                id={field.name}
                value={field.state.value}
                onBlur={field.handleBlur}
                onChange={(e) => field.handleChange(e.target.value)}
                disabled={isLoading}
              />
              {!field.state.meta.isValid && (
                <p className="text-sm text-destructive">
                  {field.state.meta.errors.join(", ")}
                </p>
              )}
            </div>
          )}
        />

        <form.Field
          name="inventoryNumber"
          children={(field) => (
            <div className="space-y-2">
              <Label htmlFor={field.name}>Inventarnummer *</Label>
              <Input
                id={field.name}
                value={field.state.value}
                onBlur={field.handleBlur}
                onChange={(e) => field.handleChange(e.target.value)}
                disabled={isLoading}
              />
              {!field.state.meta.isValid && (
                <p className="text-sm text-destructive">
                  {field.state.meta.errors.join(", ")}
                </p>
              )}
            </div>
          )}
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <form.Field
          name="category"
          children={(field) => (
            <div className="space-y-2">
              <Label htmlFor={field.name}>Kategorie *</Label>
              <Select
                value={field.state.value ?? ""}
                onValueChange={(value) => field.handleChange(value)}
                disabled={isLoading}
              >
                <SelectTrigger id={field.name}>
                  <SelectValue placeholder="Kategorie auswählen" />
                </SelectTrigger>
                <SelectContent>
                  {equipmentCategories.map((category) => (
                    <SelectItem key={category.value} value={category.value}>
                      {category.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {!field.state.meta.isValid && (
                <p className="text-sm text-destructive">
                  {field.state.meta.errors.join(", ")}
                </p>
              )}
            </div>
          )}
        />

        <form.Field
          name="status"
          children={(field) => (
            <div className="space-y-2">
              <Label htmlFor={field.name}>Status *</Label>
              <Select
                value={field.state.value ?? ""}
                onValueChange={(value) => field.handleChange(value)}
                disabled={isLoading}
              >
                <SelectTrigger id={field.name}>
                  <SelectValue placeholder="Status auswählen" />
                </SelectTrigger>
                <SelectContent>
                  {equipmentStatuses.map((status) => (
                    <SelectItem key={status.value} value={status.value}>
                      {status.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {!field.state.meta.isValid && (
                <p className="text-sm text-destructive">
                  {field.state.meta.errors.join(", ")}
                </p>
              )}
            </div>
          )}
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <form.Field
          name="locationId"
          children={(field) => (
            <div className="space-y-2">
              <Label htmlFor={field.name}>Standort *</Label>
              <Select
                value={field.state.value ?? ""}
                onValueChange={(value) => field.handleChange(value)}
                disabled={isLoading}
              >
                <SelectTrigger id={field.name}>
                  <SelectValue placeholder="Standort auswählen" />
                </SelectTrigger>
                <SelectContent>
                  {/* Standort-Optionen würden hier dynamisch geladen werden */}
                  <SelectItem value="">Standort auswählen</SelectItem>
                </SelectContent>
              </Select>
              {!field.state.meta.isValid && (
                <p className="text-sm text-destructive">
                  {field.state.meta.errors.join(", ")}
                </p>
              )}
            </div>
          )}
        />

        <form.Field
          name="vehicleId"
          children={(field) => (
            <div className="space-y-2">
              <Label htmlFor={field.name}>Fahrzeug (optional)</Label>
              <Select
                value={field.state.value ?? ""}
                onValueChange={(value) => field.handleChange(value)}
                disabled={isLoading}
              >
                <SelectTrigger id={field.name}>
                  <SelectValue placeholder="Fahrzeug auswählen" />
                </SelectTrigger>
                <SelectContent>
                  {/* Fahrzeug-Optionen würden hier dynamisch geladen werden */}
                  <SelectItem value="">Kein Fahrzeug</SelectItem>
                </SelectContent>
              </Select>
              {!field.state.meta.isValid && (
                <p className="text-sm text-destructive">
                  {field.state.meta.errors.join(", ")}
                </p>
              )}
            </div>
          )}
        />
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <form.Field
          name="purchaseDate"
          children={(field) => (
            <div className="space-y-2">
              <Label htmlFor={field.name}>Kaufdatum (optional)</Label>
              <Input
                id={field.name}
                type="date"
                value={field.state.value}
                onBlur={field.handleBlur}
                onChange={(e) => field.handleChange(e.target.value)}
                disabled={isLoading}
              />
              {!field.state.meta.isValid && (
                <p className="text-sm text-destructive">
                  {field.state.meta.errors.join(", ")}
                </p>
              )}
            </div>
          )}
        />

        <form.Field
          name="nextInspectionDate"
          children={(field) => (
            <div className="space-y-2">
              <Label htmlFor={field.name}>Nächste Prüfung (optional)</Label>
              <Input
                id={field.name}
                type="date"
                value={field.state.value}
                onBlur={field.handleBlur}
                onChange={(e) => field.handleChange(e.target.value)}
                disabled={isLoading}
              />
              {!field.state.meta.isValid && (
                <p className="text-sm text-destructive">
                  {field.state.meta.errors.join(", ")}
                </p>
              )}
            </div>
          )}
        />
      </div>

      <form.Field
        name="notes"
        children={(field) => (
          <div className="space-y-2">
            <Label htmlFor={field.name}>Bemerkungen (optional)</Label>
            <Textarea
              id={field.name}
              value={field.state.value}
              onBlur={field.handleBlur}
              onChange={(e) => field.handleChange(e.target.value)}
              disabled={isLoading}
              rows={3}
            />
            {!field.state.meta.isValid && (
              <p className="text-sm text-destructive">
                {field.state.meta.errors.join(", ")}
              </p>
            )}
          </div>
        )}
      />

      <div className="flex justify-end">
        <form.Subscribe
          selector={(state) => [state.canSubmit, state.isSubmitting]}
          children={([canSubmit, _isSubmitting]) => (
            <Button type="submit" disabled={!canSubmit || isLoading}>
              {isLoading ? "Wird gespeichert..." : "Speichern"}
            </Button>
          )}
        />
      </div>
    </form>
  )
}
