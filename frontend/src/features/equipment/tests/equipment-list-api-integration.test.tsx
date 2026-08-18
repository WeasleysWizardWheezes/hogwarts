import { screen, waitFor } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { describe, it, expect, vi, beforeEach } from "vitest"
import { renderWithProviders, server, http, HttpResponse } from "@/test"

vi.mock("@/shared/api/client", async () => {
  const { default: createClient } = await import("openapi-fetch")
  return {
    api: createClient({
      baseUrl: import.meta.env.VITE_API_URL || "http://localhost:3000",
      fetch: (...args: Parameters<typeof globalThis.fetch>) => globalThis.fetch(...args),
    }),
  }
})

vi.mock("react-router", async () => {
  const actual = await vi.importActual<typeof import("react-router")>("react-router")
  return {
    ...actual,
    useNavigate: () => vi.fn(),
  }
})

import EquipmentListPage from "../pages/equipment-list-page"

const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:3000"

const mockCategories = [
  { id: "cat-atemschutz-01", name: "Atemschutz" },
  { id: "cat-funk-01", name: "Funk" },
]

const mockVehicles = [
  { id: "veh-hlf20-01", name: "01-HLF20-01" },
]

const mockEquipmentItems = [
  {
    id: "equip-pa300-01",
    name: "Pressluftatmer PA 300",
    inventoryNumber: "AGT-2024-0042",
    categoryId: "cat-atemschutz-01",
    categoryName: "Atemschutz",
    vehicleId: "veh-hlf20-01",
    vehicleName: "01-HLF20-01",
    status: "VERFUEGBAR",
    nextInspectionDate: null,
  },
  {
    id: "equip-funk-03",
    name: "Funkgerät 3",
    inventoryNumber: "FUNK-2023-003",
    categoryId: "cat-funk-01",
    categoryName: "Funk",
    status: "DEFEKT",
    nextInspectionDate: "2025-06-01",
  },
]

function setupDefaultHandlers() {
  server.use(
    http.get(`${BASE_URL}/api/v1/equipment`, () => {
      return HttpResponse.json({
        data: mockEquipmentItems,
        page: { page: 0, size: 20, totalElements: 2, totalPages: 1 },
      })
    }),
    http.get(`${BASE_URL}/api/v1/equipment-categories`, () => {
      return HttpResponse.json({
        data: mockCategories,
        page: { page: 0, size: 100, totalElements: 2, totalPages: 1 },
      })
    }),
    http.get(`${BASE_URL}/api/v1/vehicles`, () => {
      return HttpResponse.json({
        data: mockVehicles,
        page: { page: 0, size: 20, totalElements: 1, totalPages: 1 },
      })
    }),
  )
}

