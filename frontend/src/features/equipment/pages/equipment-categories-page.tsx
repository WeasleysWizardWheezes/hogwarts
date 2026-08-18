import { useState } from "react"
import { Archive, Pencil, Plus } from "lucide-react"
import { Button } from "@/shared/components/ui/button"
import { Input } from "@/shared/components/ui/input"
import { Label } from "@/shared/components/ui/label"
import { Textarea } from "@/shared/components/ui/textarea"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/shared/components/ui/table"
import { Dialog, DialogClose, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/shared/components/ui/dialog"
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/shared/components/ui/alert-dialog"
import { useCreateEquipmentCategory, useDeleteEquipmentCategory, useEquipmentCategories, useUpdateEquipmentCategory } from "../api/equipment-categories-api"
import type { EquipmentCategoryResponse } from "../api/equipment-categories-api"

export default function EquipmentCategoriesPage() {
  const [search, setSearch] = useState("")
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<EquipmentCategoryResponse | null>(null)
  const [archiving, setArchiving] = useState<EquipmentCategoryResponse | null>(null)
  const [name, setName] = useState("")
  const [description, setDescription] = useState("")
  const query = useEquipmentCategories(search)
  const createMutation = useCreateEquipmentCategory()
  const updateMutation = useUpdateEquipmentCategory()
  const deleteMutation = useDeleteEquipmentCategory()
  const categories = query.data?.data ?? []
  function openCreate() { setEditing(null); setName(""); setDescription(""); setOpen(true) }
  function openEdit(category: EquipmentCategoryResponse) { setEditing(category); setName(category.name ?? ""); setDescription(category.description ?? ""); setOpen(true) }
  async function submit() { if (!name.trim()) return; if (editing?.id) await updateMutation.mutateAsync({ categoryId: editing.id, body: { name, description: description || undefined } }); else await createMutation.mutateAsync({ name, description: description || undefined }); setOpen(false) }
  async function archive() { if (archiving?.id) await deleteMutation.mutateAsync(archiving.id); setArchiving(null) }
  if (query.isLoading) return <p className="text-muted-foreground">Kategorien werden geladen...</p>
  if (query.isError) return <p className="text-destructive">Die Kategorien konnten nicht geladen werden.</p>
  return <><div className="flex flex-col gap-5"><div className="flex flex-wrap items-start justify-between gap-3"><div><h1 className="text-2xl font-semibold">Gerätekategorien</h1><p className="text-sm text-muted-foreground">Ordne Geräte übersichtlich nach Typ und Einsatzbereich.</p></div><Button onClick={openCreate}><Plus className="size-4" />Kategorie hinzufügen</Button></div><Input className="max-w-md" aria-label="Kategorien suchen" placeholder="Kategorie suchen" value={search} onChange={(event) => setSearch(event.target.value)} />{categories.length === 0 ? <div className="rounded-lg border border-dashed p-10 text-center text-sm text-muted-foreground">Keine Kategorien gefunden.</div> : <Table><TableHeader><TableRow><TableHead>Name</TableHead><TableHead>Beschreibung</TableHead><TableHead className="w-28">Aktionen</TableHead></TableRow></TableHeader><TableBody>{categories.map((category) => <TableRow key={category.id}><TableCell className="font-medium">{category.name}</TableCell><TableCell>{category.description ?? "–"}</TableCell><TableCell><div className="flex gap-1"><Button variant="ghost" size="icon-sm" onClick={() => openEdit(category)} aria-label="Bearbeiten"><Pencil className="size-4" /></Button><Button variant="ghost" size="icon-sm" onClick={() => setArchiving(category)} aria-label="Archivieren"><Archive className="size-4" /></Button></div></TableCell></TableRow>)}</TableBody></Table>}</div>
    <Dialog open={open} onOpenChange={setOpen}><DialogContent><DialogHeader><DialogTitle>{editing ? "Kategorie bearbeiten" : "Kategorie hinzufügen"}</DialogTitle></DialogHeader><div className="grid gap-4 py-3"><div className="grid gap-1.5"><Label htmlFor="category-name">Name *</Label><Input id="category-name" value={name} onChange={(event) => setName(event.target.value)} /></div><div className="grid gap-1.5"><Label htmlFor="category-description">Beschreibung</Label><Textarea id="category-description" value={description} onChange={(event) => setDescription(event.target.value)} /></div></div><DialogFooter><DialogClose render={<Button variant="outline" />}>Abbrechen</DialogClose><Button onClick={submit} disabled={!name.trim() || createMutation.isPending || updateMutation.isPending}>Speichern</Button></DialogFooter></DialogContent></Dialog>
    <AlertDialog open={Boolean(archiving)} onOpenChange={(next) => { if (!next) setArchiving(null) }}><AlertDialogContent><AlertDialogHeader><AlertDialogTitle>Kategorie archivieren?</AlertDialogTitle><AlertDialogDescription>„{archiving?.name}“ wird nicht mehr in aktiven Auswahllisten angezeigt.</AlertDialogDescription></AlertDialogHeader><AlertDialogFooter><AlertDialogCancel>Abbrechen</AlertDialogCancel><AlertDialogAction variant="destructive" onClick={archive}>Archivieren</AlertDialogAction></AlertDialogFooter></AlertDialogContent></AlertDialog>
  </> 
}