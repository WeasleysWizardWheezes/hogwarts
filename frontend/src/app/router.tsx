import { createBrowserRouter } from "react-router"
import { lazy } from "react"
import { Button } from "@/shared/components/ui/button"

const AppLayout = lazy(() => import("@/app/app-layout"))
const CoursesPage = lazy(() => import("@/features/courses/pages/courses-page"))
const CourseEnrollmentsPage = lazy(() => import("@/features/courses/pages/course-enrollments-page"))

// Placeholder: Wird durch echte Feature-Pages ersetzt
function DashboardPage() {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen gap-4">
      <h1 className="text-3xl font-bold">Fire Manager</h1>
      <Button onClick={() => window.location.href = "/courses"}>
        Zu den Lehrgängen
      </Button>
    </div>
  )
}

export const router = createBrowserRouter([
  {
    path: "/",
    Component: AppLayout,
    children: [
      { index: true, Component: DashboardPage },
      { path: "courses", Component: CoursesPage },
      { path: "courses/:courseId/enrollments", Component: CourseEnrollmentsPage },
    ],
  },
])
