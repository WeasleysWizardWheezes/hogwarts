import { render, screen, waitFor } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { describe, it, expect, vi, beforeEach } from "vitest"
import CourseEnrollmentsPage from "./course-enrollments-page"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import * as coursesApi from "../api/courses-api"

describe("CourseEnrollmentsPage Component Tests", () => {
  const queryClient = new QueryClient()

  const renderWithClient = (component: React.ReactElement) => {
    return render(
      <QueryClientProvider client={queryClient}>
        {component}
      </QueryClientProvider>
    )
  }

  beforeEach(() => {
    vi.restoreAllMocks()
  })

  it("should render page title", () => {
    vi.spyOn(coursesApi, "useCourse").mockReturnValue({
      data: {
        id: "course-1",
        name: "Atemschutzgeräteträger",
        description: "Grundausbildung Atemschutz",
        maxParticipants: 20,
        currentParticipants: 15,
        startDate: "2026-09-01T00:00:00Z",
        endDate: "2026-09-05T00:00:00Z",
        instructorId: "instructor-1",
        instructorName: "Max Mustermann",
        status: "CONFIRMED",
      },
      isLoading: false,
      isError: false,
    })

    vi.spyOn(coursesApi, "useCourseEnrollments").mockReturnValue({
      data: { data: [] },
      isLoading: false,
      isError: false,
    })

    renderWithClient(<CourseEnrollmentsPage />)

    expect(screen.getByText("Anmeldungen für Atemschutzgeräteträger")).toBeInTheDocument()
    expect(screen.getByText("15 / 20 Plätze belegt")).toBeInTheDocument()
  })

  it("should show loading state", () => {
    vi.spyOn(coursesApi, "useCourse").mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
    })

    vi.spyOn(coursesApi, "useCourseEnrollments").mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
    })

    renderWithClient(<CourseEnrollmentsPage />)

    expect(screen.getByText("Laden...")).toBeInTheDocument()
  })

  it("should show error state", () => {
    vi.spyOn(coursesApi, "useCourse").mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: true,
    })

    vi.spyOn(coursesApi, "useCourseEnrollments").mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: false,
    })

    renderWithClient(<CourseEnrollmentsPage />)

    expect(screen.getByText("Fehler beim Laden.")).toBeInTheDocument()
  })

  it("should show empty enrollments state", () => {
    vi.spyOn(coursesApi, "useCourse").mockReturnValue({
      data: {
        id: "course-1",
        name: "Atemschutzgeräteträger",
        description: "Grundausbildung Atemschutz",
        maxParticipants: 20,
        currentParticipants: 15,
        startDate: "2026-09-01T00:00:00Z",
        endDate: "2026-09-05T00:00:00Z",
        instructorId: "instructor-1",
        instructorName: "Max Mustermann",
        status: "CONFIRMED",
      },
      isLoading: false,
      isError: false,
    })

    vi.spyOn(coursesApi, "useCourseEnrollments").mockReturnValue({
      data: { data: [] },
      isLoading: false,
      isError: false,
    })

    renderWithClient(<CourseEnrollmentsPage />)

    expect(screen.getByText("Keine Anmeldungen vorhanden.")).toBeInTheDocument()
  })

  it("should show enrollments when available", () => {
    vi.spyOn(coursesApi, "useCourse").mockReturnValue({
      data: {
        id: "course-1",
        name: "Atemschutzgeräteträger",
        description: "Grundausbildung Atemschutz",
        maxParticipants: 20,
        currentParticipants: 15,
        startDate: "2026-09-01T00:00:00Z",
        endDate: "2026-09-05T00:00:00Z",
        instructorId: "instructor-1",
        instructorName: "Max Mustermann",
        status: "CONFIRMED",
      },
      isLoading: false,
      isError: false,
    })

    vi.spyOn(coursesApi, "useCourseEnrollments").mockReturnValue({
      data: {
        data: [
          {
            id: "enrollment-1",
            memberId: "member-1",
            status: "CONFIRMED",
            createdAt: "2026-08-01T10:00:00Z",
          },
        ],
      },
      isLoading: false,
      isError: false,
    })

    renderWithClient(<CourseEnrollmentsPage />)

    expect(screen.getByText("member-1")).toBeInTheDocument()
    expect(screen.getByText("Bestätigt")).toBeInTheDocument()
  })

  it("should open create enrollment dialog when create button is clicked", async () => {
    vi.spyOn(coursesApi, "useCourse").mockReturnValue({
      data: {
        id: "course-1",
        name: "Atemschutzgeräteträger",
        description: "Grundausbildung Atemschutz",
        maxParticipants: 20,
        currentParticipants: 15,
        startDate: "2026-09-01T00:00:00Z",
        endDate: "2026-09-05T00:00:00Z",
        instructorId: "instructor-1",
        instructorName: "Max Mustermann",
        status: "CONFIRMED",
      },
      isLoading: false,
      isError: false,
    })

    vi.spyOn(coursesApi, "useCourseEnrollments").mockReturnValue({
      data: { data: [] },
      isLoading: false,
      isError: false,
    })

    vi.spyOn(coursesApi, "useCreateEnrollment").mockReturnValue({
      mutateAsync: vi.fn(),
      isPending: false,
    })

    const user = userEvent.setup()
    renderWithClient(<CourseEnrollmentsPage />)

    await user.click(screen.getByRole("button", { name: "Anmeldung erstellen" }))

    expect(screen.getByRole("dialog", { name: "Anmeldung erstellen" })).toBeInTheDocument()
    expect(screen.getByLabelText("Mitglied ID")).toBeInTheDocument()
    expect(screen.getByLabelText("Kommentar (optional)")).toBeInTheDocument()
  })
})
