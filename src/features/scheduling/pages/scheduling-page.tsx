// Scheduling main page
import React from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/shared/components/ui/tabs';
import { CalendarView } from '../components/calendar-view';
import { EventList } from '../components/event-list';
import { OperationList } from '../components/operation-list';
import { ReminderList } from '../components/reminder-list';
import { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/shared/components/ui/dialog';
import { EventForm } from '../components/event-form';
import { OperationForm } from '../components/operation-form';
import { useCreateEvent, useDeleteEvent } from '../hooks/use-events';
import { useCreateOperation, useDeleteOperation } from '../hooks/use-operations';
import { useToast } from '@/shared/components/ui/use-toast';
import { ConfirmActionDialog } from '@/shared/components/feedback/confirm-action-dialog';

interface SchedulingPageProps {
  organizationUnitId?: string;
}

export function SchedulingPage({ organizationUnitId }: SchedulingPageProps) {
  const { toast } = useToast();
  const [activeTab, setActiveTab] = useState('calendar');
  const [isEventDialogOpen, setIsEventDialogOpen] = useState(false);
  const [isOperationDialogOpen, setIsOperationDialogOpen] = useState(false);
  const [editingEvent, setEditingEvent] = useState<any>(null);
  const [editingOperation, setEditingOperation] = useState<any>(null);
  const [eventToDelete, setEventToDelete] = useState<string | null>(null);
  const [operationToDelete, setOperationToDelete] = useState<string | null>(null);

  const createEventMutation = useCreateEvent();
  const deleteEventMutation = useDeleteEvent();
  const createOperationMutation = useCreateOperation();
  const deleteOperationMutation = useDeleteOperation();

  const handleCreateEvent = () => {
    setEditingEvent(null);
    setIsEventDialogOpen(true);
  };

  const handleEditEvent = (eventId: string) => {
    // In a real implementation, we would fetch the event data here
    setEditingEvent({ id: eventId });
    setIsEventDialogOpen(true);
  };

  const handleDeleteEvent = (eventId: string) => {
    setEventToDelete(eventId);
  };

  const handleCreateOperation = () => {
    setEditingOperation(null);
    setIsOperationDialogOpen(true);
  };

  const handleEditOperation = (operationId: string) => {
    // In a real implementation, we would fetch the operation data here
    setEditingOperation({ id: operationId });
    setIsOperationDialogOpen(true);
  };

  const handleDeleteOperation = (operationId: string) => {
    setOperationToDelete(operationId);
  };

  const handleSubmitEvent = async (values: any) => {
    try {
      if (editingEvent) {
        // Update event logic would go here
        toast({
          title: 'Erfolgreich',
          description: 'Termin aktualisiert',
        });
      } else {
        await createEventMutation.mutateAsync(values);
      }
      setIsEventDialogOpen(false);
    } catch (error) {
      toast({
        title: 'Fehler',
        description: 'Termin konnte nicht gespeichert werden',
        variant: 'destructive',
      });
    }
  };

  const handleSubmitOperation = async (values: any) => {
    try {
      if (editingOperation) {
        // Update operation logic would go here
        toast({
          title: 'Erfolgreich',
          description: 'Einsatz aktualisiert',
        });
      } else {
        await createOperationMutation.mutateAsync(values);
      }
      setIsOperationDialogOpen(false);
    } catch (error) {
      toast({
        title: 'Fehler',
        description: 'Einsatz konnte nicht gespeichert werden',
        variant: 'destructive',
      });
    }
  };

  const confirmDeleteEvent = async () => {
    if (eventToDelete) {
      try {
        await deleteEventMutation.mutateAsync(eventToDelete);
        toast({
          title: 'Erfolgreich',
          description: 'Termin gelöscht',
        });
      } catch (error) {
        toast({
          title: 'Fehler',
          description: 'Termin konnte nicht gelöscht werden',
          variant: 'destructive',
        });
      }
      setEventToDelete(null);
    }
  };

  const confirmDeleteOperation = async () => {
    if (operationToDelete) {
      try {
        await deleteOperationMutation.mutateAsync(operationToDelete);
        toast({
          title: 'Erfolgreich',
          description: 'Einsatz gelöscht',
        });
      } catch (error) {
        toast({
          title: 'Fehler',
          description: 'Einsatz konnte nicht gelöscht werden',
          variant: 'destructive',
        });
      }
      setOperationToDelete(null);
    }
  };

  return (
    <div className="container mx-auto py-6">
      <h1 className="text-2xl font-bold mb-6">Dienst-, Termin- & Einsatzplanung</h1>

      <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-4">
        <TabsList>
          <TabsTrigger value="calendar">Kalender</TabsTrigger>
          <TabsTrigger value="events">Termine</TabsTrigger>
          <TabsTrigger value="operations">Einsätze</TabsTrigger>
          <TabsTrigger value="reminders">Erinnerungen</TabsTrigger>
        </TabsList>

        <TabsContent value="calendar">
          <CalendarView
            organizationUnitId={organizationUnitId}
            onCreateEvent={handleCreateEvent}
            onEventClick={handleEditEvent}
          />
        </TabsContent>

        <TabsContent value="events">
          <EventList
            onCreateEvent={handleCreateEvent}
            onEditEvent={handleEditEvent}
            onDeleteEvent={handleDeleteEvent}
          />
        </TabsContent>

        <TabsContent value="operations">
          <OperationList
            onCreateOperation={handleCreateOperation}
            onEditOperation={handleEditOperation}
            onDeleteOperation={handleDeleteOperation}
          />
        </TabsContent>

        <TabsContent value="reminders">
          <ReminderList />
        </TabsContent>
      </Tabs>

      {/* Event Form Dialog */}
      <Dialog open={isEventDialogOpen} onOpenChange={setIsEventDialogOpen}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>{editingEvent ? 'Termin bearbeiten' : 'Neuer Termin'}</DialogTitle>
          </DialogHeader>
          <EventForm
            event={editingEvent}
            onSubmit={handleSubmitEvent}
            onCancel={() => setIsEventDialogOpen(false)}
            organizationUnitId={organizationUnitId}
          />
        </DialogContent>
      </Dialog>

      {/* Operation Form Dialog */}
      <Dialog open={isOperationDialogOpen} onOpenChange={setIsOperationDialogOpen}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>{editingOperation ? 'Einsatz bearbeiten' : 'Neuer Einsatz'}</DialogTitle>
          </DialogHeader>
          <OperationForm
            operation={editingOperation}
            onSubmit={handleSubmitOperation}
            onCancel={() => setIsOperationDialogOpen(false)}
          />
        </DialogContent>
      </Dialog>

      {/* Delete Event Confirmation */}
      <ConfirmActionDialog
        open={!!eventToDelete}
        onOpenChange={() => setEventToDelete(null)}
        title="Termin löschen?"
        description="Dieser Termin wird unwiderruflich gelöscht."
        confirmLabel="Termin löschen"
        onConfirm={confirmDeleteEvent}
      />

      {/* Delete Operation Confirmation */}
      <ConfirmActionDialog
        open={!!operationToDelete}
        onOpenChange={() => setOperationToDelete(null)}
        title="Einsatz löschen?"
        description="Dieser Einsatz wird unwiderruflich gelöscht."
        confirmLabel="Einsatz löschen"
        onConfirm={confirmDeleteOperation}
      />
    </div>
  );
}
