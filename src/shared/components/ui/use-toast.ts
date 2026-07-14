// Toast hook
import { useState } from 'react';

export function useToast() {
  const [toasts, setToasts] = useState<any[]>([]);

  const toast = ({ title, description, variant = 'default' }: { title: string; description: string; variant?: string }) => {
    const id = Date.now();
    setToasts(prev => [...prev, { id, title, description, variant }]);
    
    setTimeout(() => {
      setToasts(prev => prev.filter(t => t.id !== id));
    }, 5000);
  };

  return { toast, toasts };
}

export function ToastProvider({ children }: { children: React.ReactNode }) {
  const { toasts } = useToast();
  
  return (
    <>
      {children}
      <div className="fixed bottom-4 right-4 space-y-2">
        {toasts.map(toast => (
          <div key={toast.id} className={`p-4 rounded-md shadow-lg ${toast.variant === 'destructive' ? 'bg-red-100 border border-red-300' : 'bg-white border'}`}>
            <h3 className="font-medium">{toast.title}</h3>
            <p className="text-sm text-gray-600">{toast.description}</p>
          </div>
        ))}
      </div>
    </>
  );
}
