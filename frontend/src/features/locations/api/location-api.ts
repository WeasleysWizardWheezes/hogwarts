import { api } from "@/shared/api"
import type { components } from "@weasleyswizardwheezes/hogwarts-api-client"

export async function assignMemberToLocation(
  memberId: string,
  locationId: string
) {
  const { data, error } = await api.POST("/api/v1/members/{memberId}/locations", {
    params: { path: { memberId } },
    body: { locationId },
  })
  if (error) throw error
  return data
}

export async function getLocations(page: number = 0, size: number = 10) {
  const { data, error } = await api.GET("/api/v1/locations", {
    params: { query: { page, size } },
  })
  if (error) throw error
  return data
}

export async function getLocationById(id: string) {
  const { data, error } = await api.GET("/api/v1/locations/{locationId}", {
    params: { path: { locationId: id } },
  })
  if (error) throw error
  return data
}

export async function createLocation(
  body: components["schemas"]["CreateLocationRequest"]
) {
  const { data, error } = await api.POST("/api/v1/locations", {
    body,
  })
  if (error) throw error
  return data
}

export async function updateLocation(
  id: string,
  body: components["schemas"]["UpdateLocationRequest"]
) {
  const { data, error } = await api.PUT("/api/v1/locations/{locationId}", {
    params: { path: { locationId: id } },
    body,
  })
  if (error) throw error
  return data
}

export async function deleteLocation(id: string) {
  const { error } = await api.DELETE("/api/v1/locations/{locationId}", {
    params: { path: { locationId: id } },
  })
  if (error) throw error
}
