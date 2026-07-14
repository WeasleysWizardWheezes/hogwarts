// Scheduling API functions
import { apiClient } from '@/shared/lib/api-client';
import type {
  Calendar,
  Event,
  EventAttendee,
  Operation,
  OperationAssignment,
  Reminder,
  CreateEventInput,
  UpdateEventInput,
  CreateOperationInput,
  UpdateReminderInput,
  CalendarListParams,
  EventListParams,
  OperationListParams,
  ReminderListParams
} from '../types';

// Calendar API

export async function getCalendars(params?: CalendarListParams): Promise<Calendar[]> {
  const response = await apiClient.get('/api/v1/calendars', { params });
  return response.data;
}

export async function getCalendarById(calendarId: string): Promise<Calendar> {
  const response = await apiClient.get(`/api/v1/calendars/${calendarId}`);
  return response.data;
}

// Event API

export async function getEvents(params?: EventListParams): Promise<Event[]> {
  const response = await apiClient.get('/api/v1/events', { params });
  return response.data;
}

export async function getEventById(eventId: string): Promise<Event> {
  const response = await apiClient.get(`/api/v1/events/${eventId}`);
  return response.data;
}

export async function createEvent(input: CreateEventInput): Promise<Event> {
  const response = await apiClient.post('/api/v1/events', input);
  return response.data;
}

export async function updateEvent(eventId: string, input: UpdateEventInput): Promise<Event> {
  const response = await apiClient.put(`/api/v1/events/${eventId}`, input);
  return response.data;
}

export async function deleteEvent(eventId: string): Promise<void> {
  await apiClient.delete(`/api/v1/events/${eventId}`);
}

// Event Attendees API

export async function getEventAttendees(eventId: string): Promise<EventAttendee[]> {
  const response = await apiClient.get(`/api/v1/events/${eventId}/attendees`);
  return response.data;
}

export async function addEventAttendee(eventId: string, userId: string): Promise<EventAttendee> {
  const response = await apiClient.post(`/api/v1/events/${eventId}/attendees`, { userId });
  return response.data;
}

export async function removeEventAttendee(eventId: string, userId: string): Promise<void> {
  await apiClient.delete(`/api/v1/events/${eventId}/attendees/${userId}`);
}

// Operation API

export async function getOperations(params?: OperationListParams): Promise<Operation[]> {
  const response = await apiClient.get('/api/v1/operations', { params });
  return response.data;
}

export async function getOperationById(operationId: string): Promise<Operation> {
  const response = await apiClient.get(`/api/v1/operations/${operationId}`);
  return response.data;
}

export async function createOperation(input: CreateOperationInput): Promise<Operation> {
  const response = await apiClient.post('/api/v1/operations', input);
  return response.data;
}

export async function updateOperation(operationId: string, input: Partial<CreateOperationInput>): Promise<Operation> {
  const response = await apiClient.put(`/api/v1/operations/${operationId}`, input);
  return response.data;
}

export async function deleteOperation(operationId: string): Promise<void> {
  await apiClient.delete(`/api/v1/operations/${operationId}`);
}

// Operation Assignments API

export async function getOperationAssignments(operationId: string): Promise<OperationAssignment[]> {
  const response = await apiClient.get(`/api/v1/operations/${operationId}/assignments`);
  return response.data;
}

export async function assignEquipmentToOperation(operationId: string, equipmentId: string, vehicleId?: string): Promise<OperationAssignment> {
  const response = await apiClient.post(`/api/v1/operations/${operationId}/assign`, {
    equipmentId,
    vehicleId
  });
  return response.data;
}

export async function getAvailableEquipment(operationType: string): Promise<any[]> {
  const response = await apiClient.get('/api/v1/operations/available-equipment', {
    params: { operationType }
  });
  return response.data;
}

// Reminder API

export async function getReminders(params?: ReminderListParams): Promise<Reminder[]> {
  const response = await apiClient.get('/api/v1/reminders', { params });
  return response.data;
}

export async function getReminderById(reminderId: string): Promise<Reminder> {
  const response = await apiClient.get(`/api/v1/reminders/${reminderId}`);
  return response.data;
}

export async function updateReminder(reminderId: string, input: UpdateReminderInput): Promise<Reminder> {
  const response = await apiClient.patch(`/api/v1/reminders/${reminderId}`, input);
  return response.data;
}

export async function deleteReminder(reminderId: string): Promise<void> {
  await apiClient.delete(`/api/v1/reminders/${reminderId}`);
}
