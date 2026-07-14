// Hook for managing operations
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getOperations,
  getOperationById,
  createOperation,
  updateOperation,
  deleteOperation,
  getOperationAssignments,
  assignEquipmentToOperation,
  getAvailableEquipment
} from '../api/scheduling-api';

export function useOperations(params?: any) {
  return useQuery({
    queryKey: ['operations', params],
    queryFn: () => getOperations(params),
  });
}

export function useOperation(operationId: string) {
  return useQuery({
    queryKey: ['operation', operationId],
    queryFn: () => getOperationById(operationId),
    enabled: !!operationId,
  });
}

export function useCreateOperation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createOperation,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['operations'] });
    },
  });
}

export function useUpdateOperation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ operationId, input }: { operationId: string; input: any }) => 
      updateOperation(operationId, input),
    onSuccess: (_, { operationId }) => {
      queryClient.invalidateQueries({ queryKey: ['operations'] });
      queryClient.invalidateQueries({ queryKey: ['operation', operationId] });
    },
  });
}

export function useDeleteOperation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteOperation,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['operations'] });
    },
  });
}

export function useOperationAssignments(operationId: string) {
  return useQuery({
    queryKey: ['operation-assignments', operationId],
    queryFn: () => getOperationAssignments(operationId),
    enabled: !!operationId,
  });
}

export function useAssignEquipmentToOperation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ operationId, equipmentId, vehicleId }: { operationId: string; equipmentId: string; vehicleId?: string }) => 
      assignEquipmentToOperation(operationId, equipmentId, vehicleId),
    onSuccess: (_, { operationId }) => {
      queryClient.invalidateQueries({ queryKey: ['operation-assignments', operationId] });
      queryClient.invalidateQueries({ queryKey: ['operations'] });
    },
  });
}

export function useAvailableEquipment(operationType: string) {
  return useQuery({
    queryKey: ['available-equipment', operationType],
    queryFn: () => getAvailableEquipment(operationType),
    enabled: !!operationType,
  });
}
