import { useState } from "react"
import { MapPin } from "lucide-react"
import { Button } from "@/shared/components/ui/button"
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/shared/components/ui/table"
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogClose,
} from "@/shared/components/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/shared/components/ui/select"
import { Label } from "@/shared/components/ui/label"
import { Badge } from "@/shared/components/ui/badge"
import { Input } from "@/shared/components/ui/input"
import { useMembers, useAssignMemberToLocation } from "../api/members-api"
import { useLocations } from "@/features/locations/api/locations-api"
import type { MemberResponse } from "../api/members-api"
import type { LocationResponse } from "@/features/locations/api/locations-api"

interface FormState {
  memberId: string
  locationId: string
}

const EMPTY_FORM: FormState = {
  memberId: "",
  locationId: "",
}

export default function MembersPage() {
  const { data: membersData, isLoading: isMembersLoading, isError: isMembersError } = useMembers()
  const { data: locationsData, isLoading: isLocationsLoading, isError: isLocationsError } = useLocations()
  const assignMutation = useAssignMemberToLocation()

  const [dialogOpen, setDialogOpen] = useState(false)
  const [selectedMember, setSelectedMember] = useState<MemberResponse | null>(null)
  const [formState, setFormState] = useState<FormState>(EMPTY_FORM)

  const members: MemberResponse[] = membersData?.data ?? []
  const locations: LocationResponse[] = locationsData?.data ?? []

  function openAssign(member: MemberResponse) {
    setSelectedMember(member)
    setFormState({
      memberId: member.id ?? "",
      locationId: "",
    })
    setDialogOpen(true)
  }

  async function handleSubmit() {
    if (!selectedMember) return
    await assignMutation.mutateAsync({ 
      memberId: selectedMember.id!,
      body: { locationId: formState.locationId }
    })
    setDialogOpen(false)
  }

  // --- Loading / Error ---
  if (isMembersLoading || isLocationsLoading) {
    return (
      <div className="flex flex-col gap-4">
        <h1 className="text-2xl font-bold">Mitglieder</h1>
        <p className="text-muted-foreground">Laden...</p>
      </div>
    )
  }

  if (isMembersError || isLocationsError) {
    return (
      <div className="flex flex-col gap-4">
        <h1 className="text-2xl font-bold">Mitglieder</h1>
        <p className="text-destructive">Fehler beim Laden.</p>
      </div>
    )
  }

  // --- Render ---
  return (
    <div className="flex flex-col gap-4">
      {/* Page Header */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Mitglieder</h1>
      </div>

      {/* Filter */}
      <div className="flex items-center gap-2">
        <Select value={formState.locationId} onValueChange={(value) => setFormState((prev) => ({ ...prev, locationId: value ?? "" }))}>
          <SelectTrigger className="w-[200px]">
            <SelectValue placeholder="Alle Standorte" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="">Alle Standorte</SelectItem>
            {locations.map((loc) => (
              <SelectItem key={loc.id} value={loc.id!}>{loc.name}</SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {/* Tabelle */}
      {members.length === 0 ? (
        <p className="text-muted-foreground">Keine Mitglieder vorhanden.</p>
      ) : (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Dienstgrad</TableHead>
              <TableHead>Einheit</TableHead>
              <TableHead>Standort</TableHead>
              <TableHead className="w-[100px]">Aktionen</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {members.map((member) => (
              <TableRow key={member.id}>
                <TableCell className="font-medium">{member.firstName} {member.lastName}</TableCell>
                <TableCell>{member.rank}</TableCell>
                <TableCell>{member.unit}</TableCell>
                <TableCell>
                  {member.locations && member.locations.length > 0 ? (
                    <div className="flex flex-wrap gap-1">
                      {member.locations.map((loc) => (
                        <Badge key={loc.id} variant="secondary">
                          {loc.name}
                        </Badge>
                      ))}
                    </div>
                  ) : (
                    <span className="text-muted-foreground">Kein Standort</span>
                  )}
                </TableCell>
                <TableCell>
                  <div className="flex items-center gap-1">
                    <Button variant="ghost" size="icon-sm" onClick={() => openAssign(member)} aria-label="Standort zuweisen">
                      <MapPin className="size-4" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}

      {/* Assign Location Dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Standort zuweisen</DialogTitle>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="member">Mitglied</Label>
              <Input
                id="member"
                value={`${selectedMember?.firstName ?? ""} ${selectedMember?.lastName ?? ""}`}
                readOnly
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="location">Standort</Label>
              <Select
                value={formState.locationId}
                onValueChange={(value) => {
                  if (value) setFormState((prev) => ({ ...prev, locationId: value }))
                }}
              >
                <SelectTrigger id="location">
                  <SelectValue placeholder="Standort auswählen" />
                </SelectTrigger>
                <SelectContent>
                  {locations.map((loc) => (
                    <SelectItem key={loc.id} value={loc.id!}>{loc.name}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <DialogClose render={<Button variant="outline" />}>Abbrechen</DialogClose>
            <Button onClick={handleSubmit} disabled={assignMutation.isPending}>
              Zuweisen
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}