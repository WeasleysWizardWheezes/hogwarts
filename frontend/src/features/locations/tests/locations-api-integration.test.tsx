import { renderWithProviders, server } from "@/test";
import { http, HttpResponse } from "msw";
import { screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import LocationsPage from "@/features/locations/pages/locations-page";

const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:3000";
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

describe("LocationsPage – API-Integration", () => {
  describe("Ladezustand", () => {
    it("zeigt Ladehinweis während die API antwortet", async () => {
      server.use(
        http.get(locationsUrl, async () => {
          await new Promise((resolve) => setTimeout(resolve, 100));
          return HttpResponse.json({ data: mockLocations, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        })
      );

      renderWithProviders(<LocationsPage />);

      expect(screen.getByText("Laden...")).toBeInTheDocument();
    });
  });

  describe("Erfolgszustand", () => {
    beforeEach(() => {
      server.use(
        http.get(locationsUrl, () => {
          return HttpResponse.json({ data: mockLocations, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        })
      );
    });

    it("zeigt Standorte in der Tabelle nach erfolgreicher API-Antwort", async () => {
      renderWithProviders(<LocationsPage />);

      expect(await screen.findByText("Hauptwache Köln")).toBeInTheDocument();
      expect(screen.getByText("Stolkgasse 33, 50667 Köln")).toBeInTheDocument();
      expect(screen.getByText("Feuerwache")).toBeInTheDocument();

      expect(screen.getByText("Gerätedepot Ehrenfeld")).toBeInTheDocument();
      expect(screen.getByText("Venloer Straße 200, 50823 Köln")).toBeInTheDocument();
      expect(screen.getByText("Gerätedepot")).toBeInTheDocument();
    });
  });

  describe("Leerzustand", () => {
    beforeEach(() => {
      server.use(
        http.get(locationsUrl, () => {
          return HttpResponse.json({ data: [], page: { page: 0, size: 20, totalElements: 0, totalPages: 0 } });
        })
      );
    });

    it("zeigt Hinweis wenn keine Standorte vorhanden sind", async () => {
      renderWithProviders(<LocationsPage />);

      expect(await screen.findByText("Keine Einträge vorhanden.")).toBeInTheDocument();
    });
  });

  describe("Fehlerzustand", () => {
    beforeEach(() => {
      server.use(
        http.get(locationsUrl, () => {
          return HttpResponse.json(
            { error: "INTERNAL_ERROR", message: "Unerwarteter Serverfehler" },
            { status: 500 }
          );
        })
      );
    });

    it("zeigt Fehlermeldung bei API-Fehler", async () => {
      renderWithProviders(<LocationsPage />);

      expect(await screen.findByText("Fehler beim Laden.")).toBeInTheDocument();
    });
  });

  describe("Mutation: Standort erstellen", () => {
    let createRequestBody: unknown = null;

    beforeEach(() => {
      server.use(
        http.get(locationsUrl, () => {
          return HttpResponse.json({ data: mockLocations, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        }),
        http.post(locationsUrl, async ({ request }) => {
          createRequestBody = await request.json();
          return HttpResponse.json(
            {
              id: "loc-new-training",
              name: "Ausbildungszentrum Nord",
              address: "Nordstraße 10, 50733 Köln",
              type: "TRAINING_CENTER",
            },
            { status: 201 }
          );
        })
      );
    });

    it("erstellt einen neuen Standort und sendet korrekten Request", async () => {
      const user = userEvent.setup();
      renderWithProviders(<LocationsPage />);

      // Warten bis Daten geladen sind
      await screen.findByText("Hauptwache Köln");

      // Dialog öffnen
      await user.click(screen.getByRole("button", { name: /Erstellen/ }));
      expect(await screen.findByText("Standort erstellen")).toBeInTheDocument();

      // Formular ausfüllen
      await user.type(screen.getByLabelText("Name"), "Ausbildungszentrum Nord");
      await user.type(screen.getByLabelText("Adresse"), "Nordstraße 10, 50733 Köln");

      // Typ auswählen (Custom Select)
      await user.click(screen.getByRole("combobox", { name: "Typ" }));
      await user.click(await screen.findByRole("option", { name: "Ausbildungszentrum" }));

      // Absenden
      await user.click(screen.getByRole("button", { name: "Erstellen" }));

      // Prüfen dass der Request korrekt war
      await waitFor(() => {
        expect(createRequestBody).toEqual({
          name: "Ausbildungszentrum Nord",
          address: "Nordstraße 10, 50733 Köln",
          type: "TRAINING_CENTER",
        });
      });
    });
  });

  describe("Mutation: Standort bearbeiten", () => {
    let updateRequestBody: unknown = null;
    let updateRequestUrl: string | null = null;

    beforeEach(() => {
      server.use(
        http.get(locationsUrl, () => {
          return HttpResponse.json({ data: mockLocations, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        }),
        http.put(`${locationsUrl}/:locationId`, async ({ request, params }) => {
          updateRequestBody = await request.json();
          updateRequestUrl = params.locationId as string;
          return HttpResponse.json({
            id: params.locationId,
            name: "Hauptwache Köln (aktualisiert)",
            address: "Neue Straße 1, 50667 Köln",
            type: "FIRE_STATION",
          });
        })
      );
    });

    it("aktualisiert einen bestehenden Standort", async () => {
      const user = userEvent.setup();
      renderWithProviders(<LocationsPage />);

      // Warten bis Daten geladen sind
      await screen.findByText("Hauptwache Köln");

      // Bearbeiten-Button in der ersten Zeile klicken
      const rows = screen.getAllByRole("row");
      const firstDataRow = rows[1];
      const editButton = firstDataRow.querySelector('[aria-label="Bearbeiten"]') as HTMLElement;
      await user.click(editButton);

      // Dialog prüfen
      expect(await screen.findByText("Standort bearbeiten")).toBeInTheDocument();

      // Name ändern
      const nameInput = screen.getByLabelText("Name");
      await user.clear(nameInput);
      await user.type(nameInput, "Hauptwache Köln (aktualisiert)");

      // Speichern
      await user.click(screen.getByRole("button", { name: "Speichern" }));

      // Request-Prüfung
      await waitFor(() => {
        expect(updateRequestUrl).toBe("loc-hauptwache-koeln");
        expect(updateRequestBody).toMatchObject({
          name: "Hauptwache Köln (aktualisiert)",
        });
      });
    });
  });

  describe("Mutation: Standort löschen", () => {
    let deleteRequestUrl: string | null = null;

    beforeEach(() => {
      server.use(
        http.get(locationsUrl, () => {
          return HttpResponse.json({ data: mockLocations, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        }),
        http.delete(`${locationsUrl}/:locationId`, ({ params }) => {
          deleteRequestUrl = params.locationId as string;
          return new HttpResponse(null, { status: 204 });
        })
      );
    });

    it("löscht einen Standort nach Bestätigung im Dialog", async () => {
      const user = userEvent.setup();
      renderWithProviders(<LocationsPage />);

      // Warten bis Daten geladen sind
      await screen.findByText("Hauptwache Köln");

      // Löschen-Button in der ersten Zeile klicken
      const rows = screen.getAllByRole("row");
      const firstDataRow = rows[1];
      const deleteButton = firstDataRow.querySelector('[aria-label="Löschen"]') as HTMLElement;
      await user.click(deleteButton);

      // Bestätigungsdialog prüfen
      expect(await screen.findByRole("heading", { name: "Standort löschen" })).toBeInTheDocument();
      const dialog = screen.getByRole("alertdialog");
      expect(within(dialog).getByText(/Hauptwache Köln/)).toBeInTheDocument();
      expect(within(dialog).getByText(/wirklich löschen/)).toBeInTheDocument();

      // Löschen bestätigen
      await user.click(screen.getByRole("button", { name: "Löschen" }));

      // Request-Prüfung
      await waitFor(() => {
        expect(deleteRequestUrl).toBe("loc-hauptwache-koeln");
      });
    });
  });

  describe("Mutation-Fehler: Standort erstellen fehlgeschlagen", () => {
    beforeEach(() => {
      server.use(
        http.get(locationsUrl, () => {
          return HttpResponse.json({ data: mockLocations, page: { page: 0, size: 20, totalElements: 2, totalPages: 1 } });
        }),
        http.post(locationsUrl, () => {
          return HttpResponse.json(
            { error: "VALIDATION_FAILED", message: "Name darf nicht leer sein" },
            { status: 400 }
          );
        })
      );
    });

    it("zeigt Fehlermeldung bei fehlgeschlagener Erstellung", async () => {
      const user = userEvent.setup();
      renderWithProviders(<LocationsPage />);

      await screen.findByText("Hauptwache Köln");

      // Dialog öffnen und Formular mit Daten füllen
      await user.click(screen.getByRole("button", { name: /Erstellen/ }));
      await user.type(screen.getByLabelText("Name"), "Test");
      await user.click(screen.getByRole("combobox", { name: "Typ" }));
      await user.click(await screen.findByRole("option", { name: "Feuerwache" }));
      await user.click(screen.getByRole("button", { name: "Erstellen" }));

      // Toast-Fehlermeldung sichtbar
      await waitFor(() => {
        expect(screen.getByText("Fehler beim Erstellen")).toBeInTheDocument();
      });
    });
  });
});
