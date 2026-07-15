import createClient from "openapi-fetch"
import type { paths } from '@weasleyswizardwheezes/hogwarts-api-client'

export const api = createClient<paths>({
  baseUrl: import.meta.env.VITE_API_URL ?? "",
})
