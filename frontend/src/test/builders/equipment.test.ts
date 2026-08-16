import { describe, it, expect } from "vitest"
import { buildEquipment } from "./equipment"

describe("buildEquipment", () => {
  it("gibt defaults zurück", () => {
    const equipment = buildEquipment()

    expect(equipment).toMatchObject({
      id: "equipment-test-1",
      name: "Testgerät",
      category: "Funk",
      status: "EINSATZBEREIT",
      location: "Gerätehaus",
      vehicle: null,
      nextInspectionDate: null,
      defects: [],
    })
  })

  it("überschreibt defaults mit overrides", () => {
    const equipment = buildEquipment({
      id: "equipment-radio-3",
      name: "Funkgerät 3",
      status: "DEFEKT",
      location: "HLF 20 > Geräteraum G1",
    })

    expect(equipment).toMatchObject({
      id: "equipment-radio-3",
      name: "Funkgerät 3",
      status: "DEFEKT",
      location: "HLF 20 > Geräteraum G1",
    })

    // Prüfe, dass nicht-überschriebene Defaults erhalten bleiben
    expect(equipment.category).toBe("Funk")
    expect(equipment.vehicle).toBeNull()
  })

  it("erlaubt leeren override", () => {
    const equipment = buildEquipment({})
    expect(equipment.id).toBe("equipment-test-1")
  })
})