import createClient from "openapi-fetch"
import type { paths } from "@weasleyswizardwheezes/hogwarts-api"

export const api = createClient<paths>({
  baseUrl: import.meta.env.VITE_API_URL ?? "",
})
