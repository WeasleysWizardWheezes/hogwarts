import { Outlet, Link } from "react-router-dom"
import { Suspense } from "react"
import { Button } from "@/shared/components/ui/button"

export default function AppLayout() {
  return (
    <div className="min-h-screen flex flex-col">
      <header className="border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="container mx-auto px-4 py-3 flex justify-between items-center">
          <Link to="/" className="text-xl font-bold">Fire Manager</Link>
          <nav className="flex gap-2">
            <Button variant="ghost">
              <Link to="/">Dashboard</Link>
            </Button>
            <Button variant="ghost">
              <Link to="/vehicles">Vehicles</Link>
            </Button>
          </nav>
        </div>
      </header>

      <main className="flex-1">
        <Suspense fallback={<div className="flex items-center justify-center min-h-screen">Laden...</div>}>
          <Outlet />
        </Suspense>
      </main>
    </div>
  )
}
