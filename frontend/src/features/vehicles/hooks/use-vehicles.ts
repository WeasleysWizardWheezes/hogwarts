import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import {
  getVehicles,
  createVehicle,
  updateVehicle,
  deleteVehicle,
  getVehicleById
} from "../api/vehicle-api"

interface Vehicle {
  id: string
  name: string
  licensePlate: string
  type: string
  status: "VERFUEGBAR" | "IM_EINSATZ" | "WARTUNG_REPARATUR" | "DEFEKT"
  vehicleGroupId?: string
  year?: number
}

export function useVehicles() {
  return useQuery<{ data: Vehicle[] }>({
    queryKey: ["vehicles"],
    queryFn: getVehicles,
  })
}

export function useVehicleById(id: string) {
  return useQuery<{ data: Vehicle }>({
    queryKey: ["vehicles", id],
    queryFn: () => getVehicleById(id),
    enabled: !!id,
  })
}

export function useCreateVehicle() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (vehicle: { name: string; licensePlate: string; type: string; year?: number; status: string; vehicleGroupId?: string }) => createVehicle(vehicle),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["vehicles"] })
    },
  })
}

export function useUpdateVehicle() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, vehicle }: { id: string; vehicle: { name: string; licensePlate: string; type: string; year?: number; status: string; vehicleGroupId?: string } }) => 
      updateVehicle(id, vehicle),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["vehicles"] })
    },
  })
}

export function useDeleteVehicle() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: deleteVehicle,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["vehicles"] })
    },
  })
}