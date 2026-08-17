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
import {
  useVehicleGroups, useCreateVehicleGroup, useUpdateVehicleGroup, useDeleteVehicleGroup,
} from "../api/vehicle-groups-api"
import type { VehicleGroupResponse, CreateVehicleGroupRequest } from "../api/vehicle-groups-api"

interface FormState {
  name: string
  beschreibung: string
}

const EMPTY_FORM: FormState = {
  name: "",
  beschreibung: "",
}

export default function VehicleGroupsPage() {
  const { data, isLoading, isError } = useVehicleGroups()
  const createMutation = useCreateVehicleGroup()
  const updateMutation = useUpdateVehicleGroup()
  const deleteMutation = useDeleteVehicleGroup()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<VehicleGroupResponse | null>(null)
  const [deleting, setDeleting] = useState<VehicleGroupResponse | null>(null)
  const [formState, setFormState] = useState<FormState>(EMPTY_FORM)

  const items: VehicleGroupResponse[] = data?.data ?? []

  function openCreate() {
    setEditing(null)
    setFormState(EMPTY_FORM)
    setDialogOpen(true)
  }

  function openEdit(item: VehicleGroupResponse) {
    setEditing(item)
    setFormState({
      name: item.name ?? "",
      beschreibung: item.beschreibung ?? "",
    })
    setDialogOpen(true)
  }

  async function handleSubmit() {
    if (editing) {
      await updateMutation.mutateAsync({
        vehicleGroupId: editing.id!,
        body: { name: formState.name, beschreibung: formState.beschreibung || undefined },
      })
    } else {
      await createMutation.mutateAsync({
        name: formState.name,
        beschreibung: formState.beschreibung || undefined,
      } as CreateVehicleGroupRequest)
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
        <h1 className="text-2xl font-bold">Fahrzeuggruppen</h1>
        <p className="text-muted-foreground">Laden...</p>
      </div>
    )
  }

  if (isError) {
    return (
      <div className="flex flex-col gap-4">
        <h1 className="text-2xl font-bold">Fahrzeuggruppen</h1>
        <p className="text-destructive">Fehler beim Laden.</p>
      </div>
    )
  }

  // --- Render ---
  return (
    <div className="flex flex-col gap-4">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Fahrzeuggruppen</h1>
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
              <TableHead>Beschreibung</TableHead>
              <TableHead className="w-[100px]">Aktionen</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {items.map((item) => (
              <TableRow key={item.id}>
                <TableCell className="font-medium">{item.name}</TableCell>
                <TableCell>{item.beschreibung ?? "–"}</TableCell>
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
            <DialogTitle>{editing ? "Fahrzeuggruppe bearbeiten" : "Fahrzeuggruppe erstellen"}</DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="name">Name</Label>
              <Input
                id="name"
                value={formState.name}
                onChange={(e) => setFormState((prev) => ({ ...prev, name: e.target.value }))}
                placeholder="z.B. Löschfahrzeuge"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="beschreibung">Beschreibung</Label>
              <Input
                id="beschreibung"
                value={formState.beschreibung}
                onChange={(e) => setFormState((prev) => ({ ...prev, beschreibung: e.target.value }))}
                placeholder="Optionale Beschreibung"
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
            <AlertDialogTitle>Fahrzeuggruppe löschen</AlertDialogTitle>
            <AlertDialogDescription>
              Möchten Sie &quot;{deleting?.name}&quot; wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden.
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
