import { describe, it, expect, beforeAll, afterAll, afterEach } from "vitest"
import { renderHook, waitFor } from "@testing-library/react"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import { setupServer } from "msw/node"
import { handlers } from "@/test/api-handlers"
import { useLocations } from "./use-locations"

const server = setupServer(...handlers)

beforeAll(() => server.listen())
afterEach(() => server.resetHandlers())
afterAll(() => server.close())

describe("useLocations Hook", () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  })

  const wrapper = ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  )

  it("sollte Standorte erfolgreich laden", async () => {
    const { result } = renderHook(() => useLocations(0, 10), { wrapper })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    expect(result.current.data).toEqual({
      data: [
        {
          id: "location-1",
          name: "Hauptwache",
          address: "Hauptstraße 1",
          type: "FIRE_STATION",
          createdAt: "2026-01-01T00:00:00Z",
          updatedAt: "2026-01-01T00:00:00Z"
        },
        {
          id: "location-2",
          name: "Gerätedepot",
          address: "Industriestraße 10",
          type: "EQUIPMENT_DEPOT",
          createdAt: "2026-01-01T00:00:00Z",
          updatedAt: "2026-01-01T00:00:00Z"
        },
        {
          id: "location-3",
          name: "Ausbildungszentrum",
          address: "Schulweg 5",
          type: "TRAINING_CENTER",
          createdAt: "2026-01-01T00:00:00Z",
          updatedAt: "2026-01-01T00:00:00Z"
        }
      ],
      page: {
        size: 10,
        totalElements: 3,
        totalPages: 1,
        number: 0
      }
    })
  })

  it("sollte Ladezustand anzeigen", () => {
    const { result } = renderHook(() => useLocations(0, 10), { wrapper })

    expect(result.current.isLoading).toBe(true)
    expect(result.current.isFetching).toBe(true)
  })

  it("sollte Fehlerzustand anzeigen, wenn das Laden fehlschlägt", async () => {
    server.use(
      ...handlers,
      http.get("/api/v1/locations", () => {
        return HttpResponse.json(
          { message: "Fehler beim Laden der Standorte" },
          { status: 500 }
        )
      })
    )

    const { result } = renderHook(() => useLocations(0, 10), { wrapper })

    await waitFor(() => expect(result.current.isError).toBe(true))

    expect(result.current.error).toBeDefined()
  })

  it("sollte Pagination unterstützen", async () => {
    const { result } = renderHook(() => useLocations(0, 2), { wrapper })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    expect(result.current.data?.data).toHaveLength(2)
    expect(result.current.data?.page.size).toBe(2)
    expect(result.current.data?.page.totalPages).toBe(2)
  })

  it("sollte Daten neu laden können", async () => {
    const { result } = renderHook(() => useLocations(0, 10), { wrapper })

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    const initialData = result.current.data

    await result.current.refetch()

    await waitFor(() => expect(result.current.isSuccess).toBe(true))

    expect(result.current.data).toEqual(initialData)
  })
})
