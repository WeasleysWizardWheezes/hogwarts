// Confirm action dialog component
import React from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from '../ui/dialog';
import { Button } from '../ui/button';

interface ConfirmActionDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  title: string;
  description: string;
  confirmLabel: string;
  onConfirm: () => void;
  cancelLabel?: string;
}

export function ConfirmActionDialog({ 
  open, 
  onOpenChange, 
  title, 
  description, 
  confirmLabel, 
  onConfirm, 
  cancelLabel = 'Abbrechen'
}: ConfirmActionDialogProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-sm">
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
          <DialogDescription>{description}</DialogDescription>
        </DialogHeader>
        <div className="flex justify-end space-x-2 mt-4">
          <Button variant="outline" onClick={() => onOpenChange(false)}>
            {cancelLabel}
          </Button>
          <Button variant="destructive" onClick={onConfirm}>
            {confirmLabel}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
