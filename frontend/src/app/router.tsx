import { createBrowserRouter } from "react-router"
import { lazy } from "react"

const AppLayout = lazy(() => import("@/app/app-layout"))
const VehicleGroupsPage = lazy(
  () => import("@/features/vehicle-groups/pages/vehicle-groups-page")
)
const VehiclesPage = lazy(
  () => import("@/features/vehicles/pages/vehicles-page")
)

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
      { path: "vehicle-groups", Component: VehicleGroupsPage },
      { path: "vehicles", Component: VehiclesPage },
    ],
  },
])
