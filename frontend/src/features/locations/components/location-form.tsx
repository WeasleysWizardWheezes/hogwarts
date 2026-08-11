import { useForm } from "@tanstack/react-form"
import { Button } from "@/shared/components/ui/button"
import { Input } from "@/shared/components/ui/input"
import { Label } from "@/shared/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/shared/components/ui/select"
import { Textarea } from "@/shared/components/ui/textarea"
import { createLocationSchema, updateLocationSchema } from "../schemas/location-schema"
import type { LocationType } from "../types"

export function LocationForm({
  initialValues,
  onSubmit,
  isLoading = false,
}: {
  initialValues?: {
    name?: string
    address?: string
    type?: LocationType
  }
  onSubmit: (data: any) => void
  isLoading?: boolean
}) {
  const form = useForm({
    defaultValues: {
      name: initialValues?.name || "",
      address: initialValues?.address || "",
      type: initialValues?.type || "",
    },
    validators: {
      onBlur: initialValues ? updateLocationSchema : createLocationSchema,
    },
    onSubmit: ({ value }) => {
      onSubmit(value)
    },
  })

  const locationTypes: { value: LocationType; label: string }[] = [
    { value: "FIRE_STATION", label: "Feuerwache" },
    { value: "EQUIPMENT_DEPOT", label: "Gerätedepot" },
    { value: "TRAINING_CENTER", label: "Ausbildungszentrum" },
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
      <form.Field
        name="name"
        children={(field) => (
          <div className="space-y-2">
            <Label htmlFor={field.name}>Name *</Label>
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
        name="address"
        children={(field) => (
          <div className="space-y-2">
            <Label htmlFor={field.name}>Adresse</Label>
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

      <form.Field
        name="type"
        children={(field) => (
          <div className="space-y-2">
            <Label htmlFor={field.name}>Typ *</Label>
            <Select
              value={field.state.value}
              onValueChange={(value) => field.handleChange(value)}
              disabled={isLoading}
            >
              <SelectTrigger id={field.name}>
                <SelectValue placeholder="Standorttyp auswählen" />
              </SelectTrigger>
              <SelectContent>
                {locationTypes.map((type) => (
                  <SelectItem key={type.value} value={type.value}>
                    {type.label}
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
