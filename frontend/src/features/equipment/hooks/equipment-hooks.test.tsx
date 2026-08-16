import { describe, it, expect, vi, beforeEach } from "vitest"
import { render, screen } from "@testing-library/react"
import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import { useCreateEquipment } from "./use-create-equipment"
import { useDeleteEquipment } from "./use-delete-equipment"
import { useUpdateEquipment } from "./use-update-equipment"
import { useEquipmentDetail } from "./use-equipment-detail"

describe("Equipment API Hooks", () => {
  describe("useCreateEquipment", () => {
    it("returns a mutate function", () => {
      const client = new QueryClient({
        defaultOptions: {
          queries: { retry: false },
        },
      })

      const TestComponent = () => {
        const result = useCreateEquipment()
        return <div>{typeof result.mutate === "function" ? "HasMutate" : "NoMutate"}</div>
      }

      render(
        <QueryClientProvider client={client}>
          <TestComponent />
        </QueryClientProvider>,
      )

      expect(screen.getByText("HasMutate")).toBeInTheDocument()
    })
  })

  describe("useUpdateEquipment", () => {
    it("returns a mutate function", () => {
      const client = new QueryClient({
        defaultOptions: {
          queries: { retry: false },
        },
      })

      const TestComponent = () => {
        const result = useUpdateEquipment()
        return <div>{typeof result.mutate === "function" ? "HasMutate" : "NoMutate"}</div>
      }

      render(
        <QueryClientProvider client={client}>
          <TestComponent />
        </QueryClientProvider>,
      )

      expect(screen.getByText("HasMutate")).toBeInTheDocument()
    })
  })

  describe("useDeleteEquipment", () => {
    it("returns a mutate function", () => {
      const client = new QueryClient({
        defaultOptions: {
          queries: { retry: false },
        },
      })

      const TestComponent = () => {
        const result = useDeleteEquipment()
        return <div>{typeof result.mutate === "function" ? "HasMutate" : "NoMutate"}</div>
      }

      render(
        <QueryClientProvider client={client}>
          <TestComponent />
        </QueryClientProvider>,
      )

      expect(screen.getByText("HasMutate")).toBeInTheDocument()
    })
  })

  describe("useEquipmentDetail", () => {
    it("returns a query object", () => {
      const client = new QueryClient({
        defaultOptions: {
          queries: { retry: false },
        },
      })

      const TestComponent = () => {
        const result = useEquipmentDetail("1")
        return <div>{result.isSuccess ? "Success" : "Pending"}</div>
      }

      render(
        <QueryClientProvider client={client}>
          <TestComponent />
        </QueryClientProvider>,
      )

      expect(screen.getByText("Pending")).toBeInTheDocument()
    })

    it("does not fetch when id is empty", async () => {
      const client = new QueryClient({
        defaultOptions: {
          queries: { retry: false },
        },
      })

      const TestComponent = () => {
        const result = useEquipmentDetail("")
        return <div>{result.isLoading ? "Loading" : "NotLoading"}</div>
      }

      render(
        <QueryClientProvider client={client}>
          <TestComponent />
        </QueryClientProvider>,
      )

      expect(screen.getByText("NotLoading")).toBeInTheDocument()
    })
  })
})