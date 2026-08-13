import { useState } from "react"
import { Plus, Pencil, Trash2 } from "lucide-react"
import { Button } from "@/shared/components/ui/button"
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/shared/components/ui/table"
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogClose,
} from "@/shared/components/ui/dialog"
import {
  AlertDialog, AlertDialogContent, AlertDialogHeader, AlertDialogTitle,
  AlertDialogDescription, AlertDialogFooter, AlertDialogAction, AlertDialogCancel,
} from "@/shared/components/ui/alert-dialog"
import { Input } from "@/shared/components/ui/input"
import { Label } from "@/shared/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/shared/components/ui/select"
import {
  useLocations, useCreateLocation, useUpdateLocation, useDeleteLocation,
} from "../api/locations-api"
import type { LocationResponse, CreateLocationRequest } from "../api/locations-api"

interface FormState {
  name: string
  type: "FIRE_STATION" | "EQUIPMENT_DEPOT" | "TRAINING_CENTER"
  address: string
}

const EMPTY_FORM: FormState = {
  name: "",
  type: "FIRE_STATION",
  address: "",
}

const TYPE_LABELS: Record<string, string> = {
  FIRE_STATION: "Feuerwache",
  EQUIPMENT_DEPOT: "Gerätedepot",
  TRAINING_CENTER: "Ausbildungszentrum",
}

const TYPE_VARIANTS: Record<string, "default" | "secondary" | "outline"> = {
  FIRE_STATION: "default",
  EQUIPMENT_DEPOT: "secondary",
  TRAINING_CENTER: "outline",
}

export default function LocationsPage() {
  const { data, isLoading, isError } = useLocations()
  const createMutation = useCreateLocation()
  const updateMutation = useUpdateLocation()
  const deleteMutation = useDeleteLocation()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<LocationResponse | null>(null)
  const [deleting, setDeleting] = useState<LocationResponse | null>(null)
  const [formState, setFormState] = useState<FormState>(EMPTY_FORM)

  const items: LocationResponse[] = data?.data ?? []

  function openCreate() {
    setEditing(null)
    setFormState(EMPTY_FORM)
    setDialogOpen(true)
  }

  function openEdit(item: LocationResponse) {
    setEditing(item)
    setFormState({
      name: item.name ?? "",
      type: item.type ?? "FIRE_STATION",
      address: item.address ?? "",
    })
    setDialogOpen(true)
  }

  async function handleSubmit() {
    if (editing) {
      await updateMutation.mutateAsync({ locationId: editing.id!, body: formState })
    } else {
      await createMutation.mutateAsync(formState as CreateLocationRequest)
    }
    setDialogOpen(false)
  }

  async function handleDelete() {
    if (!deleting) return
    await deleteMutation.mutateAsync(deleting.id!)
    setDeleting(null)
  }

  // --- Loading / Error ---
  if (isLoading) {
    return (
      <div className="flex flex-col gap-4">
        <h1 className="text-2xl font-bold">Standorte</h1>
        <p className="text-muted-foreground">Laden...</p>
      </div>
    )
  }

  if (isError) {
    return (
      <div className="flex flex-col gap-4">
        <h1 className="text-2xl font-bold">Standorte</h1>
        <p className="text-destructive">Fehler beim Laden.</p>
      </div>
    )
  }

  // --- Render ---
  return (
    <div className="flex flex-col gap-4">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Standorte</h1>
        <Button onClick={openCreate}>
          <Plus className="size-4" />
          Erstellen
        </Button>
      </div>

      {/* Tabelle */}
      {items.length === 0 ? (
        <p className="text-muted-foreground">Keine Einträge vorhanden.</p>
      ) : (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Typ</TableHead>
              <TableHead>Adresse</TableHead>
              <TableHead className="w-[100px]">Aktionen</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {items.map((item) => (
              <TableRow key={item.id}>
                <TableCell className="font-medium">{item.name}</TableCell>
                <TableCell>
                  <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${TYPE_VARIANTS[item.type ?? ""] ?? "default"}`}>
                    {TYPE_LABELS[item.type ?? ""] ?? item.type}
                  </span>
                </TableCell>
                <TableCell>{item.address}</TableCell>
                <TableCell>
                  <div className="flex items-center gap-1">
                    <Button variant="ghost" size="icon-sm" onClick={() => openEdit(item)} aria-label="Bearbeiten">
                      <Pencil className="size-4" />
                    </Button>
                    <Button variant="ghost" size="icon-sm" onClick={() => setDeleting(item)} aria-label="Löschen">
                      <Trash2 className="size-4" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}

      {/* Create/Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editing ? "Standort bearbeiten" : "Standort erstellen"}</DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="name">Name</Label>
              <Input
                id="name"
                value={formState.name}
                onChange={(e) => setFormState((prev) => ({ ...prev, name: e.target.value }))}
                placeholder="Name"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="type">Typ</Label>
              <Select
                value={formState.type}
                onValueChange={(value) => { if (value) setFormState((prev) => ({ ...prev, type: value })) }}
              >
                <SelectTrigger id="type">
                  <SelectValue placeholder="Typ auswählen" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="FIRE_STATION">Feuerwache</SelectItem>
                  <SelectItem value="EQUIPMENT_DEPOT">Gerätedepot</SelectItem>
                  <SelectItem value="TRAINING_CENTER">Ausbildungszentrum</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="grid gap-2">
              <Label htmlFor="address">Adresse</Label>
              <Input
                id="address"
                value={formState.address}
                onChange={(e) => setFormState((prev) => ({ ...prev, address: e.target.value }))}
                placeholder="Adresse"
              />
            </div>
          </div>
          <DialogFooter>
            <DialogClose render={<Button variant="outline" />}>Abbrechen</DialogClose>
            <Button onClick={handleSubmit} disabled={createMutation.isPending || updateMutation.isPending}>
              {editing ? "Speichern" : "Erstellen"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete AlertDialog */}
      <AlertDialog open={!!deleting} onOpenChange={(open) => !open && setDeleting(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Standort löschen</AlertDialogTitle>
            <AlertDialogDescription>
              Möchten Sie "{deleting?.name}" wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Abbrechen</AlertDialogCancel>
            <AlertDialogAction variant="destructive" onClick={handleDelete} disabled={deleteMutation.isPending}>
              Löschen
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
