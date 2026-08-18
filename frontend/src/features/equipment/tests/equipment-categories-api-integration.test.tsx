import { screen, waitFor } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { describe, it, expect, vi, beforeEach } from "vitest"
import { renderWithProviders, server, http, HttpResponse } from "@/test"

// Patch openapi-fetch client to use globalThis.fetch so MSW can intercept
vi.mock("@/shared/api/client", async () => {
  const { default: createClient } = await import("openapi-fetch")
  return {
    api: createClient({
      baseUrl: import.meta.env.VITE_API_URL || "http://localhost:3000",
      fetch: (...args: Parameters<typeof globalThis.fetch>) => globalThis.fetch(...args),
    }),
  }
})

import EquipmentCategoriesPage from "../pages/equipment-categories-page"

const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:3000"

const mockCategories = [
  { id: "cat-atemschutz-01", name: "Atemschutz", description: "Atemschutzgeräte" },
  { id: "cat-funk-01", name: "Funk", description: undefined },
]

function setupDefaultHandlers() {
  server.use(
    http.get(`${BASE_URL}/api/v1/equipment-categories`, () => {
      return HttpResponse.json({
        data: mockCategories,
        page: { page: 0, size: 100, totalElements: 2, totalPages: 1 },
      })
    }),
  )
}

describe("EquipmentCategoriesPage API Integration", () => {
  beforeEach(() => {
    setupDefaultHandlers()
  })

  it("shows loading state initially", () => {
    renderWithProviders(<EquipmentCategoriesPage />)
    // During load, table content is not yet visible
    expect(screen.queryByText("Atemschutz")).not.toBeInTheDocument()
  })

  it("renders categories after successful fetch", async () => {
    renderWithProviders(<EquipmentCategoriesPage />)
    expect(await screen.findByText("Atemschutz")).toBeInTheDocument()
    expect(screen.getByText("Atemschutzgeräte")).toBeInTheDocument()
    expect(screen.getByText("Funk")).toBeInTheDocument()
  })

  it("shows empty state when API returns no categories", async () => {
    server.use(
      http.get(`${BASE_URL}/api/v1/equipment-categories`, () => {
        return HttpResponse.json({
          data: [],
          page: { page: 0, size: 100, totalElements: 0, totalPages: 0 },
        })
      }),
    )
    renderWithProviders(<EquipmentCategoriesPage />)
    expect(await screen.findByText(/Keine Kategorien vorhanden/i)).toBeInTheDocument()
  })

  it("shows error state on API failure", async () => {
    server.use(
      http.get(`${BASE_URL}/api/v1/equipment-categories`, () => {
        return HttpResponse.json({ message: "Server error" }, { status: 500 })
      }),
    )
    renderWithProviders(<EquipmentCategoriesPage />)
    expect(await screen.findByText(/konnten nicht geladen werden/i)).toBeInTheDocument()
  })

  it("creates a new category via API and shows success toast", async () => {
    const user = userEvent.setup()
    let requestBody: unknown = null

    server.use(
      http.post(`${BASE_URL}/api/v1/equipment-categories`, async ({ request }) => {
        requestBody = await request.json()
        return HttpResponse.json(
          { id: "cat-hydraulik-01", name: "Hydraulik", description: "Hydraulisches Rettungsgerät" },
          { status: 201 },
        )
      }),
    )

    renderWithProviders(<EquipmentCategoriesPage />)
    expect(await screen.findByText("Atemschutz")).toBeInTheDocument()

    await user.click(screen.getByRole("button", { name: /Kategorie erstellen/i }))

    await waitFor(() => {
      expect(screen.getByLabelText("Name *")).toBeInTheDocument()
    })

    await user.type(screen.getByLabelText("Name *"), "Hydraulik")
    await user.type(screen.getByLabelText("Beschreibung"), "Hydraulisches Rettungsgerät")

    await user.click(screen.getByRole("button", { name: "Erstellen" }))

    await waitFor(() => {
      expect(requestBody).toMatchObject({ name: "Hydraulik" })
    })
  })

  it("shows HTTP 409 toast error on duplicate category name", async () => {
    const user = userEvent.setup()

    server.use(
      http.post(`${BASE_URL}/api/v1/equipment-categories`, () => {
        return HttpResponse.json(
          { message: "Name already exists" },
          { status: 409 },
        )
      }),
    )

    renderWithProviders(<EquipmentCategoriesPage />)
    expect(await screen.findByText("Atemschutz")).toBeInTheDocument()

    await user.click(screen.getByRole("button", { name: /Kategorie erstellen/i }))

    await waitFor(() => {
      expect(screen.getByLabelText("Name *")).toBeInTheDocument()
    })

    await user.type(screen.getByLabelText("Name *"), "Atemschutz")
    await user.click(screen.getByRole("button", { name: "Erstellen" }))

    // Dialog closes or error toast shows (onError → toast.error)
    await waitFor(() => {
      expect(screen.queryByRole("heading", { name: "Kategorie erstellen" })).not.toBeInTheDocument()
    })
  })

  it("archives a category via API", async () => {
    const user = userEvent.setup()
    let archivedId: string | null = null

    server.use(
      http.delete(`${BASE_URL}/api/v1/equipment-categories/:categoryId`, ({ params }) => {
        archivedId = params.categoryId as string
        return new HttpResponse(null, { status: 204 })
      }),
    )

    renderWithProviders(<EquipmentCategoriesPage />)
    expect(await screen.findByText("Funk")).toBeInTheDocument()

    const row = screen.getByRole("row", { name: /Funk/i })
    const { within: withinRow } = await import("@testing-library/react")
    await user.click(withinRow(row).getByRole("button", { name: /Funk archivieren/i }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Kategorie archivieren" })).toBeInTheDocument()
    })

    await user.click(screen.getByRole("button", { name: "Archivieren" }))

    await waitFor(() => {
      expect(archivedId).toBe("cat-funk-01")
    })
  })

  it("updates an existing category via API", async () => {
    const user = userEvent.setup()
    let requestBody: unknown = null

    server.use(
      http.put(`${BASE_URL}/api/v1/equipment-categories/:categoryId`, async ({ request }) => {
        requestBody = await request.json()
        return HttpResponse.json({ id: "cat-atemschutz-01", name: "Atemschutz Neu" })
      }),
    )

    renderWithProviders(<EquipmentCategoriesPage />)
    expect(await screen.findByText("Atemschutz")).toBeInTheDocument()

    const row = screen.getByRole("row", { name: /Atemschutz/i })
    const { within: withinRow } = await import("@testing-library/react")
    await user.click(withinRow(row).getByRole("button", { name: /Atemschutz bearbeiten/i }))

    await waitFor(() => {
      expect(screen.getByLabelText("Name *")).toBeInTheDocument()
    })

    const nameInput = screen.getByLabelText("Name *")
    await user.clear(nameInput)
    await user.type(nameInput, "Atemschutz Neu")

    await user.click(screen.getByRole("button", { name: "Speichern" }))

    await waitFor(() => {
      expect(requestBody).toMatchObject({ name: "Atemschutz Neu" })
    })
  })
})
