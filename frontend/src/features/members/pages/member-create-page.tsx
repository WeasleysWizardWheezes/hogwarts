import { useNavigate } from "react-router-dom"
import { MemberForm } from "../components/member-form"
import { MemberLocationAssignment } from "@/features/locations/components/member-location-assignment"
import { useCreateMember } from "../hooks/use-create-member"
import { Button } from "@/shared/components/ui/button"
import { ArrowLeft } from "lucide-react"
import type { CreateMemberRequest } from "../types"

export default function MemberCreatePage() {
  const navigate = useNavigate()
  const createMutation = useCreateMember()

  const handleSubmit = (data: any) => {
    createMutation.mutate(data, {
      onSuccess: () => {
        navigate("/members")
      },
    })
  }

  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold">Mitglied erstellen</h1>
          <p className="text-muted-foreground">
            Erstelle ein neues Feuerwehrmitglied.
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
        <MemberForm onSubmit={handleSubmit} isLoading={createMutation.isPending} />

        <div className="border-t pt-8">
          <MemberLocationAssignment memberId="" />
        </div>
      </div>
    </div>
  )
}
