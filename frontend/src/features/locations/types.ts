import type { components } from "@weasleyswizardwheezes/hogwarts-api-client"

export type Location = components["schemas"]["LocationResponse"]
export type CreateLocationRequest = components["schemas"]["CreateLocationRequest"]
export type UpdateLocationRequest = components["schemas"]["UpdateLocationRequest"]

export type LocationType = "FIRE_STATION" | "EQUIPMENT_DEPOT" | "TRAINING_CENTER"
