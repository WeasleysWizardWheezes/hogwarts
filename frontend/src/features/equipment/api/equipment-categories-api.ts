import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { api } from "@/shared/api"
import type { components } from "@/shared/api"
import { toast } from "sonner"

export type EquipmentCategoryResponse = components["schemas"]["EquipmentCategoryResponse"]
export type CreateEquipmentCategoryRequest = components["schemas"]["CreateEquipmentCategoryRequest"]
export type UpdateEquipmentCategoryRequest = components["schemas"]["UpdateEquipmentCategoryRequest"]

export const equipmentCategoryKeys = {
  all: ["equipment-categories"] as const,
  list: (search: string) => [...equipmentCategoryKeys.all, { search }] as const,
}

export function useEquipmentCategories(search = "") {
  return useQuery({
    queryKey: equipmentCategoryKeys.list(search),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/equipment-categories", {
        params: { query: { page: 0, size: 100, search: search || undefined } },
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
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: equipmentCategoryKeys.all }); toast.success("Kategorie erstellt") },
    onError: () => toast.error("Kategorie konnte nicht erstellt werden"),
  })
}

export function useUpdateEquipmentCategory() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({ categoryId, body }: { categoryId: string; body: UpdateEquipmentCategoryRequest }) => {
      const { data, error } = await api.PUT("/api/v1/equipment-categories/{categoryId}", {
        params: { path: { categoryId } }, body,
      })
      if (error) throw error
      return data
    },
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: equipmentCategoryKeys.all }); toast.success("Kategorie aktualisiert") },
    onError: () => toast.error("Kategorie konnte nicht aktualisiert werden"),
  })
}

export function useDeleteEquipmentCategory() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (categoryId: string) => {
      const { error } = await api.DELETE("/api/v1/equipment-categories/{categoryId}", { params: { path: { categoryId } } })
      if (error) throw error
    },
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: equipmentCategoryKeys.all }); toast.success("Kategorie archiviert") },
    onError: () => toast.error("Kategorie konnte nicht archiviert werden"),
  })
}