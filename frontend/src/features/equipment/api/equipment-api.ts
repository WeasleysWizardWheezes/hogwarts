import type { components } from "@/shared/api"
import { api } from "@/shared/api"

export async function getDeviceList(search?: string, location?: string) {
  const searchParams = new URLSearchParams()
  if (search) searchParams.set("search", search)
  if (location) searchParams.set("location", location)

  const url = `/api/v1/equipment/devices${searchParams.size > 0 ? `?${searchParams.toString()}` : ""}`

  const { data, error } = await api.GET(url)
  if (error) throw error
  return data
}

export async function getDeviceById(id: string) {
  const { data, error } = await api.GET("/api/v1/equipment/devices/{id}", {
    params: { path: { id } },
  })
  if (error) throw error
  return data
}

export async function getDeviceBySerialNumber(serialNumber: string) {
  const { data, error } = await api.GET("/api/v1/equipment/devices/serial-number/{serialNumber}", {
    params: { path: { serialNumber } },
  })
  if (error) throw error
  return data
}

export async function createDevice(body: components["schemas"]["DeviceDto"]) {
  const { data, error } = await api.POST("/api/v1/equipment/devices", { body })
  if (error) throw error
  return data
}

export async function updateDevice(id: string, body: components["schemas"]["DeviceDto"]) {
  const { data, error } = await api.PUT("/api/v1/equipment/devices/{id}", {
    params: { path: { id } },
    body,
  })
  if (error) throw error
  return data
}

export async function partialUpdateDevice(id: string, body: Partial<components["schemas"]["DeviceDto"]>) {
  const { data, error } = await api.PATCH("/api/v1/equipment/devices/{id}", {
    params: { path: { id } },
    body,
  })
  if (error) throw error
  return data
}

export async function deleteDevice(id: string) {
  const { error } = await api.DELETE("/api/v1/equipment/devices/{id}", {
    params: { path: { id } },
  })
  if (error) throw error
}
