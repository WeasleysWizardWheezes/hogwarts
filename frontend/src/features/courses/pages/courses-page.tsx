import { useState } from "react"
import { Plus, Pencil, Trash2, Users } from "lucide-react"
import { Button } from "@/shared/components/ui/button"
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
import { Input } from "@/shared/components/ui/input"
import { Label } from "@/shared/components/ui/label"
import { Badge } from "@/shared/components/ui/badge"
import {
  useCourses,
  useCreateCourse,
  useUpdateCourse,
  useDeleteCourse,
} from "../api/courses-api"
import type { CourseResponse, CreateCourseRequest } from "../api/courses-api"

interface FormState {
  title: string
  description: string
  maxParticipants: string
  startDate: string
  endDate: string
  instructorId: string
}

const EMPTY_FORM: FormState = {
  title: "",
  description: "",
  maxParticipants: "",
  startDate: "",
  endDate: "",
  instructorId: "",
}

// Status-Badges für Anmeldestatus
const STATUS_LABELS: Record<string, string> = {
  PENDING: "Ausstehend",
  WAITING_LIST: "Warteliste",
  CONFIRMED: "Bestätigt",
  CANCELLED: "Storniert",
}

const STATUS_VARIANTS: Record<string, "default" | "secondary" | "outline" | "destructive"> = {
  PENDING: "secondary",
  WAITING_LIST: "outline",
  CONFIRMED: "default",
  CANCELLED: "destructive",
}

export default function CoursesPage() {
  const { data, isLoading, isError } = useCourses()
  const createMutation = useCreateCourse()
  const updateMutation = useUpdateCourse()
  const deleteMutation = useDeleteCourse()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [editing, setEditing] = useState<CourseResponse | null>(null)
  const [deleting, setDeleting] = useState<CourseResponse | null>(null)
  const [formState, setFormState] = useState<FormState>(EMPTY_FORM)

  const items: CourseResponse[] = data?.data ?? []

  function openCreate() {
    setEditing(null)
    setFormState(EMPTY_FORM)
    setDialogOpen(true)
  }

  function openEdit(item: CourseResponse) {
    setEditing(item)
    setFormState({
      title: item.name ?? "",
      description: item.description ?? "",
      maxParticipants: item.maxParticipants?.toString() ?? "",
      startDate: item.startDate?.split("T")[0] ?? "",
      endDate: item.endDate?.split("T")[0] ?? "",
      instructorId: item.instructorId ?? "",
    })
    setDialogOpen(true)
  }

  async function handleSubmit() {
    if (editing) {
      await updateMutation.mutateAsync({
        courseId: editing.id!,
        body: {
          name: formState.title,
          description: formState.description,
          maxParticipants: parseInt(formState.maxParticipants),
          startDate: formState.startDate + "T00:00:00Z",
          endDate: formState.endDate + "T00:00:00Z",
          instructorId: formState.instructorId,
        },
      })
    } else {
      await createMutation.mutateAsync({
        name: formState.title,
        description: formState.description,
        maxParticipants: parseInt(formState.maxParticipants),
        startDate: formState.startDate + "T00:00:00Z",
        endDate: formState.endDate + "T00:00:00Z",
        instructorId: formState.instructorId,
      } as CreateCourseRequest)
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
        <h1 className="text-2xl font-bold">Lehrgänge</h1>
        <p className="text-muted-foreground">Laden...</p>
      </div>
    )
  }

  if (isError) {
    return (
      <div className="flex flex-col gap-4">
        <h1 className="text-2xl font-bold">Lehrgänge</h1>
        <p className="text-destructive">Fehler beim Laden.</p>
      </div>
    )
  }

  // --- Render ---
  return (
    <div className="flex flex-col gap-4">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Lehrgänge</h1>
        <Button onClick={openCreate}>
          <Plus className="size-4" />
          Lehrgang erstellen
        </Button>
      </div>

      {/* Tabelle */}
      {items.length === 0 ? (
        <p className="text-muted-foreground">Keine Lehrgänge vorhanden.</p>
      ) : (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Titel</TableHead>
              <TableHead>Beschreibung</TableHead>
              <TableHead>Lehrgangsleiter</TableHead>
              <TableHead>Teilnehmer</TableHead>
              <TableHead>Datum</TableHead>
              <TableHead>Status</TableHead>
              <TableHead className="w-[100px]">Aktionen</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {items.map((item) => (
              <TableRow key={item.id}>
                <TableCell className="font-medium">{item.name}</TableCell>
                <TableCell>{item.description}</TableCell>
                <TableCell>{item.instructorName ?? item.instructorId}</TableCell>
                <TableCell>
                  {item.currentParticipants ?? 0} / {item.maxParticipants}
                </TableCell>
                <TableCell>
                  {item.startDate?.split("T")[0]} - {item.endDate?.split("T")[0]}
                </TableCell>
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
                    <Button variant="ghost" size="icon-sm" onClick={() => window.location.href = `/courses/${item.id}/enrollments`} aria-label="Anmeldungen">
                      <Users className="size-4" />
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
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>{editing ? "Lehrgang bearbeiten" : "Lehrgang erstellen"}</DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="title">Name</Label>
              <Input
                id="title"
                value={formState.title}
                onChange={(e) => setFormState((prev) => ({ ...prev, title: e.target.value }))}
                placeholder="z.B. Atemschutzgeräteträger"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="description">Beschreibung</Label>
              <Input
                id="description"
                value={formState.description}
                onChange={(e) => setFormState((prev) => ({ ...prev, description: e.target.value }))}
                placeholder="Beschreibung des Lehrgangs"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="maxParticipants">Maximale Teilnehmer</Label>
              <Input
                id="maxParticipants"
                type="number"
                value={formState.maxParticipants}
                onChange={(e) => setFormState((prev) => ({ ...prev, maxParticipants: e.target.value }))}
                placeholder="z.B. 20"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="startDate">Startdatum</Label>
              <Input
                id="startDate"
                type="date"
                value={formState.startDate}
                onChange={(e) => setFormState((prev) => ({ ...prev, startDate: e.target.value }))}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="endDate">Enddatum</Label>
              <Input
                id="endDate"
                type="date"
                value={formState.endDate}
                onChange={(e) => setFormState((prev) => ({ ...prev, endDate: e.target.value }))}
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="instructorId">Lehrgangsleiter ID</Label>
              <Input
                id="instructorId"
                value={formState.instructorId}
                onChange={(e) => setFormState((prev) => ({ ...prev, instructorId: e.target.value }))}
                placeholder="Mitglieder-ID"
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
      <AlertDialog open={!!deleting} onOpenChange={(open: boolean) => !open && setDeleting(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Lehrgang löschen</AlertDialogTitle>
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
