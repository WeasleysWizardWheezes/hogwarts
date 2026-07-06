# Fire Manager – Frontend

React SPA mit TypeScript, Vite, Tailwind CSS und shadcn/ui.

## Voraussetzungen

- Node.js 20+

## Setup

```bash
cp .env.example .env
npm install
npm run dev
```

Die Anwendung startet auf http://localhost:5173.

## Scripts

| Script             | Beschreibung                              |
| ------------------ | ----------------------------------------- |
| `npm run dev`      | Dev-Server starten                        |
| `npm run build`    | Production-Build                          |
| `npm run preview`  | Production-Build lokal testen             |
| `npm run lint`     | Linting (oxlint)                          |
| `npm run api:generate` | OpenAPI-Schema holen + Types generieren |

## Projektstruktur

```
src/
├── app/             App-Shell (main.tsx, App.tsx, Routing)
├── features/        Feature-Module
│   └── <feature>/
│       ├── api/         API-Calls
│       ├── components/  Feature-spezifische Komponenten
│       ├── hooks/       Feature-spezifische Hooks
│       ├── pages/       Seiten / Views
│       └── types.ts     Feature-Types
└── shared/          Geteilter Code (feature-unabhängig)
    ├── api/             OpenAPI Client + generierte Types
    ├── components/ui/   shadcn/ui Komponenten
    ├── hooks/           Shared Hooks
    └── lib/             Utilities (cn, etc.)
```

## API-Anbindung

Der API-Client wird aus dem OpenAPI-Schema des Backends generiert:

```bash
# Backend muss laufen
npm run api:generate
```

Verwendung in Features:

```ts
import { useQuery } from "@tanstack/react-query"
import { api } from "@/shared/api"

export function useEquipment() {
  return useQuery({
    queryKey: ["equipment"],
    queryFn: async () => {
      const { data, error } = await api.GET("/api/equipment")
      if (error) throw error
      return data
    },
  })
}
```

## Umgebungsvariablen

Siehe `.env.example` für alle verfügbaren Variablen.

| Variable       | Beschreibung      | Default                  |
| -------------- | ----------------- | ------------------------ |
| `VITE_API_URL` | Backend-URL       | `http://localhost:8080`  |

## UI-Komponenten

shadcn/ui Komponenten hinzufügen:

```bash
npx shadcn@latest add <component>
```

Komponenten werden in `src/shared/components/ui/` abgelegt.
