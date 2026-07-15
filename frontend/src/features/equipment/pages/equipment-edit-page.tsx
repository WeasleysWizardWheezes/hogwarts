import { useEquipmentForm } from "@/features/equipment/hooks/use-equipment-form"
import { useUpdateEquipment } from "@/features/equipment/hooks/use-update-equipment"
import { useEquipmentDetail } from "@/features/equipment/hooks/use-equipment-detail"
import { useNavigate, useParams } from "react-router"
import { Button } from "@/shared/components/ui/button"
import { Input } from "@/shared/components/ui/input"
import { Label } from "@/shared/components/ui/label"
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card"

export default function EquipmentEditPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const updateMutation = useUpdateEquipment()
  const equipmentQuery = useEquipmentDetail(id || "")

  const form = useEquipmentForm({
    onSubmit: (value) => {
      if (id) {
        updateMutation.mutate({ id, ...value })
      }
    },
    initialValues: equipmentQuery.data || undefined,
  })

  if (updateMutation.isSuccess) {
    navigate("/equipment")
  }

  if (equipmentQuery.isLoading) {
    return <div>Loading...</div>
  }

  if (!equipmentQuery.data) {
    return <div>Gerät nicht gefunden</div>
  }

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Gerät bearbeiten</h1>
        <p className="text-muted-foreground">
          Aktualisieren Sie die Gerätestammdaten.
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Gerätestammdaten</CardTitle>
          <CardDescription>
            Bitte füllen Sie alle Pflichtfelder aus.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form
            onSubmit={(e) => {
              e.preventDefault()
              e.stopPropagation()
              form.handleSubmit()
            }}
            className="space-y-4"
          >
            <div className="space-y-2">
              <Label htmlFor="name">Gerätename *</Label>
              <form.Field
                name="name"
                children={(field) => (
                  <Input
                    id={field.name}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => field.handleChange(e.target.value)}
                  />
                )}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="serialNumber">Seriennummer *</Label>
              <form.Field
                name="serialNumber"
                children={(field) => (
                  <Input
                    id={field.name}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => field.handleChange(e.target.value)}
                  />
                )}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="type">Typ *</Label>
              <form.Field
                name="type"
                children={(field) => (
                  <Input
                    id={field.name}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => field.handleChange(e.target.value)}
                  />
                )}
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="location">Lagerort *</Label>
              <form.Field
                name="location"
                children={(field) => (
                  <Input
                    id={field.name}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => field.handleChange(e.target.value)}
                  />
                )}
              />
            </div>

            <div className="flex justify-end gap-2">
              <Button
                type="button"
                variant="ghost"
                onClick={() => navigate("/equipment")}
              >
                Abbrechen
              </Button>
              <Button type="submit" disabled={updateMutation.isPending}>
                {updateMutation.isPending ? "Wird gespeichert..." : "Speichern"}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
