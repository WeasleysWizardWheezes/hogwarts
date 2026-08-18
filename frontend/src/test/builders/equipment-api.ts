/**
 * Testdaten-Builder für Equipment-API-Typen (EquipmentResponse, EquipmentCategoryResponse, EquipmentHistoryResponse).
 *
 * Verwendung:
 *   const equipment = buildEquipmentResponse({ status: "DEFEKT" })
 *   const category = buildEquipmentCategoryResponse({ name: "Atemschutz" })
 *   const history = buildEquipmentHistoryResponse({ newStatus: "DEFEKT" })
 */

export interface EquipmentResponseData {
  id: string
  name: string
  inventoryNumber: string
  description?: string
  categoryId: string
  categoryName: string
  vehicleId?: string
  vehicleName?: string
  status: "VERFUEGBAR" | "IN_GEBRAUCH" | "DEFEKT" | "WARTUNG" | "ARCHIVIERT"
  nextInspectionDate?: string
  nextMaintenanceDate?: string
}

export interface EquipmentCategoryResponseData {
  id: string
  name: string
  description?: string
}

export interface EquipmentHistoryResponseData {
  id: string
  changedAt: string
  previousStatus?: string
  newStatus?: string
}

export function buildEquipmentResponse(
  overrides: Partial<EquipmentResponseData> = {},
): EquipmentResponseData {
  return {
    id: "equip-pa300-01",
    name: "Pressluftatmer PA 300",
    inventoryNumber: "AGT-2024-0042",
    description: "Atemschutzgerät für Einsätze",
    categoryId: "cat-atemschutz-01",
    categoryName: "Atemschutz",
    status: "VERFUEGBAR",
    nextInspectionDate: undefined,
    nextMaintenanceDate: undefined,
    ...overrides,
  }
}

export function buildEquipmentCategoryResponse(
  overrides: Partial<EquipmentCategoryResponseData> = {},
): EquipmentCategoryResponseData {
  return {
    id: "cat-atemschutz-01",
    name: "Atemschutz",
    description: "Atemschutzgeräte und Zubehör",
    ...overrides,
  }
}

export function buildEquipmentHistoryResponse(
  overrides: Partial<EquipmentHistoryResponseData> = {},
): EquipmentHistoryResponseData {
  return {
    id: "history-001",
    changedAt: "2026-03-10T14:30:00Z",
    previousStatus: "VERFUEGBAR",
    newStatus: "WARTUNG",
    ...overrides,
  }
}
