import { createBrowserRouter } from "react-router"
import { lazy } from "react"

const AppLayout = lazy(() => import("@/app/app-layout"))
const EquipmentListPage = lazy(() => import("@/features/equipment/pages/equipment-list-page"))
const EquipmentCreatePage = lazy(() => import("@/features/equipment/pages/equipment-create-page"))
const EquipmentEditPage = lazy(() => import("@/features/equipment/pages/equipment-edit-page"))

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
      { path: "equipment", Component: EquipmentListPage },
      { path: "equipment/new", Component: EquipmentCreatePage },
      { path: "equipment/:id/edit", Component: EquipmentEditPage },
    ],
  },
])
