// Dialog component
import React from 'react';

export function Dialog({ children, open, onOpenChange, className }: { children: React.ReactNode; open: boolean; onOpenChange: (open: boolean) => void; className?: string }) {
  return (
    <div className={`fixed inset-0 z-50 flex items-center justify-center bg-black/50 ${className || ''}`}>
      {children}
    </div>
  );
}

export function DialogContent({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <div className={`bg-background rounded-lg border shadow-lg p-6 max-w-md w-full ${className || ''}`}>
      {children}
    </div>
  );
}

export function DialogHeader({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <div className={`flex flex-col space-y-1.5 text-center sm:text-left ${className || ''}`}>
      {children}
    </div>
  );
}

export function DialogTitle({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <h2 className={`text-lg font-semibold leading-none tracking-tight ${className || ''}`}>
      {children}
    </h2>
  );
}

export function DialogDescription({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <p className={`text-sm text-muted-foreground ${className || ''}`}>
      {children}
    </p>
  );
}
