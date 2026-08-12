import { describe, it, expect, beforeAll, afterAll, afterEach } from "vitest"
import { setupServer } from "msw/node"
import { handlers } from "@/test/api-handlers"
import { getLocations, getLocationById, createLocation, updateLocation, deleteLocation, assignMemberToLocation } from "./location-api"

type Location = {
  id: string
  name: string
  address?: string
  type: string
  createdAt: string
  updatedAt: string
}

type LocationPage = {
  data: Location[]
  page: {
    size: number
    totalElements: number
    totalPages: number
    number: number
  }
}

const server = setupServer(...handlers)

beforeAll(() => server.listen())
afterEach(() => server.resetHandlers())
afterAll(() => server.close())

describe("Location API", () => {
  describe("getLocations", () => {
    it("sollte eine Liste von Standorten zurückgeben", async () => {
      const result = await getLocations(0, 10)

      const expected: LocationPage = {
        data: [
          {
            id: "location-1",
            name: "Hauptwache",
            address: "Hauptstraße 1",
            type: "FIRE_STATION",
            createdAt: "2026-01-01T00:00:00Z",
            updatedAt: "2026-01-01T00:00:00Z"
          },
          {
            id: "location-2",
            name: "Gerätedepot",
            address: "Industriestraße 10",
            type: "EQUIPMENT_DEPOT",
            createdAt: "2026-01-01T00:00:00Z",
            updatedAt: "2026-01-01T00:00:00Z"
          },
          {
            id: "location-3",
            name: "Ausbildungszentrum",
            address: "Schulweg 5",
            type: "TRAINING_CENTER",
            createdAt: "2026-01-01T00:00:00Z",
            updatedAt: "2026-01-01T00:00:00Z"
          }
        ],
        page: {
          size: 10,
          totalElements: 3,
          totalPages: 1,
          number: 0
        }
      }

      expect(result).toEqual(expected)
    })

    it("sollte Pagination unterstützen", async () => {
      const result = await getLocations(0, 2)

      expect(result.data).toHaveLength(2)
      expect(result.page.size).toBe(2)
      expect(result.page.totalPages).toBe(2)
    })
  })

  describe("getLocationById", () => {
    it("sollte einen Standort nach ID zurückgeben", async () => {
      const result = await getLocationById("location-1")

      expect(result).toEqual({
        id: "location-1",
        name: "Hauptwache",
        address: "Hauptstraße 1",
        type: "FIRE_STATION",
        createdAt: "2026-01-01T00:00:00Z",
        updatedAt: "2026-01-01T00:00:00Z"
      })
    })
  })

  describe("createLocation", () => {
    it("sollte einen neuen Standort erstellen", async () => {
      const newLocation = {
        name: "Neue Wache",
        address: "Neue Straße 1",
        type: "FIRE_STATION"
      } as any

      const result = await createLocation(newLocation)

      expect(result).toEqual({
        id: "location-new",
        name: "Neue Wache",
        address: "Neue Straße 1",
        type: "FIRE_STATION",
        createdAt: "2026-01-01T00:00:00Z",
        updatedAt: "2026-01-01T00:00:00Z"
      })
    })
  })

  describe("updateLocation", () => {
    it("sollte einen Standort aktualisieren", async () => {
      const updatedLocation = {
        name: "Hauptwache (aktualisiert)",
        address: "Hauptstraße 1 (aktualisiert)",
        type: "FIRE_STATION"
      } as any

      const result = await updateLocation("location-1", updatedLocation)

      expect(result).toEqual({
        id: "location-1",
        name: "Hauptwache (aktualisiert)",
        address: "Hauptstraße 1 (aktualisiert)",
        type: "FIRE_STATION",
        createdAt: "2026-01-01T00:00:00Z",
        updatedAt: "2026-01-01T00:00:00Z"
      })
    })
  })

  describe("deleteLocation", () => {
    it("sollte einen Standort löschen", async () => {
      await expect(deleteLocation("location-1")).resolves.not.toThrow()
    })
  })

  describe("assignMemberToLocation", () => {
    it("sollte ein Mitglied einem Standort zuordnen", async () => {
      const result = await assignMemberToLocation("member-1", "location-1")

      expect(result).toEqual({
        memberId: "member-1",
        locationId: "location-1"
      })
    })
  })
})
