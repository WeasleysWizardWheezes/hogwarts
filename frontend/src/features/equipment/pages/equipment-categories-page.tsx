import { useState } from "react"
import { Plus, Pencil, Archive } from "lucide-react"
import { Button } from "@/shared/components/ui/button"
import { Input } from "@/shared/components/ui/input"
import { Label } from "@/shared/components/ui/label"
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
  useEquipmentCategories,
  useCreateEquipmentCategory,
  useUpdateEquipmentCategory,
  useArchiveEquipmentCategory,
} from "../api/equipment-categories-api"
import type {
  EquipmentCategoryResponse,
  CreateEquipmentCategoryRequest,
} from "../api/equipment-categories-api"

interface FormState {
  name: string
  description: string
}

const EMPTY_FORM: FormState = {
  name: "",
  description: "",
}

export default function EquipmentCategoriesPage() {
  const [search, setSearch] = useState("")
  const { data, isLoading, isError } = useEquipmentCategories({ search: search || undefined })
  const createMutation = useCreateEquipmentCategory()
  const updateMutation = useUpdateEquipmentCategory()
  const archiveMutation = useArchiveEquipmentCategory()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<EquipmentCategoryResponse | null>(null)
  const [archiving, setArchiving] = useState<EquipmentCategoryResponse | null>(null)
  const [formState, setFormState] = useState<FormState>(EMPTY_FORM)

  const items: EquipmentCategoryResponse[] = data?.data ?? []

  function openCreate() {
    setEditing(null)
    setFormState(EMPTY_FORM)
    setDialogOpen(true)
  }

  function openEdit(item: EquipmentCategoryResponse) {
    setEditing(item)
    setFormState({
      name: item.name ?? "",
      description: item.description ?? "",
    })
    setDialogOpen(true)
  }

  async function handleSubmit() {
    if (editing) {
      await updateMutation.mutateAsync({
        categoryId: editing.id!,
        body: { name: formState.name, description: formState.description || undefined },
      })
    } else {
      const body: CreateEquipmentCategoryRequest = {
        name: formState.name,
        description: formState.description || undefined,
      }
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
          <Skeleton className="h-8 w-56" />
          <Skeleton className="h-9 w-32" />
        </div>
        <Skeleton className="h-9 w-64" />
        <Skeleton className="h-48 w-full" />
      </div>
    )
  }

  if (isError) {
    return (
      <div className="flex flex-col gap-4">
        <h1 className="text-2xl font-bold">Gerätekategorien</h1>
        <p className="text-destructive">Kategorien konnten nicht geladen werden.</p>
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-4">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Gerätekategorien</h1>
          <p className="text-sm text-muted-foreground">
            Kategorien für Geräte und Ausrüstung verwalten.
          </p>
        </div>
        <Button onClick={openCreate} aria-label="Neue Kategorie erstellen">
          <Plus className="size-4" />
          Kategorie erstellen
        </Button>
      </div>

      {/* Suche */}
      <div className="flex gap-2">
        <Input
          placeholder="Kategorien suchen..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="max-w-sm"
          aria-label="Kategorien suchen"
        />
      </div>

      {/* Tabelle */}
      {items.length === 0 ? (
        <p className="text-muted-foreground">Keine Kategorien vorhanden.</p>
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
                <TableCell className="text-muted-foreground">{item.description ?? "–"}</TableCell>
                <TableCell>
                  <div className="flex items-center gap-1">
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => openEdit(item)}
                      aria-label={`Kategorie ${item.name} bearbeiten`}
                    >
                      <Pencil className="size-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => setArchiving(item)}
                      aria-label={`Kategorie ${item.name} archivieren`}
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
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {editing ? "Kategorie bearbeiten" : "Kategorie erstellen"}
            </DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="cat-name">Name *</Label>
              <Input
                id="cat-name"
                value={formState.name}
                onChange={(e) => setFormState((prev) => ({ ...prev, name: e.target.value }))}
                placeholder="z.B. Atemschutz"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="cat-description">Beschreibung</Label>
              <Input
                id="cat-description"
                value={formState.description}
                onChange={(e) =>
                  setFormState((prev) => ({ ...prev, description: e.target.value }))
                }
                placeholder="Optionale Beschreibung"
              />
            </div>
          </div>
          <DialogFooter>
            <DialogClose render={<Button variant="outline" />}>Abbrechen</DialogClose>
            <Button
              onClick={handleSubmit}
              disabled={
                !formState.name.trim() ||
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
            <AlertDialogTitle>Kategorie archivieren</AlertDialogTitle>
            <AlertDialogDescription>
              Möchten Sie die Kategorie &quot;{archiving?.name}&quot; wirklich archivieren? Sie wird
              nicht mehr in der Liste angezeigt.
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
