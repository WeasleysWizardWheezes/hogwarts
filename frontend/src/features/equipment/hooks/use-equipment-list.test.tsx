import { describe, it, expect, vi } from "vitest"
import { render, screen } from "@testing-library/react"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import { useEquipmentList } from "./use-equipment-list"

describe("useEquipmentList", () => {
  it("returns a query object", () => {
    const client = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
      },
    })

    const TestComponent = () => {
      const result = useEquipmentList()
      return <div>{result.isSuccess ? "Success" : "Pending"}</div>
    }

    render(
      <QueryClientProvider client={client}>
        <TestComponent />
      </QueryClientProvider>,
    )

    expect(screen.getByText("Pending")).toBeInTheDocument()
  })
})