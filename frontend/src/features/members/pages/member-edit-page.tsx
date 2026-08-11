import { useParams, useNavigate } from "react-router-dom"
import { MemberForm } from "../components/member-form"
import { MemberLocationAssignment } from "@/features/locations/components/member-location-assignment"
import { useMember } from "../hooks/use-member"
import { useUpdateMember } from "../hooks/use-update-member"
import { Button } from "@/shared/components/ui/button"
import { ArrowLeft } from "lucide-react"

export function MemberEditPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  
  const memberQuery = useMember(id || "")
  const updateMutation = useUpdateMember()

  const handleSubmit = (data: any) => {
    if (id) {
      updateMutation.mutate(
        { id, body: data },
        {
          onSuccess: () => {
            navigate("/members")
          },
        }
      )
    }
  }

  if (memberQuery.isLoading) {
    return <div>Mitglied wird geladen...</div>
  }

  if (memberQuery.error) {
    return (
      <div className="text-destructive">
        Fehler beim Laden des Mitglieds: {memberQuery.error.message}
      </div>
    )
  }

  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold">Mitglied bearbeiten</h1>
          <p className="text-muted-foreground">
            Bearbeite die Mitgliedinformationen.
          </p>
        </div>
        <Button
          variant="outline"
          size="icon"
          onClick={() => navigate("/members")}
        >
          <ArrowLeft className="h-4 w-4" />
        </Button>
      </div>

      <div className="max-w-2xl space-y-8">
        <MemberForm
          initialValues={memberQuery.data}
          onSubmit={handleSubmit}
          isLoading={updateMutation.isPending}
        />

        <div className="border-t pt-8">
          <MemberLocationAssignment
            memberId={id || ""}
            currentLocationId={memberQuery.data?.location?.id}
          />
        </div>
      </div>
    </div>
  )
}
