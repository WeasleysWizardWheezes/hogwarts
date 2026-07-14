// Table component
import React from 'react';

export function Table({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <div className="relative w-full overflow-auto">
      <table className={`w-full caption-bottom text-sm ${className || ''}`}>
        {children}
      </table>
    </div>
  );
}

export function TableHeader({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <thead className={`[&_tr]:border-b ${className || ''}`}>
      {children}
    </thead>
  );
}

export function TableBody({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <tbody className={`[&_tr:last-child]:border-0 ${className || ''}`}>
      {children}
    </tbody>
  );
}

export function TableFooter({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <tfoot className={`bg-primary font-medium text-primary-foreground ${className || ''}`}>
      {children}
    </tfoot>
  );
}

export function TableRow({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <tr className={`border-b transition-colors hover:bg-muted/50 data-[state=selected]:bg-muted ${className || ''}`}>
      {children}
    </tr>
  );
}

export function TableHead({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <th className={`h-12 px-4 text-left align-middle font-medium text-muted-foreground [&:has([role=checkbox])]:pr-0 ${className || ''}`}>
      {children}
    </th>
  );
}

export function TableCell({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <td className={`p-4 align-middle [&:has([role=checkbox])]:pr-0 ${className || ''}`}>
      {children}
    </td>
  );
}

export function TableCaption({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <caption className={`mt-4 text-sm text-muted-foreground ${className || ''}`}>
      {children}
    </caption>
  );
}
