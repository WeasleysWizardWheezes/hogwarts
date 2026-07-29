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
  year?: number
  status: string
  vehicleGroupId?: string
}

interface VehicleGroup {
  id: string
  name: string
  description?: string
}

const vehicleFormSchema = z.object({
  name: z.string().min(1, "Name ist erforderlich"),
  licensePlate: z.string().min(1, "Funkrufname ist erforderlich"),
  type: z.string().min(1, "Kennzeichen ist erforderlich"),
  year: z.number().min(1900, "Baujahr muss mindestens 1900 sein").max(new Date().getFullYear(), "Baujahr darf nicht in der Zukunft liegen"),
  status: z.string().min(1, "Status ist erforderlich"),
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
      year: vehicle?.year || new Date().getFullYear(),
      status: vehicle?.status || "VERFUEGBAR",
      vehicleGroupId: vehicle?.vehicleGroupId || "",
    },
  })

  const createVehicleMutation = useCreateVehicle()
  const updateVehicleMutation = useUpdateVehicle()

  function onSubmit(data: VehicleFormValues) {
    if (vehicle) {
      updateVehicleMutation.mutate(
        { id: vehicle.id!, vehicle: {
          name: data.name!,
          licensePlate: data.licensePlate!,
          type: data.type!,
          year: data.year!,
          status: data.status!,
          vehicleGroupId: data.vehicleGroupId,
        } },
        { onSuccess }
      )
    } else {
      const vehicleData = {
        name: data.name!,
        licensePlate: data.licensePlate!,
        type: data.type!,
        year: data.year!,
        status: data.status!,
        vehicleGroupId: data.vehicleGroupId,
      }
      createVehicleMutation.mutate(vehicleData, { onSuccess })
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
          name="licensePlate"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Funkrufname *</FormLabel>
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
              <FormLabel>Kennzeichen *</FormLabel>
              <FormControl>
                <Input {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="year"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Baujahr *</FormLabel>
              <FormControl>
                <Input type="number" {...field} />
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
              <FormLabel>Status *</FormLabel>
              <FormControl>
                <Select
                  onValueChange={field.onChange}
                  defaultValue={field.value}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Status auswählen" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="VERFUEGBAR">Verfügbar</SelectItem>
                    <SelectItem value="IM_EINSATZ">Im Einsatz</SelectItem>
                    <SelectItem value="WARTUNG_REPARATUR">Wartung/Reparatur</SelectItem>
                    <SelectItem value="DEFEKT">Defekt</SelectItem>
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
              <FormLabel>Fahrzeuggruppe</FormLabel>
              <FormControl>
                <Select
                  onValueChange={field.onChange}
                  defaultValue={field.value}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Fahrzeuggruppe auswählen" />
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
            Abbrechen
          </Button>
          <Button type="submit" 
            disabled={createVehicleMutation.isPending || updateVehicleMutation.isPending}>
            {vehicle ? "Fahrzeug aktualisieren" : "Fahrzeug erstellen"}
          </Button>
        </div>
      </form>
    </Form>
  )
}