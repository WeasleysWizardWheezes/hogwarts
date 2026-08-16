import { describe, it, expect } from "vitest"
import { render, screen } from "@testing-library/react"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import { MemoryRouter } from "react-router"
import EquipmentListPage from "./pages/equipment-list-page"

function renderWithProviders(ui: React.ReactElement) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  })

  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>{ui}</MemoryRouter>
    </QueryClientProvider>
  )
}

describe("EquipmentListPage", () => {
  // Test 1: Zeigt Header und Button an
  it("zeigt Geräte-Überschrift und Hinzufügen-Button an", async () => {
    renderWithProviders(<EquipmentListPage />)

    await screen.findByText("Geräte")
    expect(screen.getByRole("button", { name: /Gerät hinzufügen/ })).toBeInTheDocument()
  })

  // Test 2: Zeigt Filterfelder an
  it("zeigt Such- und Lagerort-Filter an", async () => {
    renderWithProviders(<EquipmentListPage />)

    await screen.findByText("Geräte")
    expect(screen.getByPlaceholderText("Gerätename oder Seriennummer...")).toBeInTheDocument()
    expect(screen.getByPlaceholderText("Lagerort filtern...")).toBeInTheDocument()
  })
})