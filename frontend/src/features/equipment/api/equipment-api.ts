import type { components } from "@/shared/api"
import { api } from "@/shared/api"

export async function getDeviceList() {
  const { data, error } = await api.GET("/api/v1/equipment/devices")
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
