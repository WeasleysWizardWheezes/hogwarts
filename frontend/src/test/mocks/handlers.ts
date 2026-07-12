import type { HttpHandler } from "msw"

/**
 * Default MSW handlers for API mocking.
 *
 * Add handlers here for endpoints that should be available across tests.
 * For test-specific overrides, use server.use(...) in individual tests.
 *
 * Example:
 *   import { http, HttpResponse } from "msw"
 *
 *   http.get("/api/equipment", () => {
 *     return HttpResponse.json([])
 *   })
 */
export const handlers: HttpHandler[] = []
