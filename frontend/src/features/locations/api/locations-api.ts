import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { api } from "@/shared/api"
import type { components } from "@/shared/api"
import { toast } from "sonner"

export type LocationResponse = components["schemas"]["LocationResponse"]
export type CreateLocationRequest = components["schemas"]["CreateLocationRequest"]
export type UpdateLocationRequest = components["schemas"]["UpdateLocationRequest"]

export const locationKeys = {
  all: ["locations"] as const,
  lists: () => [...locationKeys.all, "list"] as const,
  list: (page: number, size: number) => [...locationKeys.lists(), { page, size }] as const,
  details: () => [...locationKeys.all, "detail"] as const,
  detail: (id: string) => [...locationKeys.details(), id] as const,
}

export function useLocations(page = 0, size = 20) {
  return useQuery({
    queryKey: locationKeys.list(page, size),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/locations", {
        params: { query: { page, size } },
      })
      if (error) throw error
      return data
    },
  })
}

export function useCreateLocation() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (body: CreateLocationRequest) => {
      const { data, error } = await api.POST("/api/v1/locations", { body })
      if (error) throw error
      return data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: locationKeys.lists() })
      toast.success("Standort erfolgreich erstellt")
    },
    onError: () => {
      toast.error("Fehler beim Erstellen")
    },
  })
}

export function useUpdateLocation() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({ locationId, body }: { locationId: string; body: UpdateLocationRequest }) => {
      const { data, error } = await api.PUT("/api/v1/locations/{locationId}", {
        params: { path: { locationId } },
        body,
      })
      if (error) throw error
      return data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: locationKeys.lists() })
      toast.success("Standort erfolgreich aktualisiert")
    },
    onError: () => {
      toast.error("Fehler beim Aktualisieren")
    },
  })
}

export function useDeleteLocation() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (locationId: string) => {
      const { error } = await api.DELETE("/api/v1/locations/{locationId}", {
        params: { path: { locationId } },
      })
      if (error) throw error
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: locationKeys.lists() })
      toast.success("Standort erfolgreich gelöscht")
    },
    onError: () => {
      toast.error("Fehler beim Löschen")
    },
  })
}