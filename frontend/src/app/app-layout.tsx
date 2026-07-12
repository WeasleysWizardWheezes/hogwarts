import { Outlet } from "react-router"
import { Suspense } from "react"

export default function AppLayout() {
  return (
    <div className="min-h-screen">
      <Suspense fallback={<div className="flex items-center justify-center min-h-screen">Laden...</div>}>
        <Outlet />
      </Suspense>
    </div>
  )
}
