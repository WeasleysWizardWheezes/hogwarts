import { useState } from "react"
import { useParams, useNavigate } from "react-router"
import { ArrowLeft, Pencil, Archive } from "lucide-react"
import { Button } from "@/shared/components/ui/button"
import { Badge } from "@/shared/components/ui/badge"
import { Skeleton } from "@/shared/components/ui/skeleton"
import { Input } from "@/shared/components/ui/input"
import { Label } from "@/shared/components/ui/label"
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
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/shared/components/ui/table"
import {
  useEquipment,
  useEquipmentHistory,
  useUpdateEquipment,
  useArchiveEquipment,
} from "../api/equipment-api"
import type { EquipmentResponse, EquipmentStatus, EquipmentHistoryResponse } from "../api/equipment-api"
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

export default function EquipmentDetailPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { data: equipment, isLoading, isError } = useEquipment(id!)
  const { data: historyData, isLoading: historyLoading } = useEquipmentHistory(id!)
  const { data: categoriesData } = useEquipmentCategories()
  const { data: vehiclesData } = useVehicles()
  const updateMutation = useUpdateEquipment()
  const archiveMutation = useArchiveEquipment()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [archiveOpen, setArchiveOpen] = useState(false)
  const [formState, setFormState] = useState<FormState>({
    name: "",
    inventoryNumber: "",
    description: "",
    status: "VERFUEGBAR",
    categoryId: "",
    vehicleId: "",
    nextInspectionDate: "",
    nextMaintenanceDate: "",
  })

  const categories = categoriesData?.data ?? []
  const vehicles = vehiclesData?.data ?? []
  const historyItems: EquipmentHistoryResponse[] = Array.isArray(historyData) ? historyData : []

  function openEdit(item: EquipmentResponse) {
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
    if (!equipment) return
    await updateMutation.mutateAsync({
      equipmentId: equipment.id!,
      body: {
        name: formState.name,
        inventoryNumber: formState.inventoryNumber,
        description: formState.description || undefined,
        status: formState.status as EquipmentStatus,
        categoryId: formState.categoryId,
        vehicleId: formState.vehicleId || undefined,
        nextInspectionDate: formState.nextInspectionDate || undefined,
        nextMaintenanceDate: formState.nextMaintenanceDate || undefined,
      },
    })
    setDialogOpen(false)
  }

  async function handleArchive() {
    if (!equipment) return
    await archiveMutation.mutateAsync(equipment.id!)
    setArchiveOpen(false)
    navigate("/equipment")
  }

  if (isLoading) {
    return (
      <div className="flex flex-col gap-6">
        <Skeleton className="h-8 w-48" />
        <Skeleton className="h-32 w-full" />
        <Skeleton className="h-48 w-full" />
      </div>
    )
  }

  if (isError || !equipment) {
    return (
      <div className="flex flex-col gap-4">
        <Button variant="ghost" onClick={() => navigate("/equipment")} className="self-start">
          <ArrowLeft className="size-4" />
          Zurück
        </Button>
        <p className="text-destructive">Gerät konnte nicht geladen werden.</p>
      </div>
    )
  }

  const inspectionDate = equipment.nextInspectionDate
    ? new Date(equipment.nextInspectionDate)
    : null
  const isInspectionOverdue = inspectionDate && inspectionDate < new Date()

  return (
    <div className="flex flex-col gap-6">
      {/* Back + Header */}
      <div className="flex items-start justify-between">
        <div className="flex flex-col gap-1">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => navigate("/equipment")}
            className="self-start -ml-2"
            aria-label="Zurück zur Geräteliste"
          >
            <ArrowLeft className="size-4" />
            Zurück
          </Button>
          <h1 className="text-2xl font-bold">{equipment.name}</h1>
          <p className="text-sm text-muted-foreground">{equipment.inventoryNumber}</p>
        </div>
        <div className="flex gap-2">
          <Button
            variant="outline"
            onClick={() => openEdit(equipment)}
            aria-label="Gerät bearbeiten"
          >
            <Pencil className="size-4" />
            Bearbeiten
          </Button>
          <Button
            variant="outline"
            onClick={() => setArchiveOpen(true)}
            aria-label="Gerät archivieren"
          >
            <Archive className="size-4" />
            Archivieren
          </Button>
        </div>
      </div>

      {/* Details */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <div className="flex flex-col gap-1">
          <span className="text-xs text-muted-foreground uppercase tracking-wide">Status</span>
          <Badge
            variant={STATUS_VARIANTS[equipment.status ?? ""] ?? "outline"}
            className="self-start"
          >
            {STATUS_LABELS[equipment.status ?? ""] ?? equipment.status}
          </Badge>
        </div>
        <div className="flex flex-col gap-1">
          <span className="text-xs text-muted-foreground uppercase tracking-wide">Kategorie</span>
          <span>{equipment.categoryName ?? "–"}</span>
        </div>
        <div className="flex flex-col gap-1">
          <span className="text-xs text-muted-foreground uppercase tracking-wide">Fahrzeug</span>
          <span>{equipment.vehicleName ?? "–"}</span>
        </div>
        <div className="flex flex-col gap-1">
          <span className="text-xs text-muted-foreground uppercase tracking-wide">
            Nächste Prüfung
          </span>
          <span
            className={isInspectionOverdue ? "text-destructive font-medium" : undefined}
            aria-label={
              isInspectionOverdue ? "Prüftermin überfällig" : undefined
            }
          >
            {inspectionDate
              ? inspectionDate.toLocaleDateString("de-DE")
              : "–"}
            {isInspectionOverdue && " (überfällig)"}
          </span>
        </div>
        <div className="flex flex-col gap-1">
          <span className="text-xs text-muted-foreground uppercase tracking-wide">
            Nächste Wartung
          </span>
          <span>
            {equipment.nextMaintenanceDate
              ? new Date(equipment.nextMaintenanceDate).toLocaleDateString("de-DE")
              : "–"}
          </span>
        </div>
        {equipment.description && (
          <div className="flex flex-col gap-1 sm:col-span-2 lg:col-span-3">
            <span className="text-xs text-muted-foreground uppercase tracking-wide">
              Beschreibung
            </span>
            <span>{equipment.description}</span>
          </div>
        )}
      </div>

      {/* Änderungshistorie */}
      <div className="flex flex-col gap-3">
        <h2 className="text-lg font-semibold">Änderungshistorie</h2>
        {historyLoading ? (
          <Skeleton className="h-32 w-full" />
        ) : historyItems.length === 0 ? (
          <p className="text-muted-foreground text-sm">Keine Änderungen vorhanden.</p>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Datum</TableHead>
                <TableHead>Vorheriger Status</TableHead>
                <TableHead>Neuer Status</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {historyItems.map((entry) => (
                  <TableRow key={entry.id}>
                    <TableCell>
                      {entry.changedAt
                        ? new Date(entry.changedAt).toLocaleString("de-DE")
                        : "–"}
                    </TableCell>
                    <TableCell>
                      {entry.previousStatus ? (
                        <Badge variant={STATUS_VARIANTS[entry.previousStatus] ?? "outline"}>
                          {STATUS_LABELS[entry.previousStatus] ?? entry.previousStatus}
                        </Badge>
                      ) : (
                        <span className="text-muted-foreground">–</span>
                      )}
                    </TableCell>
                    <TableCell>
                      {entry.newStatus ? (
                        <Badge variant={STATUS_VARIANTS[entry.newStatus] ?? "outline"}>
                          {STATUS_LABELS[entry.newStatus] ?? entry.newStatus}
                        </Badge>
                      ) : (
                        "–"
                      )}
                    </TableCell>
                  </TableRow>
                )
              )}
            </TableBody>
          </Table>
        )}
      </div>

      {/* Edit Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>Gerät bearbeiten</DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="det-name">Name *</Label>
              <Input
                id="det-name"
                value={formState.name}
                onChange={(e) => setFormState((prev) => ({ ...prev, name: e.target.value }))}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="det-inventory">Inventarnummer *</Label>
              <Input
                id="det-inventory"
                value={formState.inventoryNumber}
                onChange={(e) =>
                  setFormState((prev) => ({ ...prev, inventoryNumber: e.target.value }))
                }
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="det-category">Kategorie *</Label>
              <Select
                value={formState.categoryId}
                onValueChange={(value) => {
                  if (value) setFormState((prev) => ({ ...prev, categoryId: value }))
                }}
              >
                <SelectTrigger id="det-category">
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
              <Label htmlFor="det-status">Status</Label>
              <Select
                value={formState.status}
                onValueChange={(value) => {
                  if (value) setFormState((prev) => ({ ...prev, status: value }))
                }}
              >
                <SelectTrigger id="det-status">
                  <SelectValue />
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
              <Label htmlFor="det-vehicle">Fahrzeug</Label>
              <Select
                value={formState.vehicleId}
                onValueChange={(value) =>
                  setFormState((prev) => ({ ...prev, vehicleId: value ?? "" }))
                }
              >
                <SelectTrigger id="det-vehicle">
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
              <Label htmlFor="det-description">Beschreibung</Label>
              <Input
                id="det-description"
                value={formState.description}
                onChange={(e) =>
                  setFormState((prev) => ({ ...prev, description: e.target.value }))
                }
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="grid gap-2">
                <Label htmlFor="det-inspection">Nächste Prüfung</Label>
                <Input
                  id="det-inspection"
                  type="date"
                  value={formState.nextInspectionDate}
                  onChange={(e) =>
                    setFormState((prev) => ({ ...prev, nextInspectionDate: e.target.value }))
                  }
                />
              </div>
              <div className="grid gap-2">
                <Label htmlFor="det-maintenance">Nächste Wartung</Label>
                <Input
                  id="det-maintenance"
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
                updateMutation.isPending
              }
            >
              {updateMutation.isPending ? "Wird gespeichert..." : "Speichern"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Archive AlertDialog */}
      <AlertDialog open={archiveOpen} onOpenChange={setArchiveOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Gerät archivieren</AlertDialogTitle>
            <AlertDialogDescription>
              Möchten Sie &quot;{equipment.name}&quot; wirklich archivieren? Das Gerät wird nicht
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
