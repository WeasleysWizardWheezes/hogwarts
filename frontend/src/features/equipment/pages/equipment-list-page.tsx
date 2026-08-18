import { useState } from "react"
import { Plus, Pencil, Archive, Search } from "lucide-react"
import { useNavigate } from "react-router"
import { Button } from "@/shared/components/ui/button"
import { Input } from "@/shared/components/ui/input"
import { Label } from "@/shared/components/ui/label"
import { Badge } from "@/shared/components/ui/badge"
import { Skeleton } from "@/shared/components/ui/skeleton"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/shared/components/ui/table"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogClose,
} from "@/shared/components/ui/dialog"
import {
  AlertDialog,
  AlertDialogContent,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogAction,
  AlertDialogCancel,
} from "@/shared/components/ui/alert-dialog"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/shared/components/ui/select"
import {
  useEquipmentList,
  useCreateEquipment,
  useUpdateEquipment,
  useArchiveEquipment,
} from "../api/equipment-api"
import type { EquipmentResponse, CreateEquipmentRequest, EquipmentStatus } from "../api/equipment-api"
import { useEquipmentCategories } from "../api/equipment-categories-api"
import { useVehicles } from "@/features/vehicles"

const STATUS_LABELS: Record<string, string> = {
  VERFUEGBAR: "Verfügbar",
  IN_GEBRAUCH: "In Gebrauch",
  DEFEKT: "Defekt",
  WARTUNG: "Wartung",
  ARCHIVIERT: "Archiviert",
}

const STATUS_VARIANTS: Record<
  string,
  "default" | "secondary" | "outline" | "destructive"
> = {
  VERFUEGBAR: "default",
  IN_GEBRAUCH: "secondary",
  WARTUNG: "outline",
  DEFEKT: "destructive",
  ARCHIVIERT: "outline",
}

const ALL_STATUSES: EquipmentStatus[] = [
  "VERFUEGBAR",
  "IN_GEBRAUCH",
  "DEFEKT",
  "WARTUNG",
]

interface FormState {
  name: string
  inventoryNumber: string
  description: string
  status: string
  categoryId: string
  vehicleId: string
  nextInspectionDate: string
  nextMaintenanceDate: string
}

const EMPTY_FORM: FormState = {
  name: "",
  inventoryNumber: "",
  description: "",
  status: "VERFUEGBAR",
  categoryId: "",
  vehicleId: "",
  nextInspectionDate: "",
  nextMaintenanceDate: "",
}

export default function EquipmentListPage() {
  const navigate = useNavigate()
  const [search, setSearch] = useState("")
  const [filterCategoryId, setFilterCategoryId] = useState("")
  const [filterVehicleId, setFilterVehicleId] = useState("")
  const [filterStatus, setFilterStatus] = useState("")

  const { data, isLoading, isError } = useEquipmentList({
    search: search || undefined,
    categoryId: filterCategoryId || undefined,
    vehicleId: filterVehicleId || undefined,
    status: (filterStatus as EquipmentStatus) || undefined,
  })
  const { data: categoriesData } = useEquipmentCategories()
  const { data: vehiclesData } = useVehicles()
  const createMutation = useCreateEquipment()
  const updateMutation = useUpdateEquipment()
  const archiveMutation = useArchiveEquipment()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<EquipmentResponse | null>(null)
  const [archiving, setArchiving] = useState<EquipmentResponse | null>(null)
  const [formState, setFormState] = useState<FormState>(EMPTY_FORM)

  const items: EquipmentResponse[] = data?.data ?? []
  const categories = categoriesData?.data ?? []
  const vehicles = vehiclesData?.data ?? []

  function openCreate() {
    setEditing(null)
    setFormState(EMPTY_FORM)
    setDialogOpen(true)
  }

  function openEdit(item: EquipmentResponse) {
    setEditing(item)
    setFormState({
      name: item.name ?? "",
      inventoryNumber: item.inventoryNumber ?? "",
      description: item.description ?? "",
      status: item.status ?? "VERFUEGBAR",
      categoryId: item.categoryId ?? "",
      vehicleId: item.vehicleId ?? "",
      nextInspectionDate: item.nextInspectionDate ?? "",
      nextMaintenanceDate: item.nextMaintenanceDate ?? "",
    })
    setDialogOpen(true)
  }

  async function handleSubmit() {
    const body: CreateEquipmentRequest = {
      name: formState.name,
      inventoryNumber: formState.inventoryNumber,
      description: formState.description || undefined,
      status: formState.status as EquipmentStatus,
      categoryId: formState.categoryId,
      vehicleId: formState.vehicleId || undefined,
      nextInspectionDate: formState.nextInspectionDate || undefined,
      nextMaintenanceDate: formState.nextMaintenanceDate || undefined,
    }
    if (editing) {
      await updateMutation.mutateAsync({ equipmentId: editing.id!, body })
    } else {
      await createMutation.mutateAsync(body)
    }
    setDialogOpen(false)
  }

  async function handleArchive() {
    if (!archiving) return
    await archiveMutation.mutateAsync(archiving.id!)
    setArchiving(null)
  }

  if (isLoading) {
    return (
      <div className="flex flex-col gap-4">
        <div className="flex items-center justify-between">
          <Skeleton className="h-8 w-40" />
          <Skeleton className="h-9 w-36" />
        </div>
        <div className="flex gap-2">
          <Skeleton className="h-9 w-64" />
          <Skeleton className="h-9 w-40" />
          <Skeleton className="h-9 w-40" />
          <Skeleton className="h-9 w-40" />
        </div>
        <Skeleton className="h-64 w-full" />
      </div>
    )
  }

  if (isError) {
    return (
      <div className="flex flex-col gap-4">
        <h1 className="text-2xl font-bold">Geräte</h1>
        <p className="text-destructive">Geräte konnten nicht geladen werden.</p>
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-4">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Geräte</h1>
          <p className="text-sm text-muted-foreground">
            Ausrüstung, Status und Prüftermine verwalten.
          </p>
        </div>
        <Button onClick={openCreate} aria-label="Neues Gerät erstellen">
          <Plus className="size-4" />
          Gerät erstellen
        </Button>
      </div>

      {/* Filter */}
      <div className="flex flex-wrap gap-2">
        <div className="relative">
          <Search className="absolute left-2.5 top-2.5 size-4 text-muted-foreground" />
          <Input
            placeholder="Name oder Inventarnummer..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-8 w-64"
            aria-label="Geräte suchen"
          />
        </div>

        <Select
          value={filterCategoryId}
          onValueChange={(value) => setFilterCategoryId(value ?? "")}
        >
          <SelectTrigger className="w-44" aria-label="Nach Kategorie filtern">
            <SelectValue placeholder="Alle Kategorien" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="">Alle Kategorien</SelectItem>
            {categories.map((cat) => (
              <SelectItem key={cat.id} value={cat.id!}>
                {cat.name}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>

        <Select
          value={filterVehicleId}
          onValueChange={(value) => setFilterVehicleId(value ?? "")}
        >
          <SelectTrigger className="w-44" aria-label="Nach Fahrzeug filtern">
            <SelectValue placeholder="Alle Fahrzeuge" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="">Alle Fahrzeuge</SelectItem>
            {vehicles.map((v) => (
              <SelectItem key={v.id} value={v.id!}>
                {v.name}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>

        <Select
          value={filterStatus}
          onValueChange={(value) => setFilterStatus(value ?? "")}
        >
          <SelectTrigger className="w-40" aria-label="Nach Status filtern">
            <SelectValue placeholder="Alle Status" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="">Alle Status</SelectItem>
            {ALL_STATUSES.map((s) => (
              <SelectItem key={s} value={s}>
                {STATUS_LABELS[s]}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {/* Tabelle */}
      {items.length === 0 ? (
        <p className="text-muted-foreground">Keine Geräte gefunden.</p>
      ) : (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Inventarnummer</TableHead>
              <TableHead>Kategorie</TableHead>
              <TableHead>Fahrzeug</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Nächste Prüfung</TableHead>
              <TableHead className="w-[100px]">Aktionen</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {items.map((item) => (
              <TableRow
                key={item.id}
                className="cursor-pointer"
                onClick={() => navigate(`/equipment/${item.id}`)}
              >
                <TableCell className="font-medium">{item.name}</TableCell>
                <TableCell className="text-muted-foreground">{item.inventoryNumber}</TableCell>
                <TableCell>{item.categoryName ?? "–"}</TableCell>
                <TableCell>{item.vehicleName ?? "–"}</TableCell>
                <TableCell>
                  <Badge variant={STATUS_VARIANTS[item.status ?? ""] ?? "outline"}>
                    {STATUS_LABELS[item.status ?? ""] ?? item.status}
                  </Badge>
                </TableCell>
                <TableCell>
                  {item.nextInspectionDate ? (
                    <span
                      className={
                        new Date(item.nextInspectionDate) < new Date()
                          ? "text-destructive font-medium"
                          : undefined
                      }
                    >
                      {new Date(item.nextInspectionDate).toLocaleDateString("de-DE")}
                    </span>
                  ) : (
                    "–"
                  )}
                </TableCell>
                <TableCell>
                  <div
                    className="flex items-center gap-1"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => openEdit(item)}
                      aria-label={`Gerät ${item.name} bearbeiten`}
                    >
                      <Pencil className="size-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => setArchiving(item)}
                      aria-label={`Gerät ${item.name} archivieren`}
                    >
                      <Archive className="size-4" />
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
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>{editing ? "Gerät bearbeiten" : "Gerät erstellen"}</DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="eq-name">Name *</Label>
              <Input
                id="eq-name"
                value={formState.name}
                onChange={(e) => setFormState((prev) => ({ ...prev, name: e.target.value }))}
                placeholder="z.B. Pressluftatmer PA 300"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="eq-inventory">Inventarnummer *</Label>
              <Input
                id="eq-inventory"
                value={formState.inventoryNumber}
                onChange={(e) =>
                  setFormState((prev) => ({ ...prev, inventoryNumber: e.target.value }))
                }
                placeholder="z.B. AGT-2024-0042"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="eq-category">Kategorie *</Label>
              <Select
                value={formState.categoryId}
                onValueChange={(value) => {
                  if (value) setFormState((prev) => ({ ...prev, categoryId: value }))
                }}
              >
                <SelectTrigger id="eq-category">
                  <SelectValue placeholder="Kategorie wählen" />
                </SelectTrigger>
                <SelectContent>
                  {categories.map((cat) => (
                    <SelectItem key={cat.id} value={cat.id!}>
                      {cat.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="grid gap-2">
              <Label htmlFor="eq-status">Status</Label>
              <Select
                value={formState.status}
                onValueChange={(value) => {
                  if (value) setFormState((prev) => ({ ...prev, status: value }))
                }}
              >
                <SelectTrigger id="eq-status">
                  <SelectValue placeholder="Status wählen" />
                </SelectTrigger>
                <SelectContent>
                  {ALL_STATUSES.map((s) => (
                    <SelectItem key={s} value={s}>
                      {STATUS_LABELS[s]}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="grid gap-2">
              <Label htmlFor="eq-vehicle">Fahrzeug</Label>
              <Select
                value={formState.vehicleId}
                onValueChange={(value) => setFormState((prev) => ({ ...prev, vehicleId: value ?? "" }))}
              >
                <SelectTrigger id="eq-vehicle">
                  <SelectValue placeholder="Kein Fahrzeug" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="">Kein Fahrzeug</SelectItem>
                  {vehicles.map((v) => (
                    <SelectItem key={v.id} value={v.id!}>
                      {v.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="grid gap-2">
              <Label htmlFor="eq-description">Beschreibung</Label>
              <Input
                id="eq-description"
                value={formState.description}
                onChange={(e) =>
                  setFormState((prev) => ({ ...prev, description: e.target.value }))
                }
                placeholder="Optionale Beschreibung"
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="grid gap-2">
                <Label htmlFor="eq-inspection">Nächste Prüfung</Label>
                <Input
                  id="eq-inspection"
                  type="date"
                  value={formState.nextInspectionDate}
                  onChange={(e) =>
                    setFormState((prev) => ({ ...prev, nextInspectionDate: e.target.value }))
                  }
                />
              </div>
              <div className="grid gap-2">
                <Label htmlFor="eq-maintenance">Nächste Wartung</Label>
                <Input
                  id="eq-maintenance"
                  type="date"
                  value={formState.nextMaintenanceDate}
                  onChange={(e) =>
                    setFormState((prev) => ({ ...prev, nextMaintenanceDate: e.target.value }))
                  }
                />
              </div>
            </div>
          </div>
          <DialogFooter>
            <DialogClose render={<Button variant="outline" />}>Abbrechen</DialogClose>
            <Button
              onClick={handleSubmit}
              disabled={
                !formState.name.trim() ||
                !formState.inventoryNumber.trim() ||
                !formState.categoryId ||
                createMutation.isPending ||
                updateMutation.isPending
              }
            >
              {createMutation.isPending || updateMutation.isPending
                ? "Wird gespeichert..."
                : editing
                  ? "Speichern"
                  : "Erstellen"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Archive AlertDialog */}
      <AlertDialog open={!!archiving} onOpenChange={(open) => !open && setArchiving(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Gerät archivieren</AlertDialogTitle>
            <AlertDialogDescription>
              Möchten Sie &quot;{archiving?.name}&quot; wirklich archivieren? Das Gerät wird nicht
              mehr in der aktiven Liste angezeigt.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Abbrechen</AlertDialogCancel>
            <AlertDialogAction
              variant="destructive"
              onClick={handleArchive}
              disabled={archiveMutation.isPending}
            >
              {archiveMutation.isPending ? "Wird archiviert..." : "Archivieren"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
