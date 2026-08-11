import { api } from "@/shared/api"
import type { components } from "@weasleyswizardwheezes/hogwarts-api-client"

export async function createMember(
  body: components["schemas"]["CreateMemberRequest"]
) {
  const { data, error } = await api.POST("/api/v1/members", {
    body,
  })
  if (error) throw error
  return data
}

export async function updateMember(
  id: string,
  body: components["schemas"]["UpdateMemberRequest"]
) {
  const { data, error } = await api.PUT("/api/v1/members/{memberId}", {
    params: { path: { memberId: id } },
    body,
  })
  if (error) throw error
  return data
}


export async function getMembers(page: number = 0, size: number = 10, locationId?: string) {
  const { data, error } = await api.GET("/api/v1/members", {
    params: { query: { page, size, locationId } },
  })
  if (error) throw error
  return data
}

export async function getMemberById(id: string) {
  const { data, error } = await api.GET("/api/v1/members/{memberId}", {
    params: { path: { memberId: id } },
  })
  if (error) throw error
  return data
}
