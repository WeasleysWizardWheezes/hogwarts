import { render } from '@/test/render'
import { screen, within } from '@testing-library/react'
import type { LocationResponse } from '@/features/locations/api/locations-api'
import { useLocations, useCreateLocation, useUpdateLocation, useDeleteLocation } from '@/features/locations/api/locations-api'
import LocationsPage from '@/features/locations/pages/locations-page'
import userEvent from '@testing-library/user-event'
import { vi } from 'vitest'

// Mocks
vi.mock('@/features/locations/api/locations-api')

// Testdaten
const mockLocations: LocationResponse[] = [
  {
    id: 'loc-1',
    name: 'Feuerwache Köln',
    address: 'Kölner Straße 123',
    type: 'FIRE_STATION',
  },
  {
    id: 'loc-2',
    name: 'Gerätedepot Bonn',
    address: 'Bonnstraße 456',
    type: 'EQUIPMENT_DEPOT',
  },
]

const mockCreateLocation = vi.fn()
const mockUpdateLocation = vi.fn()
const mockDeleteLocation = vi.fn()

// Setup
const setup = () => {
  // Mocks
  vi.mocked(useLocations).mockReturnValue({
    data: { data: mockLocations },
    isLoading: false,
    isError: false,
  })

  vi.mocked(useCreateLocation).mockReturnValue({
    mutateAsync: mockCreateLocation,
    isPending: false,
  })

  vi.mocked(useUpdateLocation).mockReturnValue({
    mutateAsync: mockUpdateLocation,
    isPending: false,
  })

  vi.mocked(useDeleteLocation).mockReturnValue({
    mutateAsync: mockDeleteLocation,
    isPending: false,
  })

  render(<LocationsPage />)
}

// Tests
describe('LocationsPage', () => {
  beforeEach(() => {
    setup()
  })

  it('zeigt den Seitenüberschrift korrekt', () => {
    expect(screen.getByText('Standorte')).toBeInTheDocument()
  })

  it('zeigt die Schaltfläche zum Erstellen korrekt', () => {
    expect(screen.getByRole('button', { name: 'Erstellen' })).toBeInTheDocument()
  })

  it('zeigt die Tabelle mit Standorten korrekt', () => {
    const table = screen.getByRole('table')
    const header = within(table).getByRole('columnheader', { name: 'Name' })
    expect(header).toBeInTheDocument()

    const rows = within(table).getAllByRole('row')
    expect(rows).toHaveLength(3) // 2 Datensätze + Header

    const firstRow = rows[1]
    expect(within(firstRow).getByText('Feuerwache Köln')).toBeInTheDocument()
    expect(within(firstRow).getByText('Kölner Straße 123')).toBeInTheDocument()
    expect(within(firstRow).getByText('Feuerwache')).toBeInTheDocument()
  })

  it('zeigt die Aktionen für Bearbeiten und Löschen korrekt', () => {
    const firstRow = screen.getAllByRole('row')[1]
    const editButton = within(firstRow).getByRole('button', { name: 'Bearbeiten' })
    const deleteButton = within(firstRow).getByRole('button', { name: 'Löschen' })

    expect(editButton).toBeInTheDocument()
    expect(deleteButton).toBeInTheDocument()
  })

  it('öffnet das Dialogfenster beim Klicken auf "Erstellen"', async () => {
    const createButton = screen.getByRole('button', { name: 'Erstellen' })
    await userEvent.click(createButton)

    expect(screen.getByText('Standort erstellen')).toBeInTheDocument()
  })

  it('öffnet das Dialogfenster beim Klicken auf "Bearbeiten"', async () => {
    const editButton = screen.getAllByRole('button', { name: 'Bearbeiten' })[0]
    await userEvent.click(editButton)

    expect(screen.getByText('Standort bearbeiten')).toBeInTheDocument()
  })

  it('öffnet das AlertDialog beim Klicken auf "Löschen"', async () => {
    const firstRow = screen.getAllByRole('row')[1]
    const deleteButton = within(firstRow).getByRole('button', { name: 'Löschen' })
    await userEvent.click(deleteButton)

    // Warten, bis der Alert-Dialog erscheint
    expect(await screen.findByRole('heading', { name: 'Standort löschen' })).toBeInTheDocument()
    const dialog = screen.getByRole('alertdialog')
    expect(within(dialog).getByText(/Feuerwache Köln/)).toBeInTheDocument()
    expect(within(dialog).getByText(/wirklich löschen/)).toBeInTheDocument()
  })

  it('schließt das Dialogfenster beim Klicken auf "Abbrechen"', async () => {
    const createButton = screen.getByRole('button', { name: 'Erstellen' })
    await userEvent.click(createButton)

    const cancelButton = screen.getByRole('button', { name: 'Abbrechen' })
    await userEvent.click(cancelButton)

    expect(screen.queryByText('Standort erstellen')).not.toBeInTheDocument()
  })

  it('zeigt den Ladezustand korrekt', () => {
    vi.mocked(useLocations).mockReturnValue({
      data: { data: mockLocations },
      isLoading: true,
      isError: false,
    })

    render(<LocationsPage />)

    expect(screen.getByText('Laden...')).toBeInTheDocument()
  })

  it('zeigt den Fehlerzustand korrekt', () => {
    vi.mocked(useLocations).mockReturnValue({
      data: { data: mockLocations },
      isLoading: false,
      isError: true,
    })

    render(<LocationsPage />)

    expect(screen.getByText('Fehler beim Laden.')).toBeInTheDocument()
  })

  it('zeigt "Keine Einträge vorhanden" wenn keine Standorte vorhanden sind', () => {
    vi.mocked(useLocations).mockReturnValue({
      data: { data: [] },
      isLoading: false,
      isError: false,
    })

    render(<LocationsPage />)

    expect(screen.getByText('Keine Einträge vorhanden.')).toBeInTheDocument()
  })
})