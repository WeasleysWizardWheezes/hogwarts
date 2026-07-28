import { createBrowserRouter } from "react-router-dom"
import { lazy } from "react"

const AppLayout = lazy(() => import("@/app/app-layout"))
const VehicleManagementPage = lazy(() => import("@/features/vehicles/pages/VehicleManagementPage"))

// Placeholder: Wird durch echte Feature-Pages ersetzt
function DashboardPage() {
  return (
    <div className="flex items-center justify-center min-h-screen">
      <h1 className="text-3xl font-bold">Fire Manager</h1>
    </div>
  )
}

export const router = createBrowserRouter([
  {
    path: "/",
    Component: AppLayout,
    children: [
      { index: true, Component: DashboardPage },
      { path: "vehicles", Component: VehicleManagementPage },
    ],
  },
])
