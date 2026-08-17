import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { api } from "@/shared/api"
import type { components } from "@/shared/api"
import { toast } from "sonner"

export type VehicleGroupResponse = components["schemas"]["VehicleGroupResponse"]
export type CreateVehicleGroupRequest = components["schemas"]["CreateVehicleGroupRequest"]
export type UpdateVehicleGroupRequest = components["schemas"]["UpdateVehicleGroupRequest"]

export const vehicleGroupKeys = {
  all: ["vehicle-groups"] as const,
  lists: () => [...vehicleGroupKeys.all, "list"] as const,
  list: () => [...vehicleGroupKeys.lists()] as const,
  details: () => [...vehicleGroupKeys.all, "detail"] as const,
  detail: (id: string) => [...vehicleGroupKeys.details(), id] as const,
}

export function useVehicleGroups() {
  return useQuery({
    queryKey: vehicleGroupKeys.list(),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/vehicle-groups")
      if (error) throw error
      return data
    },
  })
}

export function useCreateVehicleGroup() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (body: CreateVehicleGroupRequest) => {
      const { data, error } = await api.POST("/api/v1/vehicle-groups", { body })
      if (error) throw error
      return data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: vehicleGroupKeys.lists() })
      toast.success("Fahrzeuggruppe erfolgreich erstellt")
    },
    onError: () => {
      toast.error("Fehler beim Erstellen")
    },
  })
}

export function useUpdateVehicleGroup() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({ vehicleGroupId, body }: { vehicleGroupId: string; body: UpdateVehicleGroupRequest }) => {
      const { data, error } = await api.PUT("/api/v1/vehicle-groups/{vehicleGroupId}", {
        params: { path: { vehicleGroupId } },
        body,
      })
      if (error) throw error
      return data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: vehicleGroupKeys.lists() })
      toast.success("Fahrzeuggruppe erfolgreich aktualisiert")
    },
    onError: () => {
      toast.error("Fehler beim Aktualisieren")
    },
  })
}

export function useDeleteVehicleGroup() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (vehicleGroupId: string) => {
      const { error } = await api.DELETE("/api/v1/vehicle-groups/{vehicleGroupId}", {
        params: { path: { vehicleGroupId } },
      })
      if (error) throw error
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: vehicleGroupKeys.lists() })
      toast.success("Fahrzeuggruppe erfolgreich gelöscht")
    },
    onError: () => {
      toast.error("Fehler beim Löschen")
    },
  })
}
