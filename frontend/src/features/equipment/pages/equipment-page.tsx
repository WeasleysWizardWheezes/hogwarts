import { useState } from "react"
import { Link } from "react-router"
import { Plus, Pencil, Archive, Search, Eye } from "lucide-react"
import { Button } from "@/shared/components/ui/button"
import { Badge } from "@/shared/components/ui/badge"
import { Input } from "@/shared/components/ui/input"
import { Label } from "@/shared/components/ui/label"
import { Textarea } from "@/shared/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/shared/components/ui/select"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogClose } from "@/shared/components/ui/dialog"
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/shared/components/ui/alert-dialog"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/shared/components/ui/table"
import { Pagination, PaginationContent, PaginationItem, PaginationNext, PaginationPrevious } from "@/shared/components/ui/pagination"
import { useVehicles } from "@/features/vehicles"
import { useEquipmentCategories } from "../api/equipment-categories-api"
import { useCreateEquipment, useDeleteEquipment, useEquipmentList, useUpdateEquipment } from "../api/equipment-api"
import type { EquipmentResponse, EquipmentListStatus } from "../api/equipment-api"
import type { CreateEquipmentRequest } from "../api/equipment-api"

const STATUS_LABELS: Record<string, string> = {
  VERFUEGBAR: "Verfügbar", IN_GEBRAUCH: "In Gebrauch", DEFEKT: "Defekt", WARTUNG: "Wartung",
}
const STATUS_VARIANTS: Record<string, "default" | "secondary" | "outline" | "destructive"> = {
  VERFUEGBAR: "default", IN_GEBRAUCH: "secondary", WARTUNG: "outline", DEFEKT: "destructive",
}
const STATUSES: EquipmentListStatus[] = ["VERFUEGBAR", "IN_GEBRAUCH", "DEFEKT", "WARTUNG"]

type FormState = {
  name: string; inventoryNumber: string; description: string; status: EquipmentListStatus
  categoryId: string; vehicleId: string; nextInspectionDate: string; nextMaintenanceDate: string
}
const EMPTY_FORM: FormState = {
  name: "", inventoryNumber: "", description: "", status: "VERFUEGBAR", categoryId: "", vehicleId: "",
  nextInspectionDate: "", nextMaintenanceDate: "",
}

export default function EquipmentPage() {
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState("")
  const [categoryId, setCategoryId] = useState("")
  const [vehicleId, setVehicleId] = useState("")
  const [status, setStatus] = useState("")
  const [dueBefore, setDueBefore] = useState("")
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<EquipmentResponse | null>(null)
  const [archiving, setArchiving] = useState<EquipmentResponse | null>(null)
  const [form, setForm] = useState<FormState>(EMPTY_FORM)
  const filters = { page, size: 20, search: search || undefined, categoryId: categoryId || undefined, vehicleId: vehicleId || undefined, status: (status || undefined) as EquipmentListStatus | undefined, dueBefore: dueBefore || undefined }
  const equipmentQuery = useEquipmentList(filters)
  const categoriesQuery = useEquipmentCategories()
  const vehiclesQuery = useVehicles()
  const createMutation = useCreateEquipment()
  const updateMutation = useUpdateEquipment()
  const deleteMutation = useDeleteEquipment()
  const items = equipmentQuery.data?.data ?? []
  const totalPages = equipmentQuery.data?.page?.totalPages ?? 0

  function updateForm(key: keyof FormState, value: string) { setForm((previous) => ({ ...previous, [key]: value })) }
  function openCreate() { setEditing(null); setForm(EMPTY_FORM); setDialogOpen(true) }
  function openEdit(item: EquipmentResponse) {
    setEditing(item)
    setForm({ name: item.name ?? "", inventoryNumber: item.inventoryNumber ?? "", description: item.description ?? "", status: (item.status === "ARCHIVIERT" ? "VERFUEGBAR" : item.status) ?? "VERFUEGBAR", categoryId: item.categoryId ?? "", vehicleId: item.vehicleId ?? "", nextInspectionDate: item.nextInspectionDate ?? "", nextMaintenanceDate: item.nextMaintenanceDate ?? "" })
    setDialogOpen(true)
  }
  async function submit() {
    if (!form.name.trim() || !form.inventoryNumber.trim() || !form.categoryId) return
    const body = { name: form.name, inventoryNumber: form.inventoryNumber, description: form.description || undefined, status: form.status, categoryId: form.categoryId, vehicleId: form.vehicleId || null, nextInspectionDate: form.nextInspectionDate || null, nextMaintenanceDate: form.nextMaintenanceDate || null }
    if (editing?.id) await updateMutation.mutateAsync({ equipmentId: editing.id, body })
    else await createMutation.mutateAsync(body as CreateEquipmentRequest)
    setDialogOpen(false)
  }
  async function archive() { if (archiving?.id) await deleteMutation.mutateAsync(archiving.id); setArchiving(null) }

  if (equipmentQuery.isLoading) return <PageMessage title="Geräte" message="Geräte werden geladen..." />
  if (equipmentQuery.isError) return <PageMessage title="Geräte" message="Die Geräteliste konnte nicht geladen werden." error />
  return (
    <div className="flex flex-col gap-5">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div><h1 className="text-2xl font-semibold tracking-tight">Geräte</h1><p className="text-sm text-muted-foreground">Ausrüstung, Zuordnung und Prüffristen verwalten.</p></div>
        <Button onClick={openCreate}><Plus className="size-4" />Gerät hinzufügen</Button>
      </div>
      <div className="grid gap-3 rounded-lg border bg-muted/20 p-4 md:grid-cols-2 xl:grid-cols-5">
        <div className="relative xl:col-span-2"><Search className="pointer-events-none absolute left-3 top-2.5 size-4 text-muted-foreground" /><Input className="pl-9" aria-label="Geräte suchen" placeholder="Name oder Inventarnummer" value={search} onChange={(event) => { setSearch(event.target.value); setPage(0) }} /></div>
        <FilterSelect label="Kategorie" value={categoryId} placeholder="Alle Kategorien" onChange={setCategoryId} items={(categoriesQuery.data?.data ?? []).map((item) => ({ value: item.id!, label: item.name! }))} />
        <FilterSelect label="Fahrzeug" value={vehicleId} placeholder="Alle Fahrzeuge" onChange={setVehicleId} items={(vehiclesQuery.data?.data ?? []).map((item) => ({ value: item.id!, label: item.name! }))} />
        <FilterSelect label="Status" value={status} placeholder="Alle Status" onChange={setStatus} items={STATUSES.map((item) => ({ value: item, label: STATUS_LABELS[item] }))} />
        <div className="grid gap-1.5"><Label htmlFor="dueBefore">Fällig bis</Label><Input id="dueBefore" type="date" value={dueBefore} onChange={(event) => setDueBefore(event.target.value)} /></div>
      </div>
      {items.length === 0 ? <div className="rounded-lg border border-dashed p-10 text-center text-sm text-muted-foreground">Keine Geräte für die gewählten Filter gefunden.</div> : <Table><TableHeader><TableRow><TableHead>Gerät</TableHead><TableHead>Inventarnummer</TableHead><TableHead>Kategorie</TableHead><TableHead>Fahrzeug</TableHead><TableHead>Status</TableHead><TableHead>Nächste Prüfung</TableHead><TableHead className="w-28">Aktionen</TableHead></TableRow></TableHeader><TableBody>{items.map((item) => <TableRow key={item.id}><TableCell><Link className="font-medium hover:underline" to={`/equipment/${item.id}`}>{item.name}</Link></TableCell><TableCell>{item.inventoryNumber}</TableCell><TableCell>{item.categoryName ?? "–"}</TableCell><TableCell>{item.vehicleName ?? "Depot"}</TableCell><TableCell><Badge variant={STATUS_VARIANTS[item.status ?? ""] ?? "secondary"}>{STATUS_LABELS[item.status ?? ""] ?? item.status}</Badge></TableCell><TableCell className={item.nextInspectionDate && dueBefore && item.nextInspectionDate <= dueBefore ? "font-medium text-destructive" : ""}>{item.nextInspectionDate ?? "–"}</TableCell><TableCell><div className="flex items-center gap-1"><Button variant="ghost" size="icon-sm" render={<Link to={`/equipment/${item.id}`} />} aria-label="Details"><Eye className="size-4" /></Button><Button variant="ghost" size="icon-sm" onClick={() => openEdit(item)} aria-label="Bearbeiten"><Pencil className="size-4" /></Button><Button variant="ghost" size="icon-sm" onClick={() => setArchiving(item)} aria-label="Archivieren"><Archive className="size-4" /></Button></div></TableCell></TableRow>)}</TableBody></Table>}
      {totalPages > 1 && <Pagination><PaginationContent><PaginationItem><PaginationPrevious text="Zurück" href="#" aria-disabled={page === 0} onClick={(event) => { event.preventDefault(); if (page > 0) setPage(page - 1) }} /></PaginationItem><PaginationItem><span className="px-3 text-sm text-muted-foreground">Seite {page + 1} von {totalPages}</span></PaginationItem><PaginationItem><PaginationNext text="Weiter" href="#" aria-disabled={page + 1 >= totalPages} onClick={(event) => { event.preventDefault(); if (page + 1 < totalPages) setPage(page + 1) }} /></PaginationItem></PaginationContent></Pagination>}
      <EquipmentDialog open={dialogOpen} onOpenChange={setDialogOpen} editing={Boolean(editing)} form={form} updateForm={updateForm} submit={submit} pending={createMutation.isPending || updateMutation.isPending} categories={categoriesQuery.data?.data ?? []} vehicles={vehiclesQuery.data?.data ?? []} />
      <AlertDialog open={Boolean(archiving)} onOpenChange={(open) => { if (!open) setArchiving(null) }}><AlertDialogContent><AlertDialogHeader><AlertDialogTitle>Gerät archivieren?</AlertDialogTitle><AlertDialogDescription>„{archiving?.name}“ wird aus der aktiven Geräteliste entfernt.</AlertDialogDescription></AlertDialogHeader><AlertDialogFooter><AlertDialogCancel>Abbrechen</AlertDialogCancel><AlertDialogAction variant="destructive" onClick={archive} disabled={deleteMutation.isPending}>Archivieren</AlertDialogAction></AlertDialogFooter></AlertDialogContent></AlertDialog>
    </div>
  )
}