describe("EquipmentListPage API Integration", () => {
  beforeEach(() => {
    setupDefaultHandlers()
  })

  it("shows loading state initially", () => {
    renderWithProviders(<EquipmentListPage />)
    expect(screen.queryByText("Pressluftatmer PA 300")).not.toBeInTheDocument()
  })

  it("renders equipment list after successful fetch", async () => {
    renderWithProviders(<EquipmentListPage />)
    expect(await screen.findByText("Pressluftatmer PA 300")).toBeInTheDocument()
    expect(screen.getByText("AGT-2024-0042")).toBeInTheDocument()
    expect(screen.getByText("Verfügbar")).toBeInTheDocument()
    expect(screen.getByText("Funkgerät 3")).toBeInTheDocument()
    expect(screen.getByText("Defekt")).toBeInTheDocument()
  })

  it("shows empty state when API returns no equipment", async () => {
    server.use(
      http.get(`${BASE_URL}/api/v1/equipment`, () => {
        return HttpResponse.json({
          data: [],
          page: { page: 0, size: 20, totalElements: 0, totalPages: 0 },
        })
      }),
    )
    renderWithProviders(<EquipmentListPage />)
    expect(await screen.findByText(/Keine Geräte gefunden/i)).toBeInTheDocument()
  })

  it("shows error state on API failure", async () => {
    server.use(
      http.get(`${BASE_URL}/api/v1/equipment`, () => {
        return HttpResponse.json({ message: "Server error" }, { status: 500 })
      }),
    )
    renderWithProviders(<EquipmentListPage />)
    expect(await screen.findByText(/konnten nicht geladen werden/i)).toBeInTheDocument()
  })

  it("creates a new equipment item via API", async () => {
    const user = userEvent.setup()
    let requestBody: unknown = null

    server.use(
      http.post(`${BASE_URL}/api/v1/equipment`, async ({ request }) => {
        requestBody = await request.json()
        return HttpResponse.json(
          {
            id: "equip-wbk-01",
            name: "Wärmebildkamera",
            inventoryNumber: "WBK-2026-001",
            categoryId: "cat-atemschutz-01",
            categoryName: "Atemschutz",
            status: "VERFUEGBAR",
          },
          { status: 201 },
        )
      }),
    )

    renderWithProviders(<EquipmentListPage />)
    expect(await screen.findByText("Pressluftatmer PA 300")).toBeInTheDocument()

    await user.click(screen.getByRole("button", { name: /Gerät erstellen/i }))

    await waitFor(() => {
      expect(screen.getByLabelText("Name *")).toBeInTheDocument()
    })

    await user.type(screen.getByLabelText("Name *"), "Wärmebildkamera")
    await user.type(screen.getByLabelText("Inventarnummer *"), "WBK-2026-001")

    await user.click(screen.getByRole("combobox", { name: /Kategorie/i }))
    await user.click(await screen.findByRole("option", { name: "Atemschutz" }))

    await user.click(screen.getByRole("button", { name: "Erstellen" }))

    await waitFor(() => {
      expect(requestBody).toMatchObject({
        name: "Wärmebildkamera",
        inventoryNumber: "WBK-2026-001",
        categoryId: "cat-atemschutz-01",
      })
    })
  })

  it("shows HTTP 409 error on duplicate inventory number", async () => {
    const user = userEvent.setup()

    server.use(
      http.post(`${BASE_URL}/api/v1/equipment`, () => {
        return HttpResponse.json({ message: "Inventory number already exists" }, { status: 409 })
      }),
    )

    renderWithProviders(<EquipmentListPage />)
    expect(await screen.findByText("Pressluftatmer PA 300")).toBeInTheDocument()

    await user.click(screen.getByRole("button", { name: /Gerät erstellen/i }))

    await waitFor(() => {
      expect(screen.getByLabelText("Name *")).toBeInTheDocument()
    })

    await user.type(screen.getByLabelText("Name *"), "Pressluftatmer PA 300")
    await user.type(screen.getByLabelText("Inventarnummer *"), "AGT-2024-0042")

    await user.click(screen.getByRole("combobox", { name: /Kategorie/i }))
    await user.click(await screen.findByRole("option", { name: "Atemschutz" }))

    await user.click(screen.getByRole("button", { name: "Erstellen" }))

    // onError fires toast.error – dialog closes
    await waitFor(() => {
      expect(screen.queryByRole("heading", { name: "Gerät erstellen" })).not.toBeInTheDocument()
    })
  })

  it("archives an equipment item via API", async () => {
    const user = userEvent.setup()
    let deletedId: string | null = null

    server.use(
      http.delete(`${BASE_URL}/api/v1/equipment/:equipmentId`, ({ params }) => {
        deletedId = params.equipmentId as string
        return new HttpResponse(null, { status: 204 })
      }),
    )

    renderWithProviders(<EquipmentListPage />)
    expect(await screen.findByText("Funkgerät 3")).toBeInTheDocument()

    const { within } = await import("@testing-library/react")
    const row = screen.getByRole("row", { name: /Funkgerät 3/i })
    await user.click(within(row).getByRole("button", { name: /Funkgerät 3 archivieren/i }))

    await waitFor(() => {
      expect(screen.getByRole("heading", { name: "Gerät archivieren" })).toBeInTheDocument()
    })

    await user.click(screen.getByRole("button", { name: "Archivieren" }))

    await waitFor(() => {
      expect(deletedId).toBe("equip-funk-03")
    })
  })

  it("sends search query parameter to API", async () => {
    const user = userEvent.setup()
    let lastUrl: URL | null = null

    server.use(
      http.get(`${BASE_URL}/api/v1/equipment`, ({ request }) => {
        lastUrl = new URL(request.url)
        return HttpResponse.json({
          data: [],
          page: { page: 0, size: 20, totalElements: 0, totalPages: 0 },
        })
      }),
    )

    renderWithProviders(<EquipmentListPage />)
    await screen.findByRole("textbox", { name: /Geräte suchen/i })

    await user.type(screen.getByRole("textbox", { name: /Geräte suchen/i }), "Funk")

    await waitFor(() => {
      expect(lastUrl?.searchParams.get("search")).toBe("Funk")
    })
  })

  it("sends categoryId filter parameter to API", async () => {
    const user = userEvent.setup()
    let lastUrl: URL | null = null

    server.use(
      http.get(`${BASE_URL}/api/v1/equipment`, ({ request }) => {
        lastUrl = new URL(request.url)
        return HttpResponse.json({
          data: mockEquipmentItems,
          page: { page: 0, size: 20, totalElements: 2, totalPages: 1 },
        })
      }),
    )

    renderWithProviders(<EquipmentListPage />)
    expect(await screen.findByText("Pressluftatmer PA 300")).toBeInTheDocument()

    await user.click(screen.getByRole("combobox", { name: /Nach Kategorie filtern/i }))
    await user.click(await screen.findByRole("option", { name: "Atemschutz" }))

    await waitFor(() => {
      expect(lastUrl?.searchParams.get("categoryId")).toBe("cat-atemschutz-01")
    })
  })

  it("sends status filter parameter to API", async () => {
    const user = userEvent.setup()
    let lastUrl: URL | null = null

    server.use(
      http.get(`${BASE_URL}/api/v1/equipment`, ({ request }) => {
        lastUrl = new URL(request.url)
        return HttpResponse.json({
          data: mockEquipmentItems,
          page: { page: 0, size: 20, totalElements: 2, totalPages: 1 },
        })
      }),
    )

    renderWithProviders(<EquipmentListPage />)
    expect(await screen.findByText("Pressluftatmer PA 300")).toBeInTheDocument()

    await user.click(screen.getByRole("combobox", { name: /Nach Status filtern/i }))
    await user.click(await screen.findByRole("option", { name: "Defekt" }))

    await waitFor(() => {
      expect(lastUrl?.searchParams.get("status")).toBe("DEFEKT")
    })
  })
})
