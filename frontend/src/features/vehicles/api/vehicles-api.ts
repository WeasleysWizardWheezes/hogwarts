import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { api } from "@/shared/api"
import type { components } from "@/shared/api"
import { toast } from "sonner"

export type VehicleResponse = components["schemas"]["VehicleResponse"]
export type CreateVehicleRequest = components["schemas"]["CreateVehicleRequest"]
export type UpdateVehicleRequest = components["schemas"]["UpdateVehicleRequest"]
export type VehicleStatus = NonNullable<VehicleResponse["status"]>

export const vehicleKeys = {
  all: ["vehicles"] as const,
  lists: () => [...vehicleKeys.all, "list"] as const,
  list: (filters?: { vehicleGroupId?: string; status?: string }) =>
    [...vehicleKeys.lists(), filters] as const,
  details: () => [...vehicleKeys.all, "detail"] as const,
  detail: (id: string) => [...vehicleKeys.details(), id] as const,
}

export function useVehicles(filters?: {
  vehicleGroupId?: string
  status?: VehicleStatus
}) {
  return useQuery({
    queryKey: vehicleKeys.list(filters),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/vehicles", {
        params: {
          query: {
            vehicleGroupId: filters?.vehicleGroupId,
            status: filters?.status,
          },
        },
      })
      if (error) throw error
      return data
    },
  })
}

export function useCreateVehicle() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (body: CreateVehicleRequest) => {
      const { data, error } = await api.POST("/api/v1/vehicles", { body })
      if (error) throw error
      return data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: vehicleKeys.lists() })
      toast.success("Fahrzeug erfolgreich erstellt")
    },
    onError: () => {
      toast.error("Fehler beim Erstellen")
    },
  })
}

export function useUpdateVehicle() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({ vehicleId, body }: { vehicleId: string; body: UpdateVehicleRequest }) => {
      const { data, error } = await api.PUT("/api/v1/vehicles/{vehicleId}", {
        params: { path: { vehicleId } },
        body,
      })
      if (error) throw error
      return data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: vehicleKeys.lists() })
      toast.success("Fahrzeug erfolgreich aktualisiert")
    },
    onError: () => {
      toast.error("Fehler beim Aktualisieren")
    },
  })
}

export function useDeleteVehicle() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (vehicleId: string) => {
      const { error } = await api.DELETE("/api/v1/vehicles/{vehicleId}", {
        params: { path: { vehicleId } },
      })
      if (error) throw error
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: vehicleKeys.lists() })
      toast.success("Fahrzeug erfolgreich gelöscht")
    },
    onError: () => {
      toast.error("Fehler beim Löschen")
    },
  })
}
