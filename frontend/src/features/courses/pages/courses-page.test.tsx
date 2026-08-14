import { render, screen, waitFor } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { describe, it, expect, vi, beforeEach } from "vitest"
import CoursesPage from "./courses-page"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import * as coursesApi from "../api/courses-api"

describe("CoursesPage Component Tests", () => {
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
    vi.spyOn(coursesApi, "useCourses").mockReturnValue({
      data: { data: [] },
      isLoading: false,
      isError: false,
    })

    renderWithClient(<CoursesPage />)

    expect(screen.getByText("Lehrgänge")).toBeInTheDocument()
    expect(screen.getByRole("button", { name: "Lehrgang erstellen" })).toBeInTheDocument()
  })

  it("should show loading state", () => {
    vi.spyOn(coursesApi, "useCourses").mockReturnValue({
      data: undefined,
      isLoading: true,
      isError: false,
    })

    renderWithClient(<CoursesPage />)

    expect(screen.getByText("Laden...")).toBeInTheDocument()
  })

  it("should show error state", () => {
    vi.spyOn(coursesApi, "useCourses").mockReturnValue({
      data: undefined,
      isLoading: false,
      isError: true,
    })

    renderWithClient(<CoursesPage />)

    expect(screen.getByText("Fehler beim Laden.")).toBeInTheDocument()
  })

  it("should show empty state", () => {
    vi.spyOn(coursesApi, "useCourses").mockReturnValue({
      data: { data: [] },
      isLoading: false,
      isError: false,
    })

    renderWithClient(<CoursesPage />)

    expect(screen.getByText("Keine Lehrgänge vorhanden.")).toBeInTheDocument()
  })

  it("should open create dialog when create button is clicked", async () => {
    vi.spyOn(coursesApi, "useCourses").mockReturnValue({
      data: { data: [] },
      isLoading: false,
      isError: false,
    })

    vi.spyOn(coursesApi, "useCreateCourse").mockReturnValue({
      mutateAsync: vi.fn(),
      isPending: false,
    })

    const user = userEvent.setup()
    renderWithClient(<CoursesPage />)

    await user.click(screen.getByRole("button", { name: "Lehrgang erstellen" }))

    expect(screen.getByRole("dialog", { name: "Lehrgang erstellen" })).toBeInTheDocument()
    expect(screen.getByLabelText("Name")).toBeInTheDocument()
    expect(screen.getByLabelText("Beschreibung")).toBeInTheDocument()
    expect(screen.getByLabelText("Maximale Teilnehmer")).toBeInTheDocument()
  })

  it("should show course data when available", () => {
    vi.spyOn(coursesApi, "useCourses").mockReturnValue({
      data: {
        data: [
          {
            id: "1",
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
        ],
      },
      isLoading: false,
      isError: false,
    })

    renderWithClient(<CoursesPage />)

    expect(screen.getByText("Atemschutzgeräteträger")).toBeInTheDocument()
    expect(screen.getByText("Grundausbildung Atemschutz")).toBeInTheDocument()
    expect(screen.getByText("Max Mustermann")).toBeInTheDocument()
    expect(screen.getByText("15 / 20")).toBeInTheDocument()
    expect(screen.getByText("Bestätigt")).toBeInTheDocument()
  })
})
