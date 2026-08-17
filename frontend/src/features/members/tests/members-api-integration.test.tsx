import { renderWithProviders, server } from "@/test";
import { http, HttpResponse } from "msw";
import { screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import MembersPage from "@/features/members/pages/members-page";

const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:3000";
const membersUrl = `${BASE_URL}/api/v1/members`;
const locationsUrl = `${BASE_URL}/api/v1/locations`;

const mockLocations = [
  {
    id: "loc-hauptwache-koeln",
    name: "Hauptwache Köln",
    address: "Stolkgasse 33, 50667 Köln",
    type: "FIRE_STATION",
  },
  {
    id: "loc-depot-ehrenfeld",
    name: "Gerätedepot Ehrenfeld",
    address: "Venloer Straße 200, 50823 Köln",
    type: "EQUIPMENT_DEPOT",
  },
];

const mockMembers = [
  {
    id: "mem-karl-brandmeister",
    firstName: "Karl",
    lastName: "Brandmeister",
    rank: "Hauptbrandmeister",
    unit: "Einsatzabteilung 1",
    locations: [
      {
        id: "loc-hauptwache-koeln",
        name: "Hauptwache Köln",
        address: "Stolkgasse 33, 50667 Köln",
        type: "FIRE_STATION",
      },
    ],
  },
  {
    id: "mem-lisa-weber",
    firstName: "Lisa",
    lastName: "Weber",
    rank: "Oberbrandmeisterin",
    unit: "Einsatzabteilung 2",
    locations: [],
  },
];

function setupDefaultHandlers() {
  server.use(
    http.get(locationsUrl, () => {
      return HttpResponse.json({ data: mockLocations, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
    }),
    http.get(membersUrl, () => {
      return HttpResponse.json({ data: mockMembers, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
    })
  );
}

describe("MembersPage – API-Integration", () => {
  describe("Ladezustand", () => {
    it("zeigt Ladehinweis während die API antwortet", async () => {
      server.use(
        http.get(locationsUrl, async () => {
          await new Promise((resolve) => setTimeout(resolve, 100));
          return HttpResponse.json({ data: mockLocations, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        }),
        http.get(membersUrl, async () => {
          await new Promise((resolve) => setTimeout(resolve, 100));
          return HttpResponse.json({ data: mockMembers, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        })
      );

      renderWithProviders(<MembersPage />);

      expect(screen.getByText("Laden...")).toBeInTheDocument();
    });
  });

  describe("Erfolgszustand", () => {
    beforeEach(() => {
      setupDefaultHandlers();
    });

    it("zeigt Mitglieder in der Tabelle nach erfolgreicher API-Antwort", async () => {
      renderWithProviders(<MembersPage />);

      expect(await screen.findByText("Karl Brandmeister")).toBeInTheDocument();
      expect(screen.getByText("Hauptbrandmeister")).toBeInTheDocument();
      expect(screen.getByText("Einsatzabteilung 1")).toBeInTheDocument();

      expect(screen.getByText("Lisa Weber")).toBeInTheDocument();
      expect(screen.getByText("Oberbrandmeisterin")).toBeInTheDocument();
      expect(screen.getByText("Einsatzabteilung 2")).toBeInTheDocument();
    });

    it("zeigt zugewiesene Standorte als Badges am Mitglied", async () => {
      renderWithProviders(<MembersPage />);

      await screen.findByText("Karl Brandmeister");

      const karlRow = screen.getByRole("row", { name: /Karl Brandmeister/ });
      expect(within(karlRow).getByText("Hauptwache Köln")).toBeInTheDocument();

      const lisaRow = screen.getByRole("row", { name: /Lisa Weber/ });
      expect(within(lisaRow).getByText("Kein Standort")).toBeInTheDocument();
    });
  });

  describe("Leerzustand", () => {
    beforeEach(() => {
      server.use(
        http.get(locationsUrl, () => {
          return HttpResponse.json({ data: mockLocations, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        }),
        http.get(membersUrl, () => {
          return HttpResponse.json({ data: [], page: { page: 0, size: 20, totalElements: 0, totalPages: 0 } });
        })
      );
    });

    it("zeigt Hinweis wenn keine Mitglieder vorhanden sind", async () => {
      renderWithProviders(<MembersPage />);

      expect(await screen.findByText("Keine Mitglieder vorhanden.")).toBeInTheDocument();
    });
  });

  describe("Fehlerzustand", () => {
    beforeEach(() => {
      server.use(
        http.get(locationsUrl, () => {
          return HttpResponse.json({ data: mockLocations, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        }),
        http.get(membersUrl, () => {
          return HttpResponse.json(
            { error: "INTERNAL_ERROR", message: "Serverfehler" },
            { status: 500 }
          );
        })
      );
    });

    it("zeigt Fehlermeldung bei API-Fehler", async () => {
      renderWithProviders(<MembersPage />);

      expect(await screen.findByText("Fehler beim Laden.")).toBeInTheDocument();
    });
  });

  describe("Standort-Filter", () => {
    it("sendet locationId als Query-Parameter an die API", async () => {
      let capturedLocationId: string | null = null;

      server.use(
        http.get(locationsUrl, () => {
          return HttpResponse.json({ data: mockLocations, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        }),
        http.get(membersUrl, ({ request }) => {
          const url = new URL(request.url);
          capturedLocationId = url.searchParams.get("locationId");
          return HttpResponse.json({ data: mockMembers, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        })
      );

      const user = userEvent.setup();
      renderWithProviders(<MembersPage />);

      // Warten bis geladen
      await screen.findByText("Karl Brandmeister");

      // Standort-Filter öffnen und Standort auswählen
      const filterCombobox = screen.getAllByRole("combobox")[0];
      await user.click(filterCombobox);
      await user.click(await screen.findByRole("option", { name: "Hauptwache Köln" }));

      // Prüfen dass der API-Call mit locationId gemacht wurde
      await waitFor(() => {
        expect(capturedLocationId).toBe("loc-hauptwache-koeln");
      });
    });
  });

  describe("Mutation: Standort zuweisen", () => {
    let assignRequestBody: unknown = null;
    let assignRequestMemberId: string | null = null;

    beforeEach(() => {
      server.use(
        http.get(locationsUrl, () => {
          return HttpResponse.json({ data: mockLocations, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        }),
        http.get(membersUrl, () => {
          return HttpResponse.json({ data: mockMembers, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        }),
        http.post(`${membersUrl}/:memberId/locations`, async ({ request, params }) => {
          assignRequestBody = await request.json();
          assignRequestMemberId = params.memberId as string;
          return HttpResponse.json({
            ...mockMembers[1],
            locations: [mockLocations[1]],
          });
        })
      );
    });

    it("weist einem Mitglied einen Standort zu", async () => {
      const user = userEvent.setup();
      renderWithProviders(<MembersPage />);

      // Warten bis geladen
      await screen.findByText("Karl Brandmeister");

      // Zuweisen-Button bei Lisa Weber klicken
      const lisaRow = screen.getByRole("row", { name: /Lisa Weber/ });
      await user.click(within(lisaRow).getByRole("button", { name: "Standort zuweisen" }));

      // Dialog prüfen
      expect(await screen.findByText("Standort zuweisen")).toBeInTheDocument();

      // Standort im Dialog auswählen
      const dialogCombobox = screen.getByRole("combobox", { name: "Standort" });
      await user.click(dialogCombobox);
      await user.click(await screen.findByRole("option", { name: "Gerätedepot Ehrenfeld" }));

      // Zuweisen klicken
      await user.click(screen.getByRole("button", { name: "Zuweisen" }));

      // Request prüfen
      await waitFor(() => {
        expect(assignRequestMemberId).toBe("mem-lisa-weber");
        expect(assignRequestBody).toEqual({ locationId: "loc-depot-ehrenfeld" });
      });
    });
  });

  describe("Mutation-Fehler: Standort-Zuweisung fehlgeschlagen", () => {
    beforeEach(() => {
      server.use(
        http.get(locationsUrl, () => {
          return HttpResponse.json({ data: mockLocations, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        }),
        http.get(membersUrl, () => {
          return HttpResponse.json({ data: mockMembers, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        }),
        http.post(`${membersUrl}/:memberId/locations`, () => {
          return HttpResponse.json(
            { error: "NOT_FOUND", message: "Mitglied nicht gefunden" },
            { status: 404 }
          );
        })
      );
    });

    it("zeigt Fehlermeldung bei fehlgeschlagener Zuweisung", async () => {
      const user = userEvent.setup();
      renderWithProviders(<MembersPage />);

      await screen.findByText("Karl Brandmeister");

      // Zuweisen-Button klicken
      const lisaRow = screen.getByRole("row", { name: /Lisa Weber/ });
      await user.click(within(lisaRow).getByRole("button", { name: "Standort zuweisen" }));

      // Standort auswählen
      const dialogCombobox = screen.getByRole("combobox", { name: "Standort" });
      await user.click(dialogCombobox);
      await user.click(await screen.findByRole("option", { name: "Hauptwache Köln" }));

      // Zuweisen
      await user.click(screen.getByRole("button", { name: "Zuweisen" }));

      // Fehlermeldung (Toast)
      await waitFor(() => {
        expect(screen.getByText("Fehler beim Zuweisen")).toBeInTheDocument();
      });
    });
  });
});
