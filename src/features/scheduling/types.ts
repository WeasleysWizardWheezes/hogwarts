// Types for scheduling feature
export interface Calendar {
  id: string;
  name: string;
  organizationUnitId: string;
  description?: string;
}

export interface Event {
  id: string;
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  calendarId: string;
  location?: string;
  recurring?: boolean;
  recurrencePattern?: string;
  createdAt: string;
  updatedAt: string;
}

export interface EventAttendee {
  eventId: string;
  userId: string;
  userName: string;
  status: 'accepted' | 'declined' | 'pending';
  responseTime: string;
}

export interface Operation {
  id: string;
  title: string;
  description?: string;
  operationType: string;
  startTime: string;
  endTime: string;
  status: 'planned' | 'active' | 'completed' | 'cancelled';
  location?: string;
  createdAt: string;
  updatedAt: string;
}

export interface OperationAssignment {
  operationId: string;
  equipmentId: string;
  equipmentName: string;
  vehicleId?: string;
  vehicleName?: string;
  assignedAt: string;
  assignedBy: string;
}

export interface Reminder {
  id: string;
  eventId?: string;
  operationId?: string;
  userId: string;
  minutesBefore: number;
  method: 'email' | 'push';
  status: 'pending' | 'sent' | 'failed';
  createdAt: string;
  updatedAt: string;
}

export interface CreateEventInput {
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  calendarId: string;
  location?: string;
  recurring?: boolean;
  recurrencePattern?: string;
}

export interface UpdateEventInput {
  title?: string;
  description?: string;
  startTime?: string;
  endTime?: string;
  location?: string;
  recurring?: boolean;
  recurrencePattern?: string;
}

export interface CreateOperationInput {
  title: string;
  description?: string;
  operationType: string;
  startTime: string;
  endTime: string;
  location?: string;
}

export interface UpdateReminderInput {
  minutesBefore?: number;
  method?: 'email' | 'push';
}

export interface CalendarListParams {
  organizationUnitId?: string;
}

export interface EventListParams {
  calendarId?: string;
  startDate?: string;
  endDate?: string;
  userId?: string;
}

export interface OperationListParams {
  status?: string;
  startDate?: string;
  endDate?: string;
  operationType?: string;
}

export interface ReminderListParams {
  userId?: string;
  status?: string;
  startDate?: string;
  endDate?: string;
}
