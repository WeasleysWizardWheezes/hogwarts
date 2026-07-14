// Hook for managing events
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getEvents,
  getEventById,
  createEvent,
  updateEvent,
  deleteEvent,
  getEventAttendees,
  addEventAttendee,
  removeEventAttendee
} from '../api/scheduling-api';

export function useEvents(params?: any) {
  return useQuery({
    queryKey: ['events', params],
    queryFn: () => getEvents(params),
  });
}

export function useEvent(eventId: string) {
  return useQuery({
    queryKey: ['event', eventId],
    queryFn: () => getEventById(eventId),
    enabled: !!eventId,
  });
}

export function useCreateEvent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createEvent,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['events'] });
    },
  });
}

export function useUpdateEvent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ eventId, input }: { eventId: string; input: any }) => 
      updateEvent(eventId, input),
    onSuccess: (_, { eventId }) => {
      queryClient.invalidateQueries({ queryKey: ['events'] });
      queryClient.invalidateQueries({ queryKey: ['event', eventId] });
    },
  });
}

export function useDeleteEvent() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteEvent,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['events'] });
    },
  });
}

export function useEventAttendees(eventId: string) {
  return useQuery({
    queryKey: ['event-attendees', eventId],
    queryFn: () => getEventAttendees(eventId),
    enabled: !!eventId,
  });
}

export function useAddEventAttendee() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ eventId, userId }: { eventId: string; userId: string }) => 
      addEventAttendee(eventId, userId),
    onSuccess: (_, { eventId }) => {
      queryClient.invalidateQueries({ queryKey: ['event-attendees', eventId] });
    },
  });
}

export function useRemoveEventAttendee() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ eventId, userId }: { eventId: string; userId: string }) => 
      removeEventAttendee(eventId, userId),
    onSuccess: (_, { eventId }) => {
      queryClient.invalidateQueries({ queryKey: ['event-attendees', eventId] });
    },
  });
}
