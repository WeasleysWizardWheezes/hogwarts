import { useState } from "react"
import { useVehicleGroups, useDeleteVehicleGroup } from "../hooks/use-vehicle-groups"
import { Button } from "@/shared/components/ui/button"
import { Input } from "@/shared/components/ui/input"
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
  DialogTrigger,
} from "@/shared/components/ui/dialog"
import { VehicleGroupForm } from "./VehicleGroupForm"

interface VehicleGroup {
  id: string
  name: string
  description?: string
}

export function VehicleGroupList() {
  const { data: vehicleGroups, isLoading } = useVehicleGroups()
  const deleteVehicleGroupMutation = useDeleteVehicleGroup()
  const [searchTerm, setSearchTerm] = useState("")
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [editingGroup, setEditingGroup] = useState(null)

  const filteredGroups = (vehicleGroups?.data as VehicleGroup[])?.filter((group) =>
    group.name.toLowerCase().includes(searchTerm.toLowerCase())
  )

  const handleDelete = (id: string) => {
    if (confirm("Are you sure you want to delete this vehicle group?")) {
      deleteVehicleGroupMutation.mutate(id)
    }
  }

  const handleEdit = (group: any) => {
    setEditingGroup(group)
    setIsDialogOpen(true)
  }

  return (
    <div className="container mx-auto py-8">
      <h1 className="text-2xl font-bold mb-6">Vehicle Group Management</h1>

      <div className="flex justify-between items-center mb-4">
        <Input
          type="text"
          placeholder="Search vehicle groups..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="max-w-sm"
        />

        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger>
            <Button>Add Vehicle Group</Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-[600px]">
            <DialogHeader>
              <DialogTitle>
                {editingGroup ? "Edit Vehicle Group" : "Add Vehicle Group"}
              </DialogTitle>
            </DialogHeader>
            <VehicleGroupForm
              group={editingGroup}
              onSuccess={() => {
                setIsDialogOpen(false)
                setEditingGroup(null)
              }}
            />
          </DialogContent>
        </Dialog>
      </div>

      {isLoading ? (
        <div className="text-center py-8">Fahrzeuggruppen werden geladen...</div>
      ) : (
        <div className="border rounded-lg overflow-hidden">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>Description</TableHead>
                <TableHead>Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredGroups?.length ? (
                filteredGroups.map((group) => (
                  <TableRow key={group.id}>
                    <TableCell>{group.name}</TableCell>
                    <TableCell>{group.description || "-"}</TableCell>
                    <TableCell className="flex gap-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleEdit(group)}
                      >
                        Edit
                      </Button>
                      <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => handleDelete(group.id)}
                      >
                        Delete
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={3} className="text-center py-4">
                    No vehicle groups found
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </div>
      )}
    </div>
  )
}