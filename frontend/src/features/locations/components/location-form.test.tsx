import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { LocationForm } from "./location-form"

describe("LocationForm", () => {
  const mockSubmit = vi.fn()

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it("sollte das Formular mit leeren Feldern rendern", () => {
    render(
      <LocationForm 
        initialValues={undefined} 
        onSubmit={mockSubmit}
      />
    )

    expect(screen.getByRole("textbox", { name: "Name *" })).toBeInTheDocument()
    expect(screen.getByRole("textbox", { name: "Adresse" })).toBeInTheDocument()
    expect(screen.getByRole("combobox", { name: "Typ *" })).toBeInTheDocument()
    expect(screen.getByRole("button", { name: "Speichern" })).toBeInTheDocument()
  })

  it("sollte Validierungsfehler anzeigen, wenn Name fehlt", async () => {
    const user = userEvent.setup()

    render(
      <LocationForm 
        initialValues={undefined} 
        onSubmit={mockSubmit}
      />
    )

    const submitButton = screen.getByRole("button", { name: "Speichern" })
    await user.click(submitButton)

    expect(await screen.findByText("Der Name ist erforderlich")).toBeInTheDocument()
  })

  it("sollte Validierungsfehler anzeigen, wenn Typ fehlt", async () => {
    const user = userEvent.setup()

    render(
      <LocationForm 
        initialValues={undefined} 
        onSubmit={mockSubmit}
      />
    )

    await user.type(screen.getByRole("textbox", { name: "Name *" }), "Hauptwache")
    const submitButton = screen.getByRole("button", { name: "Speichern" })
    await user.click(submitButton)

    expect(await screen.findByText("Der Typ ist erforderlich")).toBeInTheDocument()
  })

  it("sollte onSubmit aufrufen, wenn das Formular gültig ist", async () => {
    const user = userEvent.setup()

    render(
      <LocationForm 
        initialValues={undefined} 
        onSubmit={mockSubmit}
      />
    )

    await user.type(screen.getByRole("textbox", { name: "Name *" }), "Hauptwache")
    await user.type(screen.getByRole("textbox", { name: "Adresse" }), "Hauptstraße 1")

    const typeSelect = screen.getByRole("combobox", { name: "Typ *" })
    await user.click(typeSelect)
    await user.click(screen.getByRole("option", { name: "Feuerwache" }))

    const submitButton = screen.getByRole("button", { name: "Speichern" })
    await user.click(submitButton)

    expect(mockSubmit).toHaveBeenCalledWith({
      name: "Hauptwache",
      address: "Hauptstraße 1",
      type: "FIRE_STATION"
    })
  })

  it("sollte den Speichern-Button deaktivieren, wenn isLoading true ist", () => {
    render(
      <LocationForm 
        initialValues={undefined} 
        onSubmit={mockSubmit}
        isLoading={true}
      />
    )

    expect(screen.getByRole("button", { name: "Wird gespeichert..." })).toBeDisabled()
  })

  it("sollte das Formular mit Initialwerten füllen", () => {
    render(
      <LocationForm 
        initialValues={{ 
          name: "Hauptwache",
          address: "Hauptstraße 1",
          type: "FIRE_STATION"
        }} 
        onSubmit={mockSubmit}
      />
    )

    expect(screen.getByRole("textbox", { name: "Name *" })).toHaveValue("Hauptwache")
    expect(screen.getByRole("textbox", { name: "Adresse" })).toHaveValue("Hauptstraße 1")
    expect(screen.getByRole("combobox", { name: "Typ *" })).toHaveValue("FIRE_STATION")
  })
})
