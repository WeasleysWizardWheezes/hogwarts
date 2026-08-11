/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_URL: string
  // weitere Umgebungsvariablen nach Bedarf
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
