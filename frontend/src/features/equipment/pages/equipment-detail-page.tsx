import type { ReactNode } from "react"
import { Link, useParams } from "react-router"
import { ArrowLeft, Pencil } from "lucide-react"
import { Button } from "@/shared/components/ui/button"
import { Badge } from "@/shared/components/ui/badge"
import { Card, CardContent, CardHeader, CardTitle } from "@/shared/components/ui/card"
import { useEquipment, useEquipmentHistory } from "../api/equipment-api"

const labels: Record<string, string> = { VERFUEGBAR: "Verfügbar", IN_GEBRAUCH: "In Gebrauch", DEFEKT: "Defekt", WARTUNG: "Wartung", ARCHIVIERT: "Archiviert" }
const variants: Record<string, "default" | "secondary" | "outline" | "destructive"> = { VERFUEGBAR: "default", IN_GEBRAUCH: "secondary", WARTUNG: "outline", DEFEKT: "destructive", ARCHIVIERT: "secondary" }

export default function EquipmentDetailPage() {
  const { id } = useParams()
  const equipmentQuery = useEquipment(id)
  const historyQuery = useEquipmentHistory(id)
  if (equipmentQuery.isLoading) return <p className="text-muted-foreground">Gerät wird geladen...</p>
  if (equipmentQuery.isError || !equipmentQuery.data) return <p className="text-destructive">Das Gerät konnte nicht geladen werden.</p>
  const equipment = equipmentQuery.data
  return <div className="flex flex-col gap-5"><div className="flex flex-wrap items-center justify-between gap-3"><div className="flex items-center gap-3"><Button variant="ghost" size="icon" render={<Link to="/equipment" />} aria-label="Zurück zur Geräteliste"><ArrowLeft className="size-4" /></Button><div><h1 className="text-2xl font-semibold">{equipment.name}</h1><p className="text-sm text-muted-foreground">{equipment.inventoryNumber}</p></div></div><Button variant="outline" render={<Link to="/equipment" />}><Pencil className="size-4" />In Liste bearbeiten</Button></div><div className="grid gap-4 lg:grid-cols-3"><Card className="lg:col-span-2"><CardHeader><CardTitle>Gerätedaten</CardTitle></CardHeader><CardContent className="grid gap-5 sm:grid-cols-2"><Detail label="Status"><Badge variant={variants[equipment.status ?? ""] ?? "secondary"}>{labels[equipment.status ?? ""] ?? equipment.status}</Badge></Detail><Detail label="Kategorie">{equipment.categoryName ?? "–"}</Detail><Detail label="Fahrzeug">{equipment.vehicleName ?? "Depot"}</Detail><Detail label="Nächste Prüfung">{equipment.nextInspectionDate ?? "Nicht geplant"}</Detail><Detail label="Nächste Wartung">{equipment.nextMaintenanceDate ?? "Nicht geplant"}</Detail><Detail label="Beschreibung">{equipment.description ?? "Keine Beschreibung"}</Detail></CardContent></Card><Card><CardHeader><CardTitle>Statusverlauf</CardTitle></CardHeader><CardContent>{historyQuery.isLoading ? <p className="text-sm text-muted-foreground">Verlauf wird geladen...</p> : historyQuery.isError ? <p className="text-sm text-destructive">Verlauf konnte nicht geladen werden.</p> : (historyQuery.data ?? []).length === 0 ? <p className="text-sm text-muted-foreground">Noch keine Statusänderungen.</p> : <ol className="grid gap-4">{(historyQuery.data ?? []).map((entry) => <li key={entry.id} className="border-l-2 pl-3 text-sm"><p className="font-medium">{labels[entry.newStatus ?? ""] ?? entry.newStatus}</p><p className="text-muted-foreground">{entry.previousStatus ? `${labels[entry.previousStatus] ?? entry.previousStatus} → ` : "Erstellt → "}{labels[entry.newStatus ?? ""] ?? entry.newStatus}</p><time className="text-xs text-muted-foreground">{entry.changedAt ? new Date(entry.changedAt).toLocaleString("de-DE") : ""}</time></li>)}</ol>}</CardContent></Card></div></div>
}
function Detail({ label, children }: { label: string; children: ReactNode }) { return <div><dt className="text-xs font-medium uppercase tracking-wide text-muted-foreground">{label}</dt><dd className="mt-1 text-sm">{children}</dd></div> }