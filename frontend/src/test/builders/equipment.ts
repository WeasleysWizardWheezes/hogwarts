/**
 * Testdaten-Builder für Geräte.
 *
 * Verwendung:
 *   const equipment = buildEquipment({ status: "DEFEKT" })
 *   const radio = buildEquipment({ id: "equipment-radio-3", name: "Funkgerät 3" })
 *
 * Der Builder liefert sinnvolle Defaults – nur überschreiben was relevant ist.
 * IDs sollten in Tests explizit gesetzt werden wenn sie für Assertions gebraucht werden.
 */

interface Equipment {
  id: string
  name: string
  category: string
  status: string
  location: string
  vehicle: string | null
  nextInspectionDate: string | null
  defects: string[]
}

export function buildEquipment(overrides: Partial<Equipment> = {}): Equipment {
  return {
    id: "equipment-test-1",
    name: "Testgerät",
    category: "Funk",
    status: "EINSATZBEREIT",
    location: "Gerätehaus",
    vehicle: null,
    nextInspectionDate: null,
    defects: [],
    ...overrides,
  }
}
