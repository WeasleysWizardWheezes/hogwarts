import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import * as z from "zod"
import { useCreateVehicle, useUpdateVehicle } from "../hooks/use-vehicles"
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/components/ui/select"

interface Vehicle {
  id?: string
  name: string
  licensePlate: string
  type: string
  status: string
  vehicleGroupId?: string
}

interface VehicleGroup {
  id: string
  name: string
  description?: string
}

const vehicleFormSchema = z.object({
  name: z.string().min(1, "Name is required"),
  licensePlate: z.string().min(1, "License plate is required"),
  type: z.string().min(1, "Type is required"),
  status: z.string().min(1, "Status is required"),
  vehicleGroupId: z.string().optional(),
})

type VehicleFormValues = z.infer<typeof vehicleFormSchema>

interface VehicleFormProps {
  vehicle?: Vehicle
  vehicleGroups: VehicleGroup[]
  onSuccess: () => void
}

export function VehicleForm({ vehicle, vehicleGroups, onSuccess }: VehicleFormProps) {
  const form = useForm<VehicleFormValues>({
    resolver: zodResolver(vehicleFormSchema),
    defaultValues: {
      name: vehicle?.name || "",
      licensePlate: vehicle?.licensePlate || "",
      type: vehicle?.type || "",
      status: vehicle?.status || "ACTIVE",
      vehicleGroupId: vehicle?.vehicleGroupId || "",
    },
  })

  const createVehicleMutation = useCreateVehicle()
  const updateVehicleMutation = useUpdateVehicle()

  function onSubmit(data: VehicleFormValues) {
    if (vehicle) {
      updateVehicleMutation.mutate(
        { id: vehicle.id!, vehicle: data },
        { onSuccess }
      )
    } else {
      createVehicleMutation.mutate(data, { onSuccess })
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
          name="licensePlate"
          render={({ field }) => (
            <FormItem>
              <FormLabel>License Plate</FormLabel>
              <FormControl>
                <Input {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="type"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Type</FormLabel>
              <FormControl>
                <Input {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="status"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Status</FormLabel>
              <FormControl>
                <Select
                  onValueChange={field.onChange}
                  defaultValue={field.value}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select status" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ACTIVE">Active</SelectItem>
                    <SelectItem value="INACTIVE">Inactive</SelectItem>
                    <SelectItem value="MAINTENANCE">Maintenance</SelectItem>
                  </SelectContent>
                </Select>
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="vehicleGroupId"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Vehicle Group</FormLabel>
              <FormControl>
                <Select
                  onValueChange={field.onChange}
                  defaultValue={field.value}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select vehicle group" />
                  </SelectTrigger>
                  <SelectContent>
                    {vehicleGroups.map((group) => (
                      <SelectItem key={group.id} value={group.id}>
                        {group.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
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
            disabled={createVehicleMutation.isPending || updateVehicleMutation.isPending}>
            {vehicle ? "Update Vehicle" : "Create Vehicle"}
          </Button>
        </div>
      </form>
    </Form>
  )
}