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

interface VehicleGroupFormValues extends Omit<VehicleGroup, 'id'> {}

const vehicleGroupFormSchema = z.object({
  name: z.string().min(1, "Name is required"),
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
        { id: group.id!, group: data },
        { onSuccess }
      )
    } else {
      createVehicleGroupMutation.mutate(data, { onSuccess })
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
              <FormLabel>Name</FormLabel>
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
              <FormLabel>Description</FormLabel>
              <FormControl>
                <Textarea {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="flex justify-end gap-2">
          <Button type="button" variant="outline">
            Cancel
          </Button>
          <Button type="submit" 
            disabled={createVehicleGroupMutation.isPending || updateVehicleGroupMutation.isPending}>
            {group ? "Update Vehicle Group" : "Create Vehicle Group"}
          </Button>
        </div>
      </form>
    </Form>
  )
}