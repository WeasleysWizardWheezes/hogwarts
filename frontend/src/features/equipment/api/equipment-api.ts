import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { api } from "@/shared/api"
import type { components } from "@/shared/api"
import { toast } from "sonner"

export type EquipmentResponse = components["schemas"]["EquipmentResponse"]
export type CreateEquipmentRequest = components["schemas"]["CreateEquipmentRequest"]
export type UpdateEquipmentRequest = components["schemas"]["UpdateEquipmentRequest"]
export type EquipmentHistoryResponse = components["schemas"]["EquipmentHistoryResponse"]
export type EquipmentStatus = NonNullable<EquipmentResponse["status"]>

export interface EquipmentFilters {
  page?: number
  size?: number
  search?: string
  categoryId?: string
  vehicleId?: string
  status?: EquipmentStatus
  dueBefore?: string
}

export const equipmentKeys = {
  all: ["equipment"] as const,
  lists: () => [...equipmentKeys.all, "list"] as const,
  list: (filters?: EquipmentFilters) => [...equipmentKeys.lists(), filters] as const,
  details: () => [...equipmentKeys.all, "detail"] as const,
  detail: (id: string) => [...equipmentKeys.details(), id] as const,
  history: (id: string) => [...equipmentKeys.all, "history", id] as const,
}

export function useEquipmentList(filters?: EquipmentFilters) {
  const { page = 0, size = 20, search, categoryId, vehicleId, status, dueBefore } = filters ?? {}
  return useQuery({
    queryKey: equipmentKeys.list(filters),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/equipment", {
        params: {
          query: { page, size, search, categoryId, vehicleId, status, dueBefore },
        },
      })
      if (error) throw error
      return data
    },
  })
}

export function useEquipment(equipmentId: string) {
  return useQuery({
    queryKey: equipmentKeys.detail(equipmentId),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/equipment/{equipmentId}", {
        params: { path: { equipmentId } },
      })
      if (error) throw error
      return data
    },
    enabled: !!equipmentId,
  })
}

export function useEquipmentHistory(equipmentId: string) {
  return useQuery({
    queryKey: equipmentKeys.history(equipmentId),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/equipment/{equipmentId}/history", {
        params: { path: { equipmentId } },
      })
      if (error) throw error
      return data
    },
    enabled: !!equipmentId,
  })
}

export function useCreateEquipment() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (body: CreateEquipmentRequest) => {
      const { data, error } = await api.POST("/api/v1/equipment", { body })
      if (error) throw error
      return data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: equipmentKeys.lists() })
      toast.success("Gerät erfolgreich erstellt")
    },
    onError: () => {
      toast.error("Fehler beim Erstellen")
    },
  })
}

export function useUpdateEquipment() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({
      equipmentId,
      body,
    }: {
      equipmentId: string
      body: UpdateEquipmentRequest
    }) => {
      const { data, error } = await api.PUT("/api/v1/equipment/{equipmentId}", {
        params: { path: { equipmentId } },
        body,
      })
      if (error) throw error
      return data
    },
    onSuccess: (_data, { equipmentId }) => {
      queryClient.invalidateQueries({ queryKey: equipmentKeys.lists() })
      queryClient.invalidateQueries({ queryKey: equipmentKeys.detail(equipmentId) })
      toast.success("Gerät erfolgreich aktualisiert")
    },
    onError: () => {
      toast.error("Fehler beim Aktualisieren")
    },
  })
}

export function useArchiveEquipment() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (equipmentId: string) => {
      const { error } = await api.DELETE("/api/v1/equipment/{equipmentId}", {
        params: { path: { equipmentId } },
      })
      if (error) throw error
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: equipmentKeys.lists() })
      toast.success("Gerät erfolgreich archiviert")
    },
    onError: () => {
      toast.error("Fehler beim Archivieren")
    },
  })
}
