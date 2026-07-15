import { Input } from "@/shared/components/ui/input"

interface EquipmentFilterPanelProps {
  searchValue: string
  onSearchChange: (value: string) => void
  locationValue: string
  onLocationChange: (value: string) => void
}

export function EquipmentFilterPanel({
  searchValue,
  onSearchChange,
  locationValue,
  onLocationChange,
}: EquipmentFilterPanelProps) {
  return (
    <div className="flex flex-col sm:flex-row gap-4 mb-4">
      <div className="flex-1">
        <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
          Suche (Name/Seriennummer)
        </label>
        <Input
          placeholder="Gerätename oder Seriennummer..."
          value={searchValue}
          onChange={(e) => onSearchChange(e.target.value)}
          className="mt-1"
        />
      </div>
      <div className="flex-1">
        <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
          Lagerort
        </label>
        <Input
          placeholder="Lagerort filtern..."
          value={locationValue}
          onChange={(e) => onLocationChange(e.target.value)}
          className="mt-1"
        />
      </div>
    </div>
  )
}
