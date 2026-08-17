import { useState } from "react"
import { Plus, Pencil, Trash2 } from "lucide-react"
import { Button } from "@/shared/components/ui/button"
import { Badge } from "@/shared/components/ui/badge"
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
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "@/shared/components/ui/select"
import {
  useVehicles, useCreateVehicle, useUpdateVehicle, useDeleteVehicle,
} from "../api/vehicles-api"
import type { VehicleResponse, CreateVehicleRequest, VehicleStatus } from "../api/vehicles-api"
import { useVehicleGroups } from "@/features/vehicle-groups"

const STATUS_LABELS: Record<string, string> = {
  VERFUEGBAR: "Verfügbar",
  IM_EINSATZ: "Im Einsatz",
  WARTUNG: "Wartung",
  DEFEKT: "Defekt",
}

const STATUS_VARIANTS: Record<string, "default" | "secondary" | "outline" | "destructive"> = {
  VERFUEGBAR: "default",
  IM_EINSATZ: "secondary",
  WARTUNG: "outline",
  DEFEKT: "destructive",
}

const ALL_STATUSES: VehicleStatus[] = [
  "VERFUEGBAR",
  "IM_EINSATZ",
  "WARTUNG",
  "DEFEKT",
]

interface FormState {
  name: string
  funkrufname: string
  kennzeichen: string
  baujahr: string
  beschreibung: string
  status: string
  vehicleGroupId: string
}

const EMPTY_FORM: FormState = {
  name: "",
  funkrufname: "",
  kennzeichen: "",
  baujahr: "",
  beschreibung: "",
  status: "VERFUEGBAR",
  vehicleGroupId: "",
}

