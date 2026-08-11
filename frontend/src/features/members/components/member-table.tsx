import { useState } from "react"
import { Link } from "react-router-dom"
import { Button } from "@/shared/components/ui/button"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/shared/components/ui/table"
import { Pagination } from "@/shared/components/ui/pagination"
import { useMembers } from "../hooks/use-members"
import { useLocations } from "@/features/locations/hooks/use-locations"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/shared/components/ui/select"

export function MemberTable() {
  const [page, setPage] = useState(0)
  const [size] = useState(10)
  const [selectedLocationId, setSelectedLocationId] = useState<string | undefined>(undefined)
  
  const membersQuery = useMembers(page, size, selectedLocationId)
  const locationsQuery = useLocations(0, 100)

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Mitglieder</h2>
        <div className="flex items-center gap-4">
          <Select
            value={selectedLocationId || ""}
            onValueChange={(value) => setSelectedLocationId(value || undefined)}
          >
            <SelectTrigger className="w-[200px]">
              <SelectValue placeholder="Alle Standorte" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="">Alle Standorte</SelectItem>
              {locationsQuery.data?.data?.map((location) => (
                <SelectItem key={location.id} value={location.id}>
                  {location.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>

      {membersQuery.isLoading ? (
        <div>Mitglieder werden geladen...</div>
      ) : membersQuery.error ? (
        <div className="text-destructive">
          Fehler beim Laden der Mitglieder: {membersQuery.error.message}
        </div>
      ) : (
        <div className="space-y-4">
          <div className="border rounded-lg overflow-hidden">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Name</TableHead>
                  <TableHead>E-Mail</TableHead>
                  <TableHead>Standort</TableHead>
                  <TableHead>Aktionen</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {membersQuery.data?.data?.map((member: any) => (
                  <TableRow key={member.id}>
                    <TableCell>{member.firstName} {member.lastName}</TableCell>
                    <TableCell>{member.email}</TableCell>
                    <TableCell>{member.location?.name || "-"}</TableCell>
                    <TableCell className="space-x-2">
                      <Link to={`/members/${member.id}`}>
                        <Button variant="outline" size="sm">
                          Bearbeiten
                        </Button>
                      </Link>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>

          <Pagination
            page={page}
            totalPages={membersQuery.data?.page?.totalPages || 0}
            onPageChange={setPage}
          />
        </div>
      )}
    </div>
  )
}
