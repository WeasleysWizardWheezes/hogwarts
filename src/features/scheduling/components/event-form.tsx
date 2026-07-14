// Event form component
import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Button } from '@/shared/components/ui/button';
import { Input } from '@/shared/components/ui/input';
import { Textarea } from '@/shared/components/ui/textarea';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/shared/components/ui/form';
import { Checkbox } from '@/shared/components/ui/checkbox';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/shared/components/ui/select';
import { useCalendars } from '../hooks/use-calendars';
import { useToast } from '@/shared/components/ui/use-toast';

const eventFormSchema = z.object({
  title: z.string().min(1, 'Titel ist erforderlich'),
  description: z.string().optional(),
  startTime: z.string().min(1, 'Startzeit ist erforderlich'),
  endTime: z.string().min(1, 'Endzeit ist erforderlich'),
  calendarId: z.string().min(1, 'Kalender ist erforderlich'),
  location: z.string().optional(),
  recurring: z.boolean().default(false),
  recurrencePattern: z.string().optional(),
});

type EventFormValues = z.infer<typeof eventFormSchema>;

interface EventFormProps {
  event?: any;
  onSubmit: (values: EventFormValues) => Promise<void>;
  onCancel?: () => void;
  organizationUnitId?: string;
}

export function EventForm({ event, onSubmit, onCancel, organizationUnitId }: EventFormProps) {
  const { toast } = useToast();
  const calendarsQuery = useCalendars({ organizationUnitId });

  const form = useForm<EventFormValues>({
    resolver: zodResolver(eventFormSchema),
    defaultValues: {
      title: event?.title || '',
      description: event?.description || '',
      startTime: event?.startTime ? new Date(event.startTime).toISOString().slice(0, 16) : '',
      endTime: event?.endTime ? new Date(event.endTime).toISOString().slice(0, 16) : '',
      calendarId: event?.calendarId || '',
      location: event?.location || '',
      recurring: event?.recurring || false,
      recurrencePattern: event?.recurrencePattern || '',
    },
  });

  const handleSubmit = async (values: EventFormValues) => {
    try {
      await onSubmit(values);
      toast({
        title: 'Erfolgreich',
        description: event ? 'Termin aktualisiert' : 'Termin erstellt',
      });
    } catch (error) {
      toast({
        title: 'Fehler',
        description: 'Termin konnte nicht gespeichert werden',
        variant: 'destructive',
      });
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
        <FormField
          control={form.control}
          name="title"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Titel *</FormLabel>
              <FormControl>
                <Input {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="description"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Beschreibung</FormLabel>
              <FormControl>
                <Textarea {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="grid grid-cols-2 gap-4">
          <FormField
            control={form.control}
            name="startTime"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Startzeit *</FormLabel>
                <FormControl>
                  <Input type="datetime-local" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="endTime"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Endzeit *</FormLabel>
                <FormControl>
                  <Input type="datetime-local" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        <FormField
          control={form.control}
          name="calendarId"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Kalender *</FormLabel>
              <Select onValueChange={field.onChange} value={field.value}>
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Kalender auswählen" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  {calendarsQuery.data?.map(calendar => (
                    <SelectItem key={calendar.id} value={calendar.id}>{calendar.name}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="location"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Ort</FormLabel>
              <FormControl>
                <Input {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="flex items-center space-x-2">
          <FormField
            control={form.control}
            name="recurring"
            render={({ field }) => (
              <FormItem className="flex flex-row items-start space-x-3 space-y-0">
                <FormControl>
                  <Checkbox
                    checked={field.value}
                    onCheckedChange={field.onChange}
                  />
                </FormControl>
                <div className="space-y-1 leading-none">
                  <FormLabel>Wiederkehrend</FormLabel>
                </div>
              </FormItem>
            )}
          />
        </div>

        {form.watch('recurring') && (
          <FormField
            control={form.control}
            name="recurrencePattern"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Wiederholungsmuster</FormLabel>
                <FormControl>
                  <Input placeholder="z.B. wöchentlich, monatlich" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        )}

        <div className="flex justify-end space-x-2 pt-4">
          {onCancel && (
            <Button type="button" variant="outline" onClick={onCancel}>
              Abbrechen
            </Button>
          )}
          <Button type="submit" disabled={form.formState.isSubmitting}>
            {form.formState.isSubmitting ? 'Speichern...' : (event ? 'Aktualisieren' : 'Erstellen')}
          </Button>
        </div>
      </form>
    </Form>
  );
}
