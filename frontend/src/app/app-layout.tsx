import { SidebarProvider, SidebarInset, SidebarTrigger } from "@/shared/components/ui/sidebar"
import { AppSidebar } from "./app-sidebar"
import { Outlet } from "react-router"
import { Suspense } from "react"
import { Toaster } from "@/shared/components/ui/sonner"

export default function AppLayout() {
  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <header className="flex h-14 items-center gap-2 border-b px-4">
          <SidebarTrigger />
        </header>
        <main className="flex-1 p-6">
          <Suspense fallback={<div>Laden...</div>}>
            <Outlet />
          </Suspense>
        </main>
      </SidebarInset>
      <Toaster />
    </SidebarProvider>
  )
}
