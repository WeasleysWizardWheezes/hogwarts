import { createBrowserRouter } from "react-router"
import { lazy } from "react"

const AppLayout = lazy(() => import("@/app/app-layout"))
const LocationListPage = lazy(() => import("@/features/locations/pages/location-list-page"))
const LocationCreatePage = lazy(() => import("@/features/locations/pages/location-create-page"))
const LocationEditPage = lazy(() => import("@/features/locations/pages/location-edit-page"))
const MemberListPage = lazy(() => import("@/features/members/pages/member-list-page"))
const EquipmentListPage = lazy(() => import("@/features/equipment/pages/equipment-list-page"))
const EquipmentCreatePage = lazy(() => import("@/features/equipment/pages/equipment-create-page"))

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
      { path: "locations", Component: LocationListPage },
      { path: "locations/create", Component: LocationCreatePage },
      { path: "locations/:id", Component: LocationEditPage },
      { path: "members", Component: MemberListPage },
      { path: "equipment", Component: EquipmentListPage },
      { path: "equipment/create", Component: EquipmentCreatePage },
    ],
  },
])
