import { api } from "@/shared/api"




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
