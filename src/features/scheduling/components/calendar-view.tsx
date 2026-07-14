// Calendar view component
import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/shared/components/ui/card';
import { Button } from '@/shared/components/ui/button';
import { Plus, ChevronLeft, ChevronRight } from 'lucide-react';
import { useCalendars } from '../hooks/use-calendars';
import { useEvents } from '../hooks/use-events';
import { format, addMonths, subMonths, startOfMonth, endOfMonth, eachDayOfInterval, isSameMonth, isToday } from 'date-fns';
import { de } from 'date-fns/locale';

interface CalendarViewProps {
  organizationUnitId?: string;
  onCreateEvent?: () => void;
  onEventClick?: (eventId: string) => void;
}

export function CalendarView({ organizationUnitId, onCreateEvent, onEventClick }: CalendarViewProps) {
  const [currentDate, setCurrentDate] = React.useState(new Date());
  const [selectedCalendarId, setSelectedCalendarId] = React.useState<string | null>(null);

  const calendarsQuery = useCalendars({ organizationUnitId });
  const eventsQuery = useEvents({ calendarId: selectedCalendarId });

  const daysInMonth = eachDayOfInterval({
    start: startOfMonth(currentDate),
    end: endOfMonth(currentDate),
  });

  const goToPreviousMonth = () => {
    setCurrentDate(subMonths(currentDate, 1));
  };

  const goToNextMonth = () => {
    setCurrentDate(addMonths(currentDate, 1));
  };

  const goToToday = () => {
    setCurrentDate(new Date());
  };

  const getEventsForDay = (day: Date) => {
    if (!eventsQuery.data) return [];
    
    const dayString = format(day, 'yyyy-MM-dd');
    return eventsQuery.data.filter(event => 
      format(new Date(event.startTime), 'yyyy-MM-dd') === dayString
    );
  };

  if (calendarsQuery.isLoading) {
    return <div>Kalender werden geladen...</div>;
  }

  if (calendarsQuery.error) {
    return <div>Fehler beim Laden der Kalender</div>;
  }

  return (
    <Card className="w-full">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <div className="flex items-center space-x-2">
          <CardTitle>Kalender</CardTitle>
          {calendarsQuery.data && calendarsQuery.data.length > 0 && (
            <select
              value={selectedCalendarId || ''}
              onChange={(e) => setSelectedCalendarId(e.target.value || null)}
              className="px-2 py-1 border rounded text-sm"
            >
              <option value="">Alle Kalender</option>
              {calendarsQuery.data.map(calendar => (
                <option key={calendar.id} value={calendar.id}>{calendar.name}</option>
              ))}
            </select>
          )}
        </div>
        <div className="flex items-center space-x-2">
          <Button variant="outline" size="sm" onClick={goToToday}>
            Heute
          </Button>
          <Button variant="outline" size="sm" onClick={goToPreviousMonth}>
            <ChevronLeft className="h-4 w-4" />
          </Button>
          <Button variant="outline" size="sm" onClick={goToNextMonth}>
            <ChevronRight className="h-4 w-4" />
          </Button>
          <Button size="sm" onClick={onCreateEvent}>
            <Plus className="h-4 w-4 mr-2" />
            Termin erstellen
          </Button>
        </div>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-7 gap-1 text-center text-sm font-medium mb-2">
          {['Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa', 'So'].map(day => (
            <div key={day} className="p-2">{day}</div>
          ))}
        </div>
        <div className="grid grid-cols-7 gap-1">
          {daysInMonth.map(day => (
            <div
              key={format(day, 'yyyy-MM-dd')}
              className={`p-2 border rounded min-h-[100px] ${
                !isSameMonth(day, currentDate) ? 'bg-gray-50 text-gray-400' : ''
              } ${
                isToday(day) ? 'bg-blue-50' : ''
              }`}
            >
              <div className={`text-right text-xs mb-1 ${
                !isSameMonth(day, currentDate) ? 'text-gray-400' : ''
              }`}>
                {format(day, 'd')}
              </div>
              <div className="space-y-1">
                {getEventsForDay(day).slice(0, 3).map(event => (
                  <div
                    key={event.id}
                    className="text-xs bg-blue-100 px-1 py-0.5 rounded cursor-pointer hover:bg-blue-200 truncate"
                    onClick={() => onEventClick?.(event.id)}
                    title={event.title}
                  >
                    {event.title}
                  </div>
                ))}
                {getEventsForDay(day).length > 3 && (
                  <div className="text-xs text-gray-500">
                    +{getEventsForDay(day).length - 3} mehr
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}
