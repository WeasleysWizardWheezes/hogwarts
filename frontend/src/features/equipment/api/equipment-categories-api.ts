import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { api } from "@/shared/api"
import type { components } from "@/shared/api"
import { toast } from "sonner"

export type EquipmentCategoryResponse = components["schemas"]["EquipmentCategoryResponse"]
export type CreateEquipmentCategoryRequest = components["schemas"]["CreateEquipmentCategoryRequest"]
export type UpdateEquipmentCategoryRequest = components["schemas"]["UpdateEquipmentCategoryRequest"]

export const equipmentCategoryKeys = {
  all: ["equipment-categories"] as const,
  lists: () => [...equipmentCategoryKeys.all, "list"] as const,
  list: (params?: { page?: number; size?: number; search?: string }) =>
    [...equipmentCategoryKeys.lists(), params] as const,
  details: () => [...equipmentCategoryKeys.all, "detail"] as const,
  detail: (id: string) => [...equipmentCategoryKeys.details(), id] as const,
}

export function useEquipmentCategories(params?: { page?: number; size?: number; search?: string }) {
  const { page = 0, size = 100, search } = params ?? {}
  return useQuery({
    queryKey: equipmentCategoryKeys.list({ page, size, search }),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/equipment-categories", {
        params: { query: { page, size, search } },
      })
      if (error) throw error
      return data
    },
  })
}

export function useCreateEquipmentCategory() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (body: CreateEquipmentCategoryRequest) => {
      const { data, error } = await api.POST("/api/v1/equipment-categories", { body })
      if (error) throw error
      return data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: equipmentCategoryKeys.lists() })
      toast.success("Kategorie erfolgreich erstellt")
    },
    onError: () => {
      toast.error("Fehler beim Erstellen")
    },
  })
}

export function useUpdateEquipmentCategory() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({
      categoryId,
      body,
    }: {
      categoryId: string
      body: UpdateEquipmentCategoryRequest
    }) => {
      const { data, error } = await api.PUT("/api/v1/equipment-categories/{categoryId}", {
        params: { path: { categoryId } },
        body,
      })
      if (error) throw error
      return data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: equipmentCategoryKeys.lists() })
      toast.success("Kategorie erfolgreich aktualisiert")
    },
    onError: () => {
      toast.error("Fehler beim Aktualisieren")
    },
  })
}

export function useArchiveEquipmentCategory() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (categoryId: string) => {
      const { error } = await api.DELETE("/api/v1/equipment-categories/{categoryId}", {
        params: { path: { categoryId } },
      })
      if (error) throw error
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: equipmentCategoryKeys.lists() })
      toast.success("Kategorie erfolgreich archiviert")
    },
    onError: () => {
      toast.error("Fehler beim Archivieren")
    },
  })
}
