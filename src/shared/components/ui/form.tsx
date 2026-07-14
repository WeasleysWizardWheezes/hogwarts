// Form component
import React from 'react';
import { useForm as useReactHookForm, UseFormReturn, FieldValues, UseFormProps } from 'react-hook-form';

export function Form<FormValues extends FieldValues>({ children, ...props }: UseFormProps<FormValues> & { children: React.ReactNode }) {
  const form = useReactHookForm<FormValues>(props);
  return <form>{children}</form>;
}

export function FormField({ children }: { children: React.ReactNode }) {
  return <div>{children}</div>;
}

export function FormItem({ children, className }: { children: React.ReactNode; className?: string }) {
  return <div className={`space-y-2 ${className || ''}`}>{children}</div>;
}

export function FormLabel({ children, className }: { children: React.ReactNode; className?: string }) {
  return <label className={`text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70 ${className || ''}`}>{children}</label>;
}

export function FormControl({ children }: { children: React.ReactNode }) {
  return <div>{children}</div>;
}

export function FormMessage({ children, className }: { children: React.ReactNode; className?: string }) {
  return <p className={`text-sm font-medium text-destructive ${className || ''}`}>{children}</p>;
}

export function useForm<FormValues extends FieldValues>(props: UseFormProps<FormValues>): UseFormReturn<FormValues> {
  return useReactHookForm<FormValues>(props);
}
