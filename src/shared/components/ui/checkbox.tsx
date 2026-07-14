// Checkbox component
import React from 'react';

export function Checkbox({ checked, onCheckedChange, className }: { checked: boolean; onCheckedChange: (checked: boolean) => void; className?: string }) {
  return (
    <button 
      type="button" 
      role="checkbox" 
      aria-checked={checked} 
      className={`peer h-4 w-4 shrink-0 rounded-sm border border-primary ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 data-[state=checked]:bg-primary data-[state=checked]:text-primary-foreground ${className || ''}`}
      onClick={() => onCheckedChange(!checked)}
      data-state={checked ? 'checked' : 'unchecked'}
    />
  );
}
