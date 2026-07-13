/**
 * Re-exports for convenient test imports.
 *
 * Usage:
 *   import { renderWithProviders } from "@/test"
 *   import { screen, waitFor } from "@testing-library/react"
 *   import userEvent from "@testing-library/user-event"
 */
export { renderWithProviders, render } from "./render"
export { server } from "./mocks/server"
export { http, HttpResponse } from "msw"