function PageMessage({ title, message, error = false }: { title: string; message: string; error?: boolean }) { return <div className="flex flex-col gap-2"><h1 className="text-2xl font-semibold">{title}</h1><p className={error ? "text-destructive" : "text-muted-foreground"}>{message}</p></div> }
function FilterSelect({ label, value, placeholder, onChange, items }: { label: string; value: string; placeholder: string; onChange: (value: string) => void; items: { value: string; label: string }[] }) { return <div className="grid gap-1.5"><Label>{label}</Label><Select value={value || "all"} onValueChange={(next) => onChange(next === "all" ? "" : next ?? "")}><SelectTrigger aria-label={label}><SelectValue placeholder={placeholder} /></SelectTrigger><SelectContent><SelectItem value="all">{placeholder}</SelectItem>{items.map((item) => <SelectItem key={item.value} value={item.value}>{item.label}</SelectItem>)}</SelectContent></Select></div> }
function EquipmentDialog({ open, onOpenChange, editing, form, updateForm, submit, pending, categories, vehicles }: { open: boolean; onOpenChange: (open: boolean) => void; editing: boolean; form: FormState; updateForm: (key: keyof FormState, value: string) => void; submit: () => void; pending: boolean; categories: { id?: string; name?: string }[]; vehicles: { id?: string; name?: string }[] }) { return <Dialog open={open} onOpenChange={onOpenChange}><DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-2xl"><DialogHeader><DialogTitle>{editing ? "Gerät bearbeiten" : "Gerät hinzufügen"}</DialogTitle></DialogHeader><div className="grid gap-5 py-3"><div className="grid gap-3 md:grid-cols-2"><div className="grid gap-1.5"><Label htmlFor="equipment-name">Name *</Label><Input id="equipment-name" value={form.name} onChange={(event) => updateForm("name", event.target.value)} /></div><div className="grid gap-1.5"><Label htmlFor="equipment-inventory">Inventarnummer *</Label><Input id="equipment-inventory" value={form.inventoryNumber} onChange={(event) => updateForm("inventoryNumber", event.target.value)} /></div></div><div className="grid gap-3 md:grid-cols-2"><FilterSelect label="Kategorie *" value={form.categoryId} placeholder="Kategorie wählen" onChange={(value) => updateForm("categoryId", value === "all" ? "" : value)} items={categories.map((item) => ({ value: item.id!, label: item.name! }))} /><FilterSelect label="Fahrzeug" value={form.vehicleId} placeholder="Depot / kein Fahrzeug" onChange={(value) => updateForm("vehicleId", value === "all" ? "" : value)} items={vehicles.map((item) => ({ value: item.id!, label: item.name! }))} /></div><div className="grid gap-3 md:grid-cols-3"><FilterSelect label="Status" value={form.status} placeholder="Status" onChange={(value) => updateForm("status", value as EquipmentListStatus)} items={STATUSES.map((item) => ({ value: item, label: STATUS_LABELS[item] }))} /><div className="grid gap-1.5"><Label htmlFor="inspection-date">Nächste Prüfung</Label><Input id="inspection-date" type="date" value={form.nextInspectionDate} onChange={(event) => updateForm("nextInspectionDate", event.target.value)} /></div><div className="grid gap-1.5"><Label htmlFor="maintenance-date">Nächste Wartung</Label><Input id="maintenance-date" type="date" value={form.nextMaintenanceDate} onChange={(event) => updateForm("nextMaintenanceDate", event.target.value)} /></div></div><div className="grid gap-1.5"><Label htmlFor="equipment-description">Beschreibung</Label><Textarea id="equipment-description" value={form.description} onChange={(event) => updateForm("description", event.target.value)} /></div></div><DialogFooter><DialogClose render={<Button variant="outline" />}>Abbrechen</DialogClose><Button onClick={submit} disabled={pending || !form.name.trim() || !form.inventoryNumber.trim() || !form.categoryId}>{pending ? "Speichern..." : editing ? "Speichern" : "Anlegen"}</Button></DialogFooter></DialogContent></Dialog> }