import { api } from "@/shared/api"

export async function getEquipmentList() {
  const { data, error } = await api.GET("/api/equipment")
  if (error) throw error
  return data
}

export async function createEquipment(body: components["schemas"]["CreateEquipmentRequest"]) {
  const { data, error } = await api.POST("/api/equipment", { body })
  if (error) throw error
  return data
}

export async function updateEquipmentStatus(id: string, status: string) {
  const { data, error } = await api.PATCH("/api/equipment/{id}/status", {
    params: { path: { id } },
    body: { status },
  })
  if (error) throw error
  return data
}

export async function deleteEquipment(id: string) {
  const { data, error } = await api.DELETE("/api/equipment/{id}", {
    params: { path: { id } },
  })
  if (error) throw error
  return data
}
