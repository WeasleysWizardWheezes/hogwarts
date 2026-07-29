import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/shared/components/ui/tabs"
import { VehicleList } from "../components/VehicleList"
import { VehicleGroupList } from "../components/VehicleGroupList"

export default function VehicleManagementPage() {

  return (
    <div className="container mx-auto py-8">
      <h1 className="text-3xl font-bold mb-8">Fahrzeugverwaltung</h1>

      <Tabs defaultValue="vehicles" className="w-full">
        <TabsList className="mb-6">
          <TabsTrigger value="vehicles">Fahrzeuge</TabsTrigger>
          <TabsTrigger value="groups">Fahrzeuggruppen</TabsTrigger>
        </TabsList>

        <TabsContent value="vehicles">
          <VehicleList />
        </TabsContent>

        <TabsContent value="groups">
          <VehicleGroupList />
        </TabsContent>
      </Tabs>
    </div>
  )
}