import { Outlet } from "react-router"
import { Suspense } from "react"
import { AppSidebar } from "./app-sidebar"

export default function AppLayout() {
  return (
    <div className="flex min-h-screen">
      <AppSidebar />
      <div className="flex-1">
        <Suspense fallback={<div className="flex items-center justify-center min-h-screen">Laden...</div>}>
          <Outlet />
        </Suspense>
      </div>
    </div>
  )
}
