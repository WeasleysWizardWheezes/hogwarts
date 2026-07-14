// Operation form component
import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Button } from '@/shared/components/ui/button';
import { Input } from '@/shared/components/ui/input';
import { Textarea } from '@/shared/components/ui/textarea';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/shared/components/ui/form';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/shared/components/ui/select';
import { useToast } from '@/shared/components/ui/use-toast';

const operationFormSchema = z.object({
  title: z.string().min(1, 'Titel ist erforderlich'),
  description: z.string().optional(),
  operationType: z.string().min(1, 'Einsatztyp ist erforderlich'),
  startTime: z.string().min(1, 'Startzeit ist erforderlich'),
  endTime: z.string().min(1, 'Endzeit ist erforderlich'),
  location: z.string().optional(),
});

type OperationFormValues = z.infer<typeof operationFormSchema>;

interface OperationFormProps {
  operation?: any;
  onSubmit: (values: OperationFormValues) => Promise<void>;
  onCancel?: () => void;
}

const operationTypes = [
  { value: 'BRANDEINSATZ', label: 'Brandeinsatz' },
  { value: 'TECHNISCHE_HILFE', label: 'Technische Hilfe' },
  { value: 'RETTUNGSDIENST', label: 'Rettungsdienst' },
  { value: 'UMWELTNOTFALL', label: 'Umweltnotfall' },
  { value: 'SONSTIGES', label: 'Sonstiges' },
];

export function OperationForm({ operation, onSubmit, onCancel }: OperationFormProps) {
  const { toast } = useToast();

  const form = useForm<OperationFormValues>({
    resolver: zodResolver(operationFormSchema),
    defaultValues: {
      title: operation?.title || '',
      description: operation?.description || '',
      operationType: operation?.operationType || '',
      startTime: operation?.startTime ? new Date(operation.startTime).toISOString().slice(0, 16) : '',
      endTime: operation?.endTime ? new Date(operation.endTime).toISOString().slice(0, 16) : '',
      location: operation?.location || '',
    },
  });

  const handleSubmit = async (values: OperationFormValues) => {
    try {
      await onSubmit(values);
      toast({
        title: 'Erfolgreich',
        description: operation ? 'Einsatz aktualisiert' : 'Einsatz erstellt',
      });
    } catch (error) {
      toast({
        title: 'Fehler',
        description: 'Einsatz konnte nicht gespeichert werden',
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

        <FormField
          control={form.control}
          name="operationType"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Einsatztyp *</FormLabel>
              <Select onValueChange={field.onChange} value={field.value}>
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Einsatztyp auswählen" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  {operationTypes.map(type => (
                    <SelectItem key={type.value} value={type.value}>{type.label}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
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

        <div className="flex justify-end space-x-2 pt-4">
          {onCancel && (
            <Button type="button" variant="outline" onClick={onCancel}>
              Abbrechen
            </Button>
          )}
          <Button type="submit" disabled={form.formState.isSubmitting}>
            {form.formState.isSubmitting ? 'Speichern...' : (operation ? 'Aktualisieren' : 'Erstellen')}
          </Button>
        </div>
      </form>
    </Form>
  );
}
