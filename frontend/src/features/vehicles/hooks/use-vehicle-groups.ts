import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import {
  getVehicleGroups,
  createVehicleGroup,
  updateVehicleGroup,
  deleteVehicleGroup,
  getVehicleGroupById
} from "../api/vehicle-api"

interface VehicleGroup {
  id: string
  name: string
  description?: string
}

export function useVehicleGroups() {
  return useQuery<{ data: VehicleGroup[] }>({
    queryKey: ["vehicle-groups"],
    queryFn: getVehicleGroups,
  })
}

export function useVehicleGroupById(id: string) {
  return useQuery<{ data: VehicleGroup }>({
    queryKey: ["vehicle-groups", id],
    queryFn: () => getVehicleGroupById(id),
    enabled: !!id,
  })
}

export function useCreateVehicleGroup() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (group: { name: string; description?: string }) => createVehicleGroup(group),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["vehicle-groups"] })
    },
  })
}

export function useUpdateVehicleGroup() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, group }: { id: string; group: { name: string; description?: string } }) => 
      updateVehicleGroup(id, group),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["vehicle-groups"] })
    },
  })
}

export function useDeleteVehicleGroup() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: deleteVehicleGroup,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["vehicle-groups"] })
    },
  })
}