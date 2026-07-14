// Hook for managing reminders
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getReminders, getReminderById, updateReminder, deleteReminder } from '../api/scheduling-api';

export function useReminders(params?: any) {
  return useQuery({
    queryKey: ['reminders', params],
    queryFn: () => getReminders(params),
  });
}

export function useReminder(reminderId: string) {
  return useQuery({
    queryKey: ['reminder', reminderId],
    queryFn: () => getReminderById(reminderId),
    enabled: !!reminderId,
  });
}

export function useUpdateReminder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ reminderId, input }: { reminderId: string; input: any }) => 
      updateReminder(reminderId, input),
    onSuccess: (_, { reminderId }) => {
      queryClient.invalidateQueries({ queryKey: ['reminders'] });
      queryClient.invalidateQueries({ queryKey: ['reminder', reminderId] });
    },
  });
}

export function useDeleteReminder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteReminder,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reminders'] });
    },
  });
}
