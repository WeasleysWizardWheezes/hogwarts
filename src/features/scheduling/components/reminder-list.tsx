// Reminder list component
import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/shared/components/ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/shared/components/ui/table';
import { Badge } from '@/shared/components/ui/badge';
import { format } from 'date-fns';
import { de } from 'date-fns/locale';
import { useReminders } from '../hooks/use-reminders';
import { Bell, Clock, Mail, Smartphone } from 'lucide-react';

interface ReminderListProps {
  userId?: string;
}

export function ReminderList({ userId }: ReminderListProps) {
  const remindersQuery = useReminders({ userId });

  const formatDate = (dateString: string) => {
    return format(new Date(dateString), 'dd.MM.yyyy HH:mm', { locale: de });
  };

  const getMethodIcon = (method: string) => {
    switch (method) {
      case 'email':
        return <Mail className="h-4 w-4" />;
      case 'push':
        return <Smartphone className="h-4 w-4" />;
      default:
        return <Bell className="h-4 w-4" />;
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'pending':
        return <Badge variant="outline">Ausstehend</Badge>;
      case 'sent':
        return <Badge variant="secondary">Gesendet</Badge>;
      case 'failed':
        return <Badge variant="destructive">Fehlgeschlagen</Badge>;
      default:
        return <Badge variant="outline">{status}</Badge>;
    }
  };

  if (remindersQuery.isLoading) {
    return <div>Erinnerungen werden geladen...</div>;
  }

  if (remindersQuery.error) {
    return <div>Fehler beim Laden der Erinnerungen</div>;
  }

  return (
    <Card className="w-full">
      <CardHeader>
        <CardTitle>Erinnerungen</CardTitle>
      </CardHeader>
      <CardContent>
        {remindersQuery.data && remindersQuery.data.length > 0 ? (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Ereignis</TableHead>
                <TableHead>Zeitpunkt</TableHead>
                <TableHead>Vorlauf</TableHead>
                <TableHead>Methode</TableHead>
                <TableHead>Status</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {remindersQuery.data.map(reminder => (
                <TableRow key={reminder.id}>
                  <TableCell className="font-medium">
                    {reminder.eventId ? 'Termin' : 'Einsatz'}
                  </TableCell>
                  <TableCell>{formatDate(reminder.createdAt)}</TableCell>
                  <TableCell>{reminder.minutesBefore} Minuten vorher</TableCell>
                  <TableCell>
                    <div className="flex items-center">
                      {getMethodIcon(reminder.method)}
                      <span className="ml-2">{reminder.method === 'email' ? 'E-Mail' : 'Push'}</span>
                    </div>
                  </TableCell>
                  <TableCell>{getStatusBadge(reminder.status)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        ) : (
          <div className="text-center py-8 text-gray-500">
            Keine Erinnerungen gefunden
          </div>
        )}
      </CardContent>
    </Card>
  );
}
