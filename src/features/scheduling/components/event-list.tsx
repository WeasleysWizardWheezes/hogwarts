// Event list component
import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/shared/components/ui/card';
import { Button } from '@/shared/components/ui/button';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/shared/components/ui/table';
import { Badge } from '@/shared/components/ui/badge';
import { format } from 'date-fns';
import { de } from 'date-fns/locale';
import { useEvents } from '../hooks/use-events';
import { Plus, Edit, Trash2, Users, Clock } from 'lucide-react';

interface EventListProps {
  calendarId?: string;
  onCreateEvent?: () => void;
  onEditEvent?: (eventId: string) => void;
  onDeleteEvent?: (eventId: string) => void;
  onViewAttendees?: (eventId: string) => void;
}

export function EventList({ 
  calendarId, 
  onCreateEvent, 
  onEditEvent, 
  onDeleteEvent, 
  onViewAttendees 
}: EventListProps) {
  const eventsQuery = useEvents({ calendarId });

  const formatDate = (dateString: string) => {
    return format(new Date(dateString), 'dd.MM.yyyy HH:mm', { locale: de });
  };

  const getStatusBadge = (startTime: string, endTime: string) => {
    const now = new Date();
    const start = new Date(startTime);
    const end = new Date(endTime);
    
    if (end < now) {
      return <Badge variant="secondary">Abgeschlossen</Badge>;
    }
    if (start <= now && end >= now) {
      return <Badge variant="default">Aktiv</Badge>;
    }
    if (start > now) {
      return <Badge variant="outline">Geplant</Badge>;
    }
  };

  if (eventsQuery.isLoading) {
    return <div>Termine werden geladen...</div>;
  }

  if (eventsQuery.error) {
    return <div>Fehler beim Laden der Termine</div>;
  }

  return (
    <Card className="w-full">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle>Termine</CardTitle>
        {onCreateEvent && (
          <Button size="sm" onClick={onCreateEvent}>
            <Plus className="h-4 w-4 mr-2" />
            Termin erstellen
          </Button>
        )}
      </CardHeader>
      <CardContent>
        {eventsQuery.data && eventsQuery.data.length > 0 ? (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Titel</TableHead>
                <TableHead>Start</TableHead>
                <TableHead>Ende</TableHead>
                <TableHead>Ort</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Aktionen</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {eventsQuery.data.map(event => (
                <TableRow key={event.id}>
                  <TableCell className="font-medium">{event.title}</TableCell>
                  <TableCell>{formatDate(event.startTime)}</TableCell>
                  <TableCell>{formatDate(event.endTime)}</TableCell>
                  <TableCell>{event.location || '-'}</TableCell>
                  <TableCell>{getStatusBadge(event.startTime, event.endTime)}</TableCell>
                  <TableCell className="flex space-x-2">
                    {onViewAttendees && (
                      <Button 
                        variant="ghost" 
                        size="sm" 
                        onClick={() => onViewAttendees(event.id)}
                      >
                        <Users className="h-4 w-4" />
                      </Button>
                    )}
                    {onEditEvent && (
                      <Button 
                        variant="ghost" 
                        size="sm" 
                        onClick={() => onEditEvent(event.id)}
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
                    )}
                    {onDeleteEvent && (
                      <Button 
                        variant="ghost" 
                        size="sm" 
                        onClick={() => onDeleteEvent(event.id)}
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        ) : (
          <div className="text-center py-8 text-gray-500">
            Keine Termine gefunden
          </div>
        )}
      </CardContent>
    </Card>
  );
}
