import { useForm } from "@tanstack/react-form"
import * as v from "valibot"
import { Button } from "@/shared/components/ui/button"
import { Input } from "@/shared/components/ui/input"
import { Label } from "@/shared/components/ui/label"
import { createMemberSchema, updateMemberSchema } from "../schemas/member-schema"

export function MemberForm({
  initialValues,
  onSubmit,
  isLoading = false,
}: {
  initialValues?: {
    firstName?: string
    lastName?: string
    email?: string
    phone?: string
  }
  onSubmit: (data: any) => void
  isLoading?: boolean
}) {
  const form = useForm({
    defaultValues: {
      firstName: initialValues?.firstName || "",
      lastName: initialValues?.lastName || "",
      email: initialValues?.email || "",
      phone: initialValues?.phone || "",
    },
    validators: {
      onBlur: ({ value }) => {
        try {
          const schema = initialValues ? updateMemberSchema : createMemberSchema
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
          name="firstName"
          children={(field) => (
            <div className="space-y-2">
              <Label htmlFor={field.name}>Vorname *</Label>
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
          name="lastName"
          children={(field) => (
            <div className="space-y-2">
              <Label htmlFor={field.name}>Nachname *</Label>
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

      <form.Field
        name="email"
        children={(field) => (
          <div className="space-y-2">
            <Label htmlFor={field.name}>E-Mail *</Label>
            <Input
              id={field.name}
              type="email"
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
        name="phone"
        children={(field) => (
          <div className="space-y-2">
            <Label htmlFor={field.name}>Telefon</Label>
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
