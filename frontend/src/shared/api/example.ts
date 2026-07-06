/**
 * Beispiel-Hook: Zeigt das Pattern für typisierte API-Calls mit TanStack Query.
 *
 * Sobald das Backend einen Endpoint hat (z.B. GET /api/equipment),
 * wird der Hook so verwendet:
 *
 * ```ts
 * import { useQuery } from "@tanstack/react-query"
 * import { api } from "@/shared/api"
 *
 * export function useEquipment() {
 *   return useQuery({
 *     queryKey: ["equipment"],
 *     queryFn: async () => {
 *       const { data, error } = await api.GET("/api/equipment")
 *       if (error) throw error
 *       return data
 *     },
 *   })
 * }
 * ```
 *
 * - `api.GET("/api/equipment")` ist vollständig typisiert
 * - Pfade, Request-Params und Response-Types kommen automatisch aus dem Schema
 * - Autocomplete für alle verfügbaren Pfade und HTTP-Methoden
 */
export {}
