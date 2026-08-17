import "@testing-library/jest-dom/vitest"
import { cleanup } from "@testing-library/react"
import { afterAll, afterEach, beforeAll, vi } from "vitest"
import { server } from "./mocks/server"

// Fix: openapi-fetch captures globalThis.fetch at module-load time,
// before MSW patches it. We mock the api module to use a lazy fetch binding.
vi.mock("@/shared/api/client", async () => {
  const { default: createClient } = await import("openapi-fetch") as { default: typeof import("openapi-fetch").default }
  return {
    api: createClient({
      baseUrl: import.meta.env.VITE_API_URL ?? "",
      fetch: (...args: Parameters<typeof fetch>) => globalThis.fetch(...args),
    }),
  }
})

// Start MSW server before all tests
beforeAll(() => server.listen({ onUnhandledRequest: "error" }))

// Reset handlers between tests (clean slate)
afterEach(() => {
  cleanup()
  server.resetHandlers()
})

// Close MSW server after all tests
afterAll(() => server.close())
