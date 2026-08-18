import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { api } from "@/shared/api"
import type { components } from "@/shared/api"
import { toast } from "sonner"

export type EquipmentResponse = components["schemas"]["EquipmentResponse"]
export type EquipmentHistoryResponse = components["schemas"]["EquipmentHistoryResponse"]
export type CreateEquipmentRequest = components["schemas"]["CreateEquipmentRequest"]
export type UpdateEquipmentRequest = components["schemas"]["UpdateEquipmentRequest"]
export type EquipmentStatus = NonNullable<EquipmentResponse["status"]>
export type EquipmentListStatus = Exclude<EquipmentStatus, "ARCHIVIERT">

export const equipmentKeys = {
  all: ["equipment"] as const,
  lists: () => [...equipmentKeys.all, "list"] as const,
  list: (filters: Record<string, string | number | undefined>) =>
    [...equipmentKeys.lists(), filters] as const,
  details: () => [...equipmentKeys.all, "detail"] as const,
  detail: (id: string) => [...equipmentKeys.details(), id] as const,
  history: (id: string) => [...equipmentKeys.detail(id), "history"] as const,
}

export function useEquipmentList(filters: {
  page: number
  size: number
  search?: string
  categoryId?: string
  vehicleId?: string
  status?: EquipmentListStatus
  dueBefore?: string
}) {
  return useQuery({
    queryKey: equipmentKeys.list(filters),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/equipment", { params: { query: filters } })
      if (error) throw error
      return data
    },
  })
}

export function useEquipment(id: string | undefined) {
  return useQuery({
    queryKey: equipmentKeys.detail(id ?? ""),
    enabled: Boolean(id),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/equipment/{equipmentId}", {
        params: { path: { equipmentId: id! } },
      })
      if (error) throw error
      return data
    },
  })
}

export function useEquipmentHistory(id: string | undefined) {
  return useQuery({
    queryKey: equipmentKeys.history(id ?? ""),
    enabled: Boolean(id),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/equipment/{equipmentId}/history", {
        params: { path: { equipmentId: id! } },
      })
      if (error) throw error
      return data
    },
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
    onError: () => toast.error("Gerät konnte nicht erstellt werden")
  })
}

export function useUpdateEquipment() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({ equipmentId, body }: { equipmentId: string; body: UpdateEquipmentRequest }) => {
      const { data, error } = await api.PUT("/api/v1/equipment/{equipmentId}", {
        params: { path: { equipmentId } }, body,
      })
      if (error) throw error
      return data
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: equipmentKeys.lists() })
      queryClient.invalidateQueries({ queryKey: equipmentKeys.detail(variables.equipmentId) })
      queryClient.invalidateQueries({ queryKey: equipmentKeys.history(variables.equipmentId) })
      toast.success("Gerät erfolgreich aktualisiert")
    },
    onError: () => toast.error("Gerät konnte nicht aktualisiert werden")
  })
}

export function useDeleteEquipment() {
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
      toast.success("Gerät archiviert")
    },
    onError: () => toast.error("Gerät konnte nicht archiviert werden")
  })
}