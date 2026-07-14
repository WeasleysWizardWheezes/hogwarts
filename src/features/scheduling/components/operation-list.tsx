// Operation list component
import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/shared/components/ui/card';
import { Button } from '@/shared/components/ui/button';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/shared/components/ui/table';
import { Badge } from '@/shared/components/ui/badge';
import { format } from 'date-fns';
import { de } from 'date-fns/locale';
import { useOperations } from '../hooks/use-operations';
import { Plus, Edit, Trash2, Truck, AlertTriangle } from 'lucide-react';

interface OperationListProps {
  onCreateOperation?: () => void;
  onEditOperation?: (operationId: string) => void;
  onDeleteOperation?: (operationId: string) => void;
  onAssignEquipment?: (operationId: string) => void;
}

export function OperationList({ 
  onCreateOperation, 
  onEditOperation, 
  onDeleteOperation, 
  onAssignEquipment 
}: OperationListProps) {
  const operationsQuery = useOperations();

  const formatDate = (dateString: string) => {
    return format(new Date(dateString), 'dd.MM.yyyy HH:mm', { locale: de });
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'planned':
        return <Badge variant="outline">Geplant</Badge>;
      case 'active':
        return <Badge variant="default">Aktiv</Badge>;
      case 'completed':
        return <Badge variant="secondary">Abgeschlossen</Badge>;
      case 'cancelled':
        return <Badge variant="destructive">Abgebrochen</Badge>;
      default:
        return <Badge variant="outline">{status}</Badge>;
    }
  };

  const getOperationTypeLabel = (type: string) => {
    const types: Record<string, string> = {
      'BRANDEINSATZ': 'Brandeinsatz',
      'TECHNISCHE_HILFE': 'Technische Hilfe',
      'RETTUNGSDIENST': 'Rettungsdienst',
      'UMWELTNOTFALL': 'Umweltnotfall',
      'SONSTIGES': 'Sonstiges',
    };
    return types[type] || type;
  };

  if (operationsQuery.isLoading) {
    return <div>Einsätze werden geladen...</div>;
  }

  if (operationsQuery.error) {
    return <div>Fehler beim Laden der Einsätze</div>;
  }

  return (
    <Card className="w-full">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle>Einsätze</CardTitle>
        {onCreateOperation && (
          <Button size="sm" onClick={onCreateOperation}>
            <Plus className="h-4 w-4 mr-2" />
            Einsatz erstellen
          </Button>
        )}
      </CardHeader>
      <CardContent>
        {operationsQuery.data && operationsQuery.data.length > 0 ? (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Titel</TableHead>
                <TableHead>Typ</TableHead>
                <TableHead>Start</TableHead>
                <TableHead>Ende</TableHead>
                <TableHead>Ort</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Aktionen</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {operationsQuery.data.map(operation => (
                <TableRow key={operation.id}>
                  <TableCell className="font-medium">{operation.title}</TableCell>
                  <TableCell>{getOperationTypeLabel(operation.operationType)}</TableCell>
                  <TableCell>{formatDate(operation.startTime)}</TableCell>
                  <TableCell>{formatDate(operation.endTime)}</TableCell>
                  <TableCell>{operation.location || '-'}</TableCell>
                  <TableCell>{getStatusBadge(operation.status)}</TableCell>
                  <TableCell className="flex space-x-2">
                    {onAssignEquipment && (
                      <Button 
                        variant="ghost" 
                        size="sm" 
                        onClick={() => onAssignEquipment(operation.id)}
                      >
                        <Truck className="h-4 w-4" />
                      </Button>
                    )}
                    {onEditOperation && (
                      <Button 
                        variant="ghost" 
                        size="sm" 
                        onClick={() => onEditOperation(operation.id)}
                      >
                        <Edit className="h-4 w-4" />
                      </Button>
                    )}
                    {onDeleteOperation && (
                      <Button 
                        variant="ghost" 
                        size="sm" 
                        onClick={() => onDeleteOperation(operation.id)}
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
            Keine Einsätze gefunden
          </div>
        )}
      </CardContent>
    </Card>
  );
}
