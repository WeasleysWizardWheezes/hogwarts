import { describe, it, expect, beforeAll, afterAll, afterEach } from "vitest"
import { renderHook } from "@testing-library/react"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import { setupServer } from "msw/node"
import { http, HttpResponse } from "msw"
import { handlers } from "@/test/api-handlers"
import { useMembers } from "./use-members"

const server = setupServer(...handlers)

beforeAll(() => server.listen())
afterEach(() => server.resetHandlers())
afterAll(() => server.close())

describe("useMembers Hook", () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  })

  const wrapper = function ({ children }: { children: React.ReactNode }) {
    return (
      <QueryClientProvider client={queryClient}>
        {children}
      </QueryClientProvider>
    )
  }

  it("sollte Mitglieder erfolgreich laden", async () => {
    const { result } = renderHook(() => useMembers(0, 10), { wrapper })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    expect(result.current.data).toEqual({
      data: [
        {
          id: "member-1",
          firstName: "Harry",
          lastName: "Potter",
          email: "harry@example.com",
          phone: "123456789",
          location: {
            id: "location-1",
            name: "Hauptwache"
          },
          createdAt: "2026-01-01T00:00:00Z",
          updatedAt: "2026-01-01T00:00:00Z"
        },
        {
          id: "member-2",
          firstName: "Hermine",
          lastName: "Granger",
          email: "hermine@example.com",
          phone: "987654321",
          location: {
            id: "location-1",
            name: "Hauptwache"
          },
          createdAt: "2026-01-01T00:00:00Z",
          updatedAt: "2026-01-01T00:00:00Z"
        },
        {
          id: "member-3",
          firstName: "Ron",
          lastName: "Weasley",
          email: "ron@example.com",
          phone: "555555555",
          location: {
            id: "location-2",
            name: "Gerätedepot"
          },
          createdAt: "2026-01-01T00:00:00Z",
          updatedAt: "2026-01-01T00:00:00Z"
        },
        {
          id: "member-4",
          firstName: "Albus",
          lastName: "Dumbledore",
          email: "albus@example.com",
          phone: "111111111",
          location: null,
          createdAt: "2026-01-01T00:00:00Z",
          updatedAt: "2026-01-01T00:00:00Z"
        }
      ],
      page: {
        size: 10,
        totalElements: 4,
        totalPages: 1,
        number: 0
      }
    })
  })

  it("sollte Mitglieder nach Standort filtern", async () => {
    const { result } = renderHook(() => useMembers(0, 10, "location-1"), { wrapper })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    expect(result.current.data?.data).toHaveLength(2)
    expect(result.current.data?.data[0].location?.id).toBe("location-1")
    expect(result.current.data?.data[1].location?.id).toBe("location-1")
  })

  it("sollte Ladezustand anzeigen", () => {
    const { result } = renderHook(() => useMembers(0, 10), { wrapper })

    expect(result.current.isLoading).toBe(true)
    expect(result.current.isFetching).toBe(true)
  })

  it("sollte Fehlerzustand anzeigen, wenn das Laden fehlschlägt", async () => {
    server.use(
      ...handlers,
      http.get("/api/v1/members", () => {
        return HttpResponse.json(
          { message: "Fehler beim Laden der Mitglieder" },
          { status: 500 }
        )
      })
    )

    const { result } = renderHook(() => useMembers(0, 10), { wrapper })

    await waitFor(() => expect(result.current.isError).toBe(true))

    expect(result.current.error).toBeDefined()
  })

  it("sollte Pagination unterstützen", async () => {
    const { result } = renderHook(() => useMembers(0, 2), { wrapper })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    expect(result.current.data?.data).toHaveLength(2)
    expect(result.current.data?.page.size).toBe(2)
    expect(result.current.data?.page.totalPages).toBe(2)
  })

  it("sollte Daten neu laden können", async () => {
    const { result } = renderHook(() => useMembers(0, 10), { wrapper })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    const initialData = result.current.data

    await result.current.refetch()

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    expect(result.current.data).toEqual(initialData)
  })
})
