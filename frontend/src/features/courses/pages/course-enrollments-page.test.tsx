import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { describe, it, expect, vi, beforeEach } from "vitest"
import CourseEnrollmentsPage from "./course-enrollments-page"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import * as coursesApi from "../api/courses-api"
import type { CourseResponse, CourseEnrollmentResponse } from "../api/courses-api"

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
    const courseData: CourseResponse = {
      id: "course-1",
      name: "Atemschutzgeräteträger",
      description: "Grundausbildung Atemschutz",
      maxParticipants: 20,
      currentParticipants: 15,
      startDate: "2026-09-01T00:00:00Z",
      endDate: "2026-09-05T00:00:00Z",
      instructorId: "instructor-1",
      instructorName: "Max Mustermann",
      status: "OPEN_FOR_REGISTRATION",
    }

    vi.spyOn(coursesApi, "useCourse").mockReturnValue({
      data: courseData,
      isLoading: false,
      isError: false,
      isSuccess: true,
      isPending: false,
      isLoadingError: false,
      isRefetchError: false,
      isFetched: true,
      isFetchedAfterMount: true,
      isFetching: false,
      isPlaceholderData: false,
      isStale: false,
      status: "success",
      dataUpdatedAt: Date.now(),
      errorUpdatedAt: 0,
      failureCount: 0,
      error: null,
      refetch: vi.fn(),
      remove: vi.fn(),
    } as any)

    vi.spyOn(coursesApi, "useCourseEnrollments").mockReturnValue({
      data: { data: [] },
      isLoading: false,
      isError: false,
      isSuccess: true,
      isPending: false,
      isLoadingError: false,
      isRefetchError: false,
      isFetched: true,
      isFetchedAfterMount: true,
      isFetching: false,
      isPlaceholderData: false,
      isStale: false,
      status: "success",
      dataUpdatedAt: Date.now(),
      errorUpdatedAt: 0,
      failureCount: 0,
      error: null,
      refetch: vi.fn(),
      remove: vi.fn(),
    } as any)

    renderWithClient(<CourseEnrollmentsPage />)

    expect(screen.getByText("Anmeldungen für Atemschutzgeräteträger")).toBeInTheDocument()
    expect(screen.getByText("15 / 20 Plätze belegt")).toBeInTheDocument()
  })

  it("should show loading state", () => {
    vi.spyOn(coursesApi, "useCourse").mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
      isSuccess: false,
      isPending: true,
      isLoadingError: false,
      isRefetchError: false,
      isFetched: false,
      isFetchedAfterMount: false,
      isFetching: true,
      isPlaceholderData: false,
      isStale: false,
      status: "pending",
      dataUpdatedAt: 0,
      errorUpdatedAt: 0,
      failureCount: 0,
      error: null,
      refetch: vi.fn(),
      remove: vi.fn(),
    } as any)

    vi.spyOn(coursesApi, "useCourseEnrollments").mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
      isSuccess: false,
      isPending: true,
      isLoadingError: false,
      isRefetchError: false,
      isFetched: false,
      isFetchedAfterMount: false,
      isFetching: true,
      isPlaceholderData: false,
      isStale: false,
      status: "pending",
      dataUpdatedAt: 0,
      errorUpdatedAt: 0,
      failureCount: 0,
      error: null,
      refetch: vi.fn(),
      remove: vi.fn(),
    } as any)

    renderWithClient(<CourseEnrollmentsPage />)

    expect(screen.getByText("Laden...")).toBeInTheDocument()
  })

  it("should show error state", () => {
    vi.spyOn(coursesApi, "useCourse").mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: true,
      isSuccess: false,
      isPending: false,
      isLoadingError: true,
      isRefetchError: false,
      isFetched: true,
      isFetchedAfterMount: true,
      isFetching: false,
      isPlaceholderData: false,
      isStale: false,
      status: "error",
      dataUpdatedAt: 0,
      errorUpdatedAt: Date.now(),
      failureCount: 1,
      error: new Error("Test error"),
      refetch: vi.fn(),
      remove: vi.fn(),
    } as any)

    vi.spyOn(coursesApi, "useCourseEnrollments").mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: false,
      isSuccess: true,
      isPending: false,
      isLoadingError: false,
      isRefetchError: false,
      isFetched: true,
      isFetchedAfterMount: true,
      isFetching: false,
      isPlaceholderData: false,
      isStale: false,
      status: "success",
      dataUpdatedAt: Date.now(),
      errorUpdatedAt: 0,
      failureCount: 0,
      error: null,
      refetch: vi.fn(),
      remove: vi.fn(),
    } as any)

    renderWithClient(<CourseEnrollmentsPage />)

    expect(screen.getByText("Fehler beim Laden.")).toBeInTheDocument()
  })

  it("should show empty enrollments state", () => {
    const courseData: CourseResponse = {
      id: "course-1",
      name: "Atemschutzgeräteträger",
      description: "Grundausbildung Atemschutz",
      maxParticipants: 20,
      currentParticipants: 15,
      startDate: "2026-09-01T00:00:00Z",
      endDate: "2026-09-05T00:00:00Z",
      instructorId: "instructor-1",
      instructorName: "Max Mustermann",
      status: "OPEN_FOR_REGISTRATION",
    }

    vi.spyOn(coursesApi, "useCourse").mockReturnValue({
      data: courseData,
      isLoading: false,
      isError: false,
      isSuccess: true,
      isPending: false,
      isLoadingError: false,
      isRefetchError: false,
      isFetched: true,
      isFetchedAfterMount: true,
      isFetching: false,
      isPlaceholderData: false,
      isStale: false,
      status: "success",
      dataUpdatedAt: Date.now(),
      errorUpdatedAt: 0,
      failureCount: 0,
      error: null,
      refetch: vi.fn(),
      remove: vi.fn(),
    } as any)

    vi.spyOn(coursesApi, "useCourseEnrollments").mockReturnValue({
      data: { data: [] },
      isLoading: false,
      isError: false,
      isSuccess: true,
      isPending: false,
      isLoadingError: false,
      isRefetchError: false,
      isFetched: true,
      isFetchedAfterMount: true,
      isFetching: false,
      isPlaceholderData: false,
      isStale: false,
      status: "success",
      dataUpdatedAt: Date.now(),
      errorUpdatedAt: 0,
      failureCount: 0,
      error: null,
      refetch: vi.fn(),
      remove: vi.fn(),
    } as any)

    renderWithClient(<CourseEnrollmentsPage />)

    expect(screen.getByText("Keine Anmeldungen vorhanden.")).toBeInTheDocument()
  })

  it("should show enrollments when available", () => {
    const courseData: CourseResponse = {
      id: "course-1",
      name: "Atemschutzgeräteträger",
      description: "Grundausbildung Atemschutz",
      maxParticipants: 20,
      currentParticipants: 15,
      startDate: "2026-09-01T00:00:00Z",
      endDate: "2026-09-05T00:00:00Z",
      instructorId: "instructor-1",
      instructorName: "Max Mustermann",
      status: "OPEN_FOR_REGISTRATION",
    }

    const enrollmentData: CourseEnrollmentResponse[] = [
      {
        id: "enrollment-1",
        courseId: "course-1",
        courseName: "Atemschutzgeräteträger",
        memberId: "member-1",
        memberName: "Test User",
        status: "CONFIRMED",
        createdAt: "2026-08-01T10:00:00Z",
      },
    ]

    vi.spyOn(coursesApi, "useCourse").mockReturnValue({
      data: courseData,
      isLoading: false,
      isError: false,
      isSuccess: true,
      isPending: false,
      isLoadingError: false,
      isRefetchError: false,
      isFetched: true,
      isFetchedAfterMount: true,
      isFetching: false,
      isPlaceholderData: false,
      isStale: false,
      status: "success",
      dataUpdatedAt: Date.now(),
      errorUpdatedAt: 0,
      failureCount: 0,
      error: null,
      refetch: vi.fn(),
      remove: vi.fn(),
    } as any)

    vi.spyOn(coursesApi, "useCourseEnrollments").mockReturnValue({
      data: { data: enrollmentData },
      isLoading: false,
      isError: false,
      isSuccess: true,
      isPending: false,
      isLoadingError: false,
      isRefetchError: false,
      isFetched: true,
      isFetchedAfterMount: true,
      isFetching: false,
      isPlaceholderData: false,
      isStale: false,
      status: "success",
      dataUpdatedAt: Date.now(),
      errorUpdatedAt: 0,
      failureCount: 0,
      error: null,
      refetch: vi.fn(),
      remove: vi.fn(),
    } as any)

    renderWithClient(<CourseEnrollmentsPage />)

    expect(screen.getByText("member-1")).toBeInTheDocument()
    expect(screen.getByText("Bestätigt")).toBeInTheDocument()
  })

  it("should open create enrollment dialog when create button is clicked", async () => {
    const courseData: CourseResponse = {
      id: "course-1",
      name: "Atemschutzgeräteträger",
      description: "Grundausbildung Atemschutz",
      maxParticipants: 20,
      currentParticipants: 15,
      startDate: "2026-09-01T00:00:00Z",
      endDate: "2026-09-05T00:00:00Z",
      instructorId: "instructor-1",
      instructorName: "Max Mustermann",
      status: "OPEN_FOR_REGISTRATION",
    }

    vi.spyOn(coursesApi, "useCourse").mockReturnValue({
      data: courseData,
      isLoading: false,
      isError: false,
      isSuccess: true,
      isPending: false,
      isLoadingError: false,
      isRefetchError: false,
      isFetched: true,
      isFetchedAfterMount: true,
      isFetching: false,
      isPlaceholderData: false,
      isStale: false,
      status: "success",
      dataUpdatedAt: Date.now(),
      errorUpdatedAt: 0,
      failureCount: 0,
      error: null,
      refetch: vi.fn(),
      remove: vi.fn(),
    } as any)

    vi.spyOn(coursesApi, "useCourseEnrollments").mockReturnValue({
      data: { data: [] },
      isLoading: false,
      isError: false,
      isSuccess: true,
      isPending: false,
      isLoadingError: false,
      isRefetchError: false,
      isFetched: true,
      isFetchedAfterMount: true,
      isFetching: false,
      isPlaceholderData: false,
      isStale: false,
      status: "success",
      dataUpdatedAt: Date.now(),
      errorUpdatedAt: 0,
      failureCount: 0,
      error: null,
      refetch: vi.fn(),
      remove: vi.fn(),
    } as any)

    vi.spyOn(coursesApi, "useCreateEnrollment").mockReturnValue({
      mutate: vi.fn(),
      mutateAsync: vi.fn(),
      isPending: false,
      isIdle: true,
      isSuccess: false,
      isError: false,
      data: undefined,
      error: null,
      variables: undefined,
      status: "idle",
      context: undefined,
      failureCount: 0,
      failureReason: null,
      reset: vi.fn(),
    } as any)

    const user = userEvent.setup()
    renderWithClient(<CourseEnrollmentsPage />)

    await user.click(screen.getByRole("button", { name: "Anmeldung erstellen" }))

    expect(screen.getByRole("dialog", { name: "Anmeldung erstellen" })).toBeInTheDocument()
    expect(screen.getByLabelText("Mitglied ID")).toBeInTheDocument()
    expect(screen.getByLabelText("Kommentar (optional)")).toBeInTheDocument()
  })
})
