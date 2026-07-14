// Hook for managing calendars
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getCalendars, getCalendarById } from '../api/scheduling-api';

export function useCalendars(params?: any) {
  return useQuery({
    queryKey: ['calendars', params],
    queryFn: () => getCalendars(params),
  });
}

export function useCalendar(calendarId: string) {
  return useQuery({
    queryKey: ['calendar', calendarId],
    queryFn: () => getCalendarById(calendarId),
    enabled: !!calendarId,
  });
}
