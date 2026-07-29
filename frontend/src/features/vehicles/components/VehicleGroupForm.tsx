import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import * as z from "zod"
import { useCreateVehicleGroup, useUpdateVehicleGroup } from "../hooks/use-vehicle-groups"
import { Button } from "@/shared/components/ui/button"
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/shared/components/ui/form"
import { Input } from "@/shared/components/ui/input"
import { Textarea } from "@/shared/components/ui/textarea"

interface VehicleGroup {
  id?: string
  name: string
  description?: string
}

const vehicleGroupFormSchema = z.object({
  name: z.string().min(1, "Name ist erforderlich"),
  description: z.string().optional(),
})

type VehicleGroupFormValues = z.infer<typeof vehicleGroupFormSchema>

interface VehicleGroupFormProps {
  group?: VehicleGroup
  onSuccess: () => void
}

export function VehicleGroupForm({ group, onSuccess }: VehicleGroupFormProps) {
  const form = useForm<VehicleGroupFormValues>({
    resolver: zodResolver(vehicleGroupFormSchema),
    defaultValues: {
      name: group?.name || "",
      description: group?.description || "",
    },
  })

  const createVehicleGroupMutation = useCreateVehicleGroup()
  const updateVehicleGroupMutation = useUpdateVehicleGroup()

  function onSubmit(data: VehicleGroupFormValues) {
    if (group) {
      updateVehicleGroupMutation.mutate(
        { id: group.id!, group: {
          name: data.name!,
          description: data.description,
        } },
        { onSuccess }
      )
    } else {
      const groupData = {
        name: data.name!,
        description: data.description,
      }
      createVehicleGroupMutation.mutate(groupData, { onSuccess })
    }
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
        <FormField
          control={form.control}
          name="name"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Name *</FormLabel>
              <FormControl>
                <Input {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="description"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Beschreibung</FormLabel>
              <FormControl>
                <Textarea {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="flex justify-end gap-2">
          <Button type="button" variant="outline">
            Abbrechen
          </Button>
          <Button type="submit" 
            disabled={createVehicleGroupMutation.isPending || updateVehicleGroupMutation.isPending}>
            {group ? "Fahrzeuggruppe aktualisieren" : "Fahrzeuggruppe erstellen"}
          </Button>
        </div>
      </form>
    </Form>
  )
}