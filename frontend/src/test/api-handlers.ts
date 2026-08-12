import { http, HttpResponse } from "msw"
import type { SetupServer } from "msw/node"

export const handlers = [
  // Standort-API-Handler
  http.get("/api/v1/locations", ({ request }) => {
    const url = new URL(request.url)
    const page = parseInt(url.searchParams.get("page") || "0")
    const size = parseInt(url.searchParams.get("size") || "10")

    const mockLocations = [
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
    ]

    const start = page * size
    const end = start + size
    const data = mockLocations.slice(start, end)

    return HttpResponse.json({
      data,
      page: {
        size,
        totalElements: mockLocations.length,
        totalPages: Math.ceil(mockLocations.length / size),
        number: page
      }
    })
  }),

  http.get("/api/v1/locations/:locationId", ({ params }) => {
    const { locationId } = params

    const mockLocation = {
      id: locationId,
      name: "Hauptwache",
      address: "Hauptstraße 1",
      type: "FIRE_STATION",
      createdAt: "2026-01-01T00:00:00Z",
      updatedAt: "2026-01-01T00:00:00Z"
    }

    return HttpResponse.json(mockLocation)
  }),

  http.post("/api/v1/locations", async ({ request }) => {
    const body = await request.json()
    
    const newLocation = {
      id: "location-new",
      name: body.name,
      address: body.address,
      type: body.type,
      createdAt: "2026-01-01T00:00:00Z",
      updatedAt: "2026-01-01T00:00:00Z"
    }

    return HttpResponse.json(newLocation, { status: 201 })
  }),

  http.put("/api/v1/locations/:locationId", async ({ params, request }) => {
    const { locationId } = params
    const body = await request.json()

    const updatedLocation = {
      id: locationId,
      name: body.name,
      address: body.address,
      type: body.type,
      createdAt: "2026-01-01T00:00:00Z",
      updatedAt: "2026-01-01T00:00:00Z"
    }

    return HttpResponse.json(updatedLocation)
  }),

  http.delete("/api/v1/locations/:locationId", ({ params }) => {
    const { locationId: _locationId } = params
    return HttpResponse.json({}, { status: 204 })
  }),

  // Mitglieder-API-Handler
  http.get("/api/v1/members", ({ request }) => {
    const url = new URL(request.url)
    const page = parseInt(url.searchParams.get("page") || "0")
    const size = parseInt(url.searchParams.get("size") || "10")
    const locationId = url.searchParams.get("locationId")

    const mockMembers = [
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
    ]

    let filteredMembers = mockMembers
    if (locationId) {
      filteredMembers = mockMembers.filter(member => 
        member.location && member.location.id === locationId
      )
    }

    const start = page * size
    const end = start + size
    const data = filteredMembers.slice(start, end)

    return HttpResponse.json({
      data,
      page: {
        size,
        totalElements: filteredMembers.length,
        totalPages: Math.ceil(filteredMembers.length / size),
        number: page
      }
    })
  }),

  http.get("/api/v1/members/:memberId", ({ params }) => {
    const { memberId } = params

    const mockMember = {
      id: memberId,
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
    }

    return HttpResponse.json(mockMember)
  }),

  http.post("/api/v1/members", async ({ request }) => {
    const body = await request.json()
    
    const newMember = {
      id: "member-new",
      firstName: body.firstName,
      lastName: body.lastName,
      email: body.email,
      phone: body.phone,
      location: null,
      createdAt: "2026-01-01T00:00:00Z",
      updatedAt: "2026-01-01T00:00:00Z"
    }

    return HttpResponse.json(newMember, { status: 201 })
  }),

  http.put("/api/v1/members/:memberId", async ({ params, request }) => {
    const { memberId } = params
    const body = await request.json()

    const updatedMember = {
      id: memberId,
      firstName: body.firstName,
      lastName: body.lastName,
      email: body.email,
      phone: body.phone,
      location: body.locationId ? {
        id: body.locationId,
        name: "Hauptwache"
      } : null,
      createdAt: "2026-01-01T00:00:00Z",
      updatedAt: "2026-01-01T00:00:00Z"
    }

    return HttpResponse.json(updatedMember)
  }),

  http.post("/api/v1/members/:memberId/locations", async ({ params, request }) => {
    const { memberId } = params
    const body = await request.json()

    return HttpResponse.json({
      memberId,
      locationId: body.locationId
    })
  }),

  // Fehler-Handler
  http.get("/api/v1/locations/error", () => {
    return HttpResponse.json(
      { message: "Fehler beim Laden der Standorte" },
      { status: 500 }
    )
  }),

  http.get("/api/v1/members/error", () => {
    return HttpResponse.json(
      { message: "Fehler beim Laden der Mitglieder" },
      { status: 500 }
    )
  })
]

export function setupTestServer(server: SetupServer) {
  server.use(...handlers)
}
