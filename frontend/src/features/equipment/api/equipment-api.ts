import { api } from "@/shared/api";
import type { components, operations } from "@/shared/api/schema";

export async function getAllEquipment() {
  const { data, error } = await api.GET("/api/material-geraete");
  if (error) throw error;
  return data;
}

export async function getEquipmentById(id: string) {
  const { data, error } = await api.GET("/api/material-geraete/{id}", {
    params: { path: { id } },
  });
  if (error) throw error;
  return data;
}

export async function createEquipment(
  body: components["schemas"]["CreateEquipmentRequest"]
) {
  const { data, error } = await api.POST("/api/material-geraete", {
    body,
  });
  if (error) throw error;
  return data;
}

export async function deleteEquipment(id: string) {
  const { error } = await api.DELETE("/api/material-geraete/{id}", {
    params: { path: { id } },
  });
  if (error) throw error;
}

export async function searchEquipment(
  params: {
    name?: string;
    serialNumber?: string;
    storageLocation?: string;
  }
) {
  const { data, error } = await api.GET("/api/material-geraete/search", {
    params: { query: params },
  });
  if (error) throw error;
  return data;
}
