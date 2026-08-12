import { createBrowserRouter } from "react-router"
import { lazy } from "react"

const AppLayout = lazy(() => import("@/app/app-layout"))
const LocationListPage = lazy(() => import("@/features/locations/pages/location-list-page"))
const LocationCreatePage = lazy(() => import("@/features/locations/pages/location-create-page"))
const LocationEditPage = lazy(() => import("@/features/locations/pages/location-edit-page"))
const MemberListPage = lazy(() => import("@/features/members/pages/member-list-page"))
const EquipmentListPage = lazy(() => import("@/features/equipment/pages/equipment-list-page"))
const EquipmentCreatePage = lazy(() => import("@/features/equipment/pages/equipment-create-page"))

// Dashboard mit Navigation zu allen Features
function DashboardPage() {
  return (
    <div className="container mx-auto py-12">
      <div className="text-center mb-12">
        <h1 className="text-4xl font-bold mb-4">Fire Manager</h1>
        <p className="text-xl text-muted-foreground">
          Feuerwehrverwaltungssystem
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {/* Standortverwaltung */}
        <div className="bg-card rounded-lg p-6 border hover:shadow-md transition-shadow">
          <h2 className="text-xl font-semibold mb-2">Standortverwaltung</h2>
          <p className="text-muted-foreground mb-4">
            Verwalte Feuerwehrwachen, Gerätedepots und Ausbildungszentren.
          </p>
          <div className="flex gap-2">
            <a href="/locations" className="text-primary hover:underline text-sm">
              Alle Standorte anzeigen
            </a>
            <a href="/locations/create" className="text-primary hover:underline text-sm">
              Neuen Standort erstellen
            </a>
          </div>
        </div>

        {/* Geräteverwaltung */}
        <div className="bg-card rounded-lg p-6 border hover:shadow-md transition-shadow">
          <h2 className="text-xl font-semibold mb-2">Geräteverwaltung</h2>
          <p className="text-muted-foreground mb-4">
            Verwalte Atemschutzgeräte, Funkgeräte, Werkzeuge und mehr.
          </p>
          <div className="flex gap-2">
            <a href="/equipment" className="text-primary hover:underline text-sm">
              Alle Geräte anzeigen
            </a>
            <a href="/equipment/create" className="text-primary hover:underline text-sm">
              Neues Gerät erstellen
            </a>
          </div>
        </div>

        {/* Mitgliederverwaltung */}
        <div className="bg-card rounded-lg p-6 border hover:shadow-md transition-shadow">
          <h2 className="text-xl font-semibold mb-2">Mitgliederverwaltung</h2>
          <p className="text-muted-foreground mb-4">
            Verwalte Feuerwehrmitglieder und ihre Zuordnungen.
          </p>
          <div className="flex gap-2">
            <a href="/members" className="text-primary hover:underline text-sm">
              Alle Mitglieder anzeigen
            </a>
          </div>
        </div>
      </div>
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
