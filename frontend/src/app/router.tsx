import { createBrowserRouter } from "react-router"
import { lazy } from "react"

const AppLayout = lazy(() => import("@/app/app-layout"))
const VehicleGroupsPage = lazy(
  () => import("@/features/vehicle-groups/pages/vehicle-groups-page")
)
const VehiclesPage = lazy(
  () => import("@/features/vehicles/pages/vehicles-page")
)
const EquipmentListPage = lazy(
  () => import("@/features/equipment/pages/equipment-list-page")
)
const EquipmentDetailPage = lazy(
  () => import("@/features/equipment/pages/equipment-detail-page")
)
const EquipmentCategoriesPage = lazy(
  () => import("@/features/equipment/pages/equipment-categories-page")
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
      { path: "equipment", Component: EquipmentListPage },
      { path: "equipment/:id", Component: EquipmentDetailPage },
      { path: "equipment-categories", Component: EquipmentCategoriesPage },
    ],
  },
])
