import "@testing-library/jest-dom/vitest"
import { cleanup } from "@testing-library/react"
import { afterEach } from "vitest"

// Clean up rendered components between tests
afterEach(() => {
  cleanup()
})
