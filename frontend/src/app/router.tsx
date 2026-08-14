import { createBrowserRouter } from "react-router"
import { lazy } from "react"

const AppLayout = lazy(() => import("@/app/app-layout"))
const CoursesPage = lazy(() => import("@/features/courses/pages/courses-page"))
const CourseEnrollmentsPage = lazy(() => import("@/features/courses/pages/course-enrollments-page"))

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
      { path: "courses", Component: CoursesPage },
      { path: "courses/:courseId/enrollments", Component: CourseEnrollmentsPage },
    ],
  },
])
