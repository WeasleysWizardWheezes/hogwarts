import { describe, it, expect, beforeAll, afterAll, afterEach } from "vitest"
import { setupServer } from "msw/node"
import { handlers } from "@/test/api-handlers"
import { getMembers, getMemberById } from "./member-api"

const server = setupServer(...handlers)

beforeAll(() => server.listen())
afterEach(() => server.resetHandlers())
afterAll(() => server.close())

describe("Member API", () => {
  describe("getMembers", () => {
    it("sollte eine Liste von Mitgliedern zurückgeben", async () => {
      const result = await getMembers(0, 10)

      expect(result).toEqual({
        data: [
          {
            id: "member-1",
            firstName: "Harry",
            lastName: "Potter",
            email: "harry@example.com",
            phone: "123456789",
            location: {
              id: "location-1",
              name: "Hauptwache"
            },
            createdAt: "2026-01-01T00:00:00Z",
            updatedAt: "2026-01-01T00:00:00Z"
          },
          {
            id: "member-2",
            firstName: "Hermine",
            lastName: "Granger",
            email: "hermine@example.com",
            phone: "987654321",
            location: {
              id: "location-1",
              name: "Hauptwache"
            },
            createdAt: "2026-01-01T00:00:00Z",
            updatedAt: "2026-01-01T00:00:00Z"
          },
          {
            id: "member-3",
            firstName: "Ron",
            lastName: "Weasley",
            email: "ron@example.com",
            phone: "555555555",
            location: {
              id: "location-2",
              name: "Gerätedepot"
            },
            createdAt: "2026-01-01T00:00:00Z",
            updatedAt: "2026-01-01T00:00:00Z"
          },
          {
            id: "member-4",
            firstName: "Albus",
            lastName: "Dumbledore",
            email: "albus@example.com",
            phone: "111111111",
            location: null,
            createdAt: "2026-01-01T00:00:00Z",
            updatedAt: "2026-01-01T00:00:00Z"
          }
        ],
        page: {
          size: 10,
          totalElements: 4,
          totalPages: 1,
          number: 0
        }
      })
    })

    it("sollte Mitglieder nach Standort filtern", async () => {
      const result = await getMembers(0, 10, "location-1")

      expect(result.data).toHaveLength(2)
      expect(result.data[0].location?.id).toBe("location-1")
      expect(result.data[1].location?.id).toBe("location-1")
    })

    it("sollte Pagination unterstützen", async () => {
      const result = await getMembers(0, 2)

      expect(result.data).toHaveLength(2)
      expect(result.page.size).toBe(2)
      expect(result.page.totalPages).toBe(2)
    })
  })

  describe("getMemberById", () => {
    it("sollte ein Mitglied nach ID zurückgeben", async () => {
      const result = await getMemberById("member-1")

      expect(result).toEqual({
        id: "member-1",
        firstName: "Harry",
        lastName: "Potter",
        email: "harry@example.com",
        phone: "123456789",
        location: {
          id: "location-1",
          name: "Hauptwache"
        },
        createdAt: "2026-01-01T00:00:00Z",
        updatedAt: "2026-01-01T00:00:00Z"
      })
    })
  })
})
