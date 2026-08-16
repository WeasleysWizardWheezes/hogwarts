import { useState } from "react"
import { Plus, Trash2, Check } from "lucide-react"
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
import { useParams } from "react-router-dom"
import {
  useCourse,
  useCourseEnrollments,
  useCreateEnrollment,
  useCancelEnrollment,
} from "../api/courses-api"
import type { CourseEnrollmentResponse } from "../api/courses-api"

interface FormState {
  memberId: string
  comment: string
}

const EMPTY_FORM: FormState = {
  memberId: "",
  comment: "",
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

export default function CourseEnrollmentsPage() {
  const { courseId } = useParams<{ courseId: string }>()
  const { data: courseData, isLoading: isCourseLoading, isError: isCourseError } = useCourse(courseId!)
  const { data, isLoading, isError } = useCourseEnrollments(courseId!)
  const createMutation = useCreateEnrollment()
  const cancelMutation = useCancelEnrollment()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [cancelling, setCancelling] = useState<CourseEnrollmentResponse | null>(null)
  const [formState, setFormState] = useState<FormState>(EMPTY_FORM)

  const items: CourseEnrollmentResponse[] = data?.data ?? []

  function openCreate() {
    setFormState(EMPTY_FORM)
    setDialogOpen(true)
  }

  async function handleSubmit() {
    await createMutation.mutateAsync({
      courseId: courseId!,
      body: {
        memberId: formState.memberId,
        comment: formState.comment || undefined,
      },
    })
    setDialogOpen(false)
  }

  async function handleCancel() {
    if (!cancelling) return
    await cancelMutation.mutateAsync({
      courseId: courseId!,
      enrollmentId: cancelling.id!,
    })
    setCancelling(null)
  }

  // --- Loading / Error ---
  if (isCourseLoading || isLoading) {
    return (
      <div className="flex flex-col gap-4">
        <h1 className="text-2xl font-bold">Lehrgangsanmeldungen</h1>
        <p className="text-muted-foreground">Laden...</p>
      </div>
    )
  }

  if (isCourseError || isError) {
    return (
      <div className="flex flex-col gap-4">
        <h1 className="text-2xl font-bold">Lehrgangsanmeldungen</h1>
        <p className="text-destructive">Fehler beim Laden.</p>
      </div>
    )
  }

  // --- Render ---
  return (
    <div className="flex flex-col gap-4">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">Anmeldungen für {courseData?.name}</h1>
          <p className="text-muted-foreground">
            {courseData?.currentParticipants ?? 0} / {courseData?.maxParticipants} Plätze belegt
          </p>
        </div>
        <Button onClick={openCreate}>
          <Plus className="size-4" />
          Anmeldung erstellen
        </Button>
      </div>

      {/* Lehrgangsinformationen */}
      <div className="grid gap-2 border rounded-lg p-4">
        <div className="flex items-center gap-2">
          <span>📅</span>
          <span>{courseData?.startDate?.split("T")[0]} - {courseData?.endDate?.split("T")[0]}</span>
        </div>
        <div className="flex items-center gap-2">
          <span>📖</span>
          <span>{courseData?.description}</span>
        </div>
        <div className="flex items-center gap-2">
          <span>👥</span>
          <span>Lehrgangsleiter: {courseData?.instructorName ?? courseData?.instructorId}</span>
        </div>
      </div>

      {/* Tabelle */}
      {items.length === 0 ? (
        <p className="text-muted-foreground">Keine Anmeldungen vorhanden.</p>
      ) : (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Mitglied ID</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Erstellt am</TableHead>
              <TableHead className="w-[100px]">Aktionen</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {items.map((item) => (
              <TableRow key={item.id}>
                <TableCell className="font-medium">{item.memberId}</TableCell>
                <TableCell>
                  <Badge variant={STATUS_VARIANTS[item.status ?? ""] ?? "default"}>
                    {STATUS_LABELS[item.status ?? ""] ?? item.status}
                  </Badge>
                </TableCell>
                <TableCell>{item.createdAt}</TableCell>
                <TableCell>
                  <div className="flex items-center gap-1">
                    {item.status === "PENDING" && (
                      <Button variant="ghost" size="icon-sm" aria-label="Bestätigen">
                        <Check className="size-4" />
                      </Button>
                    )}
                    <Button variant="ghost" size="icon-sm" onClick={() => setCancelling(item)} aria-label="Stornieren">
                      <Trash2 className="size-4" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}

      {/* Create Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Anmeldung erstellen</DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="memberId">Mitglied ID</Label>
              <Input
                id="memberId"
                value={formState.memberId}
                onChange={(e) => setFormState((prev) => ({ ...prev, memberId: e.target.value }))}
                placeholder="Mitglieder-ID"
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="comment">Kommentar (optional)</Label>
              <Input
                id="comment"
                value={formState.comment}
                onChange={(e) => setFormState((prev) => ({ ...prev, comment: e.target.value }))}
                placeholder="Optionale Bemerkung"
              />
            </div>
          </div>
          <DialogFooter>
            <DialogClose render={<Button variant="outline" />}>Abbrechen</DialogClose>
            <Button onClick={handleSubmit} disabled={createMutation.isPending}>
              Erstellen
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Cancel AlertDialog */}
      <AlertDialog open={!!cancelling} onOpenChange={(open: boolean) => !open && setCancelling(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Anmeldung stornieren</AlertDialogTitle>
            <AlertDialogDescription>
              Möchten Sie die Anmeldung von Mitglied {cancelling?.memberId} wirklich stornieren?
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Abbrechen</AlertDialogCancel>
            <AlertDialogAction variant="destructive" onClick={handleCancel} disabled={cancelMutation.isPending}>
              Stornieren
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