export default function VehiclesPage() {
  const [filterGroupId, setFilterGroupId] = useState<string>("")
  const [filterStatus, setFilterStatus] = useState<string>("")

  const { data, isLoading, isError } = useVehicles({
    vehicleGroupId: filterGroupId || undefined,
    status: (filterStatus as VehicleStatus) || undefined,
  })
  const { data: groupsData } = useVehicleGroups()
  const createMutation = useCreateVehicle()
  const updateMutation = useUpdateVehicle()
  const deleteMutation = useDeleteVehicle()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<VehicleResponse | null>(null)
  const [deleting, setDeleting] = useState<VehicleResponse | null>(null)
  const [formState, setFormState] = useState<FormState>(EMPTY_FORM)

  const items: VehicleResponse[] = data?.data ?? []
  const vehicleGroups = groupsData?.data ?? []

  function openCreate() {
    setEditing(null)
    setFormState(EMPTY_FORM)
    setDialogOpen(true)
  }

  function openEdit(item: VehicleResponse) {
    setEditing(item)
    setFormState({
      name: item.name ?? "",
      funkrufname: item.funkrufname ?? "",
      kennzeichen: item.kennzeichen ?? "",
      baujahr: item.baujahr?.toString() ?? "",
      beschreibung: item.beschreibung ?? "",
      status: item.status ?? "VERFUEGBAR",
      vehicleGroupId: item.vehicleGroupId ?? "",
    })
    setDialogOpen(true)
  }

  async function handleSubmit() {
    const body = {
      name: formState.name,
      funkrufname: formState.funkrufname,
      kennzeichen: formState.kennzeichen,
      baujahr: formState.baujahr ? Number(formState.baujahr) : undefined,
      beschreibung: formState.beschreibung || undefined,
      status: formState.status as VehicleStatus,
      vehicleGroupId: formState.vehicleGroupId,
    }
    if (editing) {
      await updateMutation.mutateAsync({ vehicleId: editing.id!, body })
    } else {
      await createMutation.mutateAsync(body as CreateVehicleRequest)
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
        <h1 className="text-2xl font-bold">Fahrzeuge</h1>
        <p className="text-muted-foreground">Laden...</p>
      </div>
    )
  }

  if (isError) {
    return (
      <div className="flex flex-col gap-4">
        <h1 className="text-2xl font-bold">Fahrzeuge</h1>
        <p className="text-destructive">Fehler beim Laden.</p>
      </div>
    )
  }

  // --- Render ---
  return (
    <div className="flex flex-col gap-4">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Fahrzeuge</h1>
        <Button onClick={openCreate}>
          <Plus className="size-4" />
          Erstellen
        </Button>
      </div>

      {/* Filters */}
      <div className="flex gap-4">
        <Select value={filterGroupId} onValueChange={(value) => setFilterGroupId(value ?? "")}>
          <SelectTrigger className="w-[200px]">
            <SelectValue placeholder="Alle Gruppen" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="">Alle Gruppen</SelectItem>
            {vehicleGroups.map((g) => (
              <SelectItem key={g.id} value={g.id!}>{g.name}</SelectItem>
            ))}
          </SelectContent>
        </Select>

        <Select value={filterStatus} onValueChange={(value) => setFilterStatus(value ?? "")}>
          <SelectTrigger className="w-[200px]">
            <SelectValue placeholder="Alle Status" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="">Alle Status</SelectItem>
            {ALL_STATUSES.map((s) => (
              <SelectItem key={s} value={s}>{STATUS_LABELS[s]}</SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {/* Tabelle */}
      {items.length === 0 ? (
        <p className="text-muted-foreground">Keine Einträge vorhanden.</p>
      ) : (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Funkrufname</TableHead>
              <TableHead>Kennzeichen</TableHead>
              <TableHead>Gruppe</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="w-[100px]">Aktionen</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {items.map((item) => (
              <TableRow key={item.id}>
                <TableCell className="font-medium">{item.name}</TableCell>
                <TableCell>{item.funkrufname}</TableCell>
                <TableCell>{item.kennzeichen}</TableCell>
                <TableCell>{item.vehicleGroupName ?? "–"}</TableCell>
                <TableCell>
                  <Badge variant={STATUS_VARIANTS[item.status ?? ""] ?? "default"}>
                    {STATUS_LABELS[item.status ?? ""] ?? item.status}
                  </Badge>
                </TableCell>
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
            <DialogTitle>{editing ? "Fahrzeug bearbeiten" : "Fahrzeug erstellen"}</DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="name">Name</Label>
              <Input
                id="name"
                value={formState.name}
                onChange={(e) => setFormState((prev) => ({ ...prev, name: e.target.value }))}
                placeholder="z.B. 01-HLF20-01"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="funkrufname">Funkrufname</Label>
              <Input
                id="funkrufname"
                value={formState.funkrufname}
                onChange={(e) => setFormState((prev) => ({ ...prev, funkrufname: e.target.value }))}
                placeholder="z.B. Florian Monheim 01-HLF20-01"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="kennzeichen">Kennzeichen</Label>
              <Input
                id="kennzeichen"
                value={formState.kennzeichen}
                onChange={(e) => setFormState((prev) => ({ ...prev, kennzeichen: e.target.value }))}
                placeholder="z.B. ME-FM 219"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="baujahr">Baujahr</Label>
              <Input
                id="baujahr"
                type="number"
                value={formState.baujahr}
                onChange={(e) => setFormState((prev) => ({ ...prev, baujahr: e.target.value }))}
                placeholder="z.B. 2019"
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
            <div className="grid gap-2">
              <Label htmlFor="status">Status</Label>
              <Select
                value={formState.status}
                onValueChange={(value) => { if (value) setFormState((prev) => ({ ...prev, status: value })) }}
              >
                <SelectTrigger id="status">
                  <SelectValue placeholder="Status wählen" />
                </SelectTrigger>
                <SelectContent>
                  {ALL_STATUSES.map((s) => (
                    <SelectItem key={s} value={s}>{STATUS_LABELS[s]}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="grid gap-2">
              <Label htmlFor="vehicleGroupId">Fahrzeuggruppe</Label>
              <Select
                value={formState.vehicleGroupId}
                onValueChange={(value) => { if (value) setFormState((prev) => ({ ...prev, vehicleGroupId: value })) }}
              >
                <SelectTrigger id="vehicleGroupId">
                  <SelectValue placeholder="Gruppe wählen" />
                </SelectTrigger>
                <SelectContent>
                  {vehicleGroups.map((g) => (
                    <SelectItem key={g.id} value={g.id!}>{g.name}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
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
            <AlertDialogTitle>Fahrzeug löschen</AlertDialogTitle>
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
