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
  year?: number
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
              vehicleGroups={vehicleGroups?.data || []}
              onSuccess={() => {
                setIsDialogOpen(false)
                setEditingVehicle(null)
              }}
            />
          </DialogContent>
        </Dialog>
      </div>

      {isLoading ? (
        <div className="text-center py-8">Fahrzeuge werden geladen...</div>
      ) : (
        <div className="border rounded-lg overflow-hidden">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>Funkrufname</TableHead>
                <TableHead>Kennzeichen</TableHead>
                <TableHead>Baujahr</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Fahrzeuggruppe</TableHead>
                <TableHead>Aktionen</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredVehicles?.length ? (
                filteredVehicles.map((vehicle) => (
                  <TableRow key={vehicle.id}>
                    <TableCell>{vehicle.name}</TableCell>
                    <TableCell>{vehicle.licensePlate}</TableCell>
                    <TableCell>{vehicle.type}</TableCell>
                    <TableCell>{vehicle.year || "-"}</TableCell>
                    <TableCell>
                      <span className={`px-2 py-1 rounded-full text-xs ${
                        vehicle.status === "VERFUEGBAR" ? "bg-green-100 text-green-800" :
                        vehicle.status === "IM_EINSATZ" ? "bg-blue-100 text-blue-800" :
                        vehicle.status === "WARTUNG_REPARATUR" ? "bg-yellow-100 text-yellow-800" :
                        "bg-red-100 text-red-800"
                      }`}>
                        {vehicle.status === "VERFUEGBAR" ? "Verfügbar" :
                         vehicle.status === "IM_EINSATZ" ? "Im Einsatz" :
                         vehicle.status === "WARTUNG_REPARATUR" ? "Wartung/Reparatur" :
                         "Defekt"}
                      </span>
                    </TableCell>
                    <TableCell>{getVehicleGroupName(vehicle.vehicleGroupId)}</TableCell>
                    <TableCell className="flex gap-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleEdit(vehicle)}
                      >
                        Bearbeiten
                      </Button>
                      <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => handleDelete(vehicle.id)}
                      >
                        Löschen
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={7} className="text-center py-4">
                    Keine Fahrzeuge gefunden
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