# Fire Manager – Frontend

React SPA mit TypeScript, Vite, Tailwind CSS und shadcn/ui.

## Voraussetzungen

- Node.js 20+
- Backend läuft auf `http://localhost:8080` (für lokale Entwicklung)

## Setup

```bash
npm ci
npm run api:generate
npm run dev
```

Die Anwendung startet auf http://localhost:5173.

**CORS:** Der Vite Dev-Server proxied alle `/api/*` Requests automatisch an `http://localhost:8080`. Keine `.env`-Datei nötig für lokale Entwicklung.

## Scripts

| Script             | Beschreibung                              |
| ------------------ | ----------------------------------------- |
| `npm run dev`      | Dev-Server starten                        |
| `npm run build`    | Production-Build                          |
| `npm run preview`  | Production-Build lokal testen             |
| `npm run lint`     | Linting (oxlint)                          |
| `npm run api:generate` | API-Types aus OpenAPI-Spec generieren |

## Projektstruktur

```
src/
├── app/             App-Shell (main.tsx, router.tsx, providers.tsx)
├── features/        Feature-Module
│   └── <feature>/
│       ├── api/         API-Funktionen + TanStack Query Hooks + Query Keys
│       ├── components/  Feature-spezifische Komponenten
│       ├── pages/       Seiten (Default Export, Lazy Loading)
│       └── index.ts     Barrel Export
└── shared/          Geteilter Code (feature-unabhängig)
    ├── api/             OpenAPI Client + generierte Types (schema.d.ts)
    ├── components/ui/   shadcn/ui Komponenten
    ├── hooks/           Shared Hooks
    └── lib/             Utilities (cn, etc.)
```

## API-Anbindung

Der API-Client wird aus dem OpenAPI-Schema generiert:

```bash
npm run api:generate
```

Das generiert `src/shared/api/schema.d.ts` aus `../openapi/api.yaml`.

Verwendung in Features:

```ts
import { api } from "@/shared/api"
import type { components } from "@/shared/api"

type Location = components["schemas"]["LocationResponse"]

// GET mit Query-Params
const { data, error } = await api.GET("/api/v1/locations", {
  params: { query: { page: 0, size: 20 } }
})

// POST
const { data, error } = await api.POST("/api/v1/locations", {
  body: { name: "Hauptwache", type: "FIRE_STATION" }
})
```

## Dev-Server Proxy

In `vite.config.ts` ist ein Proxy konfiguriert der `/api/*` an das Backend weiterleitet:

```ts
server: {
  proxy: {
    "/api": {
      target: "http://localhost:8080",
      changeOrigin: true,
    },
  },
},
```

- **Entwicklung:** Vite Proxy leitet an localhost:8080 (kein CORS)
- **Production-Build:** Proxy wird ignoriert, Nginx/Reverse Proxy übernimmt

## Umgebungsvariablen

| Variable       | Beschreibung      | Default | Hinweis |
| -------------- | ----------------- | ------- | ------- |
| `VITE_API_URL` | Backend-URL       | `""`    | Leer = Vite Proxy nutzen (Dev), oder Backend-URL für Produktion |

## UI-Komponenten

shadcn/ui Komponenten hinzufügen:

```bash
npx shadcn@latest add <component>
```

Komponenten werden in `src/shared/components/ui/` abgelegt.
