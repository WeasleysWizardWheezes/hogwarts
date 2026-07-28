import { useState } from "react"
import { useVehicles, useDeleteVehicle } from "../hooks/use-vehicles"
import { useVehicleGroups } from "../hooks/use-vehicle-groups"
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
import { VehicleForm } from "./VehicleForm"

interface Vehicle {
  id: string
  name: string
  licensePlate: string
  type: string
  status: string
  vehicleGroupId?: string
}

interface VehicleGroup {
  id: string
  name: string
  description?: string
}

export function VehicleList() {
  const { data: vehicles, isLoading } = useVehicles()
  const { data: vehicleGroups } = useVehicleGroups()
  const deleteVehicleMutation = useDeleteVehicle()
  const [searchTerm, setSearchTerm] = useState("")
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [editingVehicle, setEditingVehicle] = useState<Vehicle | null>(null)

  const filteredVehicles = (vehicles?.data as Vehicle[])?.filter((vehicle) =>
    vehicle.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    vehicle.licensePlate.toLowerCase().includes(searchTerm.toLowerCase())
  )

  const handleDelete = (id: string) => {
    if (confirm("Are you sure you want to delete this vehicle?")) {
      deleteVehicleMutation.mutate(id)
    }
  }

  const handleEdit = (vehicle: any) => {
    setEditingVehicle(vehicle)
    setIsDialogOpen(true)
  }

  const getVehicleGroupName = (groupId: string) => {
    return (vehicleGroups?.data as VehicleGroup[])?.find((group) => group.id === groupId)?.name || "N/A"
  }

  return (
    <div className="container mx-auto py-8">
      <h1 className="text-2xl font-bold mb-6">Vehicle Management</h1>

      <div className="flex justify-between items-center mb-4">
        <Input
          type="text"
          placeholder="Search vehicles..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="max-w-sm"
        />

        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger>
            <Button>Add Vehicle</Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-[600px]">
            <DialogHeader>
              <DialogTitle>
                {editingVehicle ? "Edit Vehicle" : "Add Vehicle"}
              </DialogTitle>
            </DialogHeader>
            <VehicleForm
              vehicle={editingVehicle}
              vehicleGroups={vehicleGroups || []}
              onSuccess={() => {
                setIsDialogOpen(false)
                setEditingVehicle(null)
              }}
            />
          </DialogContent>
        </Dialog>
      </div>

      {isLoading ? (
        <div className="text-center py-8">Loading vehicles...</div>
      ) : (
        <div className="border rounded-lg overflow-hidden">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>License Plate</TableHead>
                <TableHead>Type</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Group</TableHead>
                <TableHead>Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredVehicles?.length ? (
                filteredVehicles.map((vehicle) => (
                  <TableRow key={vehicle.id}>
                    <TableCell>{vehicle.name}</TableCell>
                    <TableCell>{vehicle.licensePlate}</TableCell>
                    <TableCell>{vehicle.type}</TableCell>
                    <TableCell>{vehicle.status}</TableCell>
                    <TableCell>{getVehicleGroupName(vehicle.vehicleGroupId)}</TableCell>
                    <TableCell className="flex gap-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleEdit(vehicle)}
                      >
                        Edit
                      </Button>
                      <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => handleDelete(vehicle.id)}
                      >
                        Delete
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={6} className="text-center py-4">
                    No vehicles found
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