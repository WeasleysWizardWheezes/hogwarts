const API_BASE_URL = (import.meta as any).env?.VITE_API_URL ?? ""

export interface Vehicle {
  id?: string
  name: string
  licensePlate: string
  type: string
  status: string
  vehicleGroupId?: string
}

export interface VehicleGroup {
  id?: string
  name: string
  description?: string
}

async function handleResponse(response: Response) {
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}))
    throw new Error(errorData.message || "Request failed")
  }
  return response.json()
}

export async function getVehicles() {
  const response = await fetch(`${API_BASE_URL}/api/v1/vehicles`)
  return handleResponse(response)
}

export async function getVehicleById(id: string) {
  const response = await fetch(`${API_BASE_URL}/api/v1/vehicles/${id}`)
  return handleResponse(response)
}

export async function createVehicle(vehicle: Omit<Vehicle, 'id'>) {
  const response = await fetch(`${API_BASE_URL}/api/v1/vehicles`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(vehicle),
  })
  return handleResponse(response)
}

export async function updateVehicle(id: string, vehicle: Vehicle) {
  const response = await fetch(`${API_BASE_URL}/api/v1/vehicles/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(vehicle),
  })
  return handleResponse(response)
}

export async function deleteVehicle(id: string) {
  const response = await fetch(`${API_BASE_URL}/api/v1/vehicles/${id}`, {
    method: 'DELETE',
  })
  return handleResponse(response)
}

export async function getVehicleGroups() {
  const response = await fetch(`${API_BASE_URL}/api/v1/vehicle-groups`)
  return handleResponse(response)
}

export async function getVehicleGroupById(id: string) {
  const response = await fetch(`${API_BASE_URL}/api/v1/vehicle-groups/${id}`)
  return handleResponse(response)
}

export async function createVehicleGroup(group: Omit<VehicleGroup, 'id'>) {
  const response = await fetch(`${API_BASE_URL}/api/v1/vehicle-groups`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(group),
  })
  return handleResponse(response)
}

export async function updateVehicleGroup(id: string, group: VehicleGroup) {
  const response = await fetch(`${API_BASE_URL}/api/v1/vehicle-groups/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(group),
  })
  return handleResponse(response)
}

export async function deleteVehicleGroup(id: string) {
  const response = await fetch(`${API_BASE_URL}/api/v1/vehicle-groups/${id}`, {
    method: 'DELETE',
  })
  return handleResponse(response)
}