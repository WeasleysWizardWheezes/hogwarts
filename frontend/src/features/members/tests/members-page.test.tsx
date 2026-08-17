import { render } from '@/test/render'
import { screen, within } from '@testing-library/react'
import type { MemberResponse } from '@/features/members/api/members-api'
import { useMembers, useAssignMemberToLocation } from '@/features/members/api/members-api'
import { useLocations } from '@/features/locations/api/locations-api'
import MembersPage from '@/features/members/pages/members-page'
import userEvent from '@testing-library/user-event'
import { vi } from 'vitest'

// Mocks
vi.mock('@/features/members/api/members-api')
vi.mock('@/features/locations/api/locations-api')

// Testdaten
const mockMembers: MemberResponse[] = [
  {
    id: 'mem-1',
    firstName: 'Hans',
    lastName: 'Müller',
    rank: 'Hauptfeuerwehrmann',
    unit: 'K-1',
    locations: [
      {
        id: 'loc-1',
        name: 'Feuerwache Köln',
        address: 'Kölner Straße 123',
        type: 'FIRE_STATION',
      },
    ],
  },
  {
    id: 'mem-2',
    firstName: 'Anna',
    lastName: 'Schmidt',
    rank: 'Feuerwehrfrau',
    unit: 'K-2',
    locations: [],
  },
]

const mockLocationsData = [
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

const mockAssignMemberToLocation = vi.fn()

// Tests
describe('MembersPage', () => {
  describe('Normaler Zustand', () => {
    beforeEach(() => {
      vi.mocked(useMembers).mockReturnValue({
        data: { data: mockMembers },
        isLoading: false,
        isError: false,
      })

      vi.mocked(useLocations).mockReturnValue({
        data: { data: mockLocationsData },
        isLoading: false,
        isError: false,
      })

      vi.mocked(useAssignMemberToLocation).mockReturnValue({
        mutateAsync: mockAssignMemberToLocation,
        isPending: false,
      })

      render(<MembersPage />)
    })

    it('zeigt den Seitenüberschrift korrekt', () => {
      expect(screen.getByText('Mitglieder')).toBeInTheDocument()
    })

    it('zeigt die Filter-Schaltfläche korrekt', () => {
      expect(screen.getByRole('combobox')).toBeInTheDocument()
    })

    it('zeigt die Tabelle mit Mitgliedern korrekt', () => {
      const table = screen.getByRole('table')
      const header = within(table).getByRole('columnheader', { name: 'Name' })
      expect(header).toBeInTheDocument()

      const rows = within(table).getAllByRole('row')
      expect(rows).toHaveLength(3) // 2 Datensätze + Header

      const firstRow = rows[1]
      expect(within(firstRow).getByText('Hans Müller')).toBeInTheDocument()
      expect(within(firstRow).getByText('Hauptfeuerwehrmann')).toBeInTheDocument()
      expect(within(firstRow).getByText('K-1')).toBeInTheDocument()
      expect(within(firstRow).getByText('Feuerwache Köln')).toBeInTheDocument()
    })

    it('zeigt die Aktionen für Zuweisen korrekt', () => {
      const firstRow = screen.getByRole('row', { name: /Hans Müller/ });
      const assignButton = within(firstRow).getByRole('button', { name: 'Standort zuweisen' });
      expect(assignButton).toBeInTheDocument();
    })

    it('öffnet das Dialogfenster beim Klicken auf "Standort zuweisen"', async () => {
      const firstRow = screen.getByRole('row', { name: /Hans Müller/ });
      const assignButton = within(firstRow).getByRole('button', { name: 'Standort zuweisen' });
      await userEvent.click(assignButton)

      expect(screen.getByRole('heading', { name: 'Standort zuweisen' })).toBeInTheDocument()
    })

    it('schließt das Dialogfenster beim Klicken auf "Abbrechen"', async () => {
      const firstRow = screen.getByRole('row', { name: /Hans Müller/ });
      const assignButton = within(firstRow).getByRole('button', { name: 'Standort zuweisen' });
      await userEvent.click(assignButton)

      const cancelButton = screen.getByRole('button', { name: 'Abbrechen' })
      await userEvent.click(cancelButton)

      expect(screen.queryByRole('heading', { name: 'Standort zuweisen' })).not.toBeInTheDocument()
    })

    it('ruft die Mutation mit korrekten Daten auf beim Zuweisen', async () => {
      const firstRow = screen.getByRole('row', { name: /Hans Müller/ });
      const assignButton = within(firstRow).getByRole('button', { name: 'Standort zuweisen' });
      await userEvent.click(assignButton)

      // Standort-Select im Dialog öffnen und Option wählen
      const dialogCombobox = screen.getByRole('combobox', { name: 'Standort' })
      await userEvent.click(dialogCombobox)
      await userEvent.click(await screen.findByRole('option', { name: 'Feuerwache Köln' }))

      // Zuweisen klicken
      const submitButton = screen.getByRole('button', { name: 'Zuweisen' })
      await userEvent.click(submitButton)

      expect(mockAssignMemberToLocation).toHaveBeenCalledWith({
        memberId: 'mem-1',
        body: { locationId: 'loc-1' },
      })
    })
  })

  describe('Ladezustand', () => {
    beforeEach(() => {
      vi.mocked(useMembers).mockReturnValue({
        data: { data: mockMembers },
        isLoading: true,
        isError: false,
      })

      vi.mocked(useLocations).mockReturnValue({
        data: { data: mockLocationsData },
        isLoading: true,
        isError: false,
      })

      render(<MembersPage />)
    })

    it('zeigt den Ladezustand korrekt', () => {
      expect(screen.getByText('Laden...')).toBeInTheDocument()
    })
  })

  describe('Fehlerzustand', () => {
    beforeEach(() => {
      vi.mocked(useMembers).mockReturnValue({
        data: { data: mockMembers },
        isLoading: false,
        isError: true,
      })

      vi.mocked(useLocations).mockReturnValue({
        data: { data: mockLocationsData },
        isLoading: false,
        isError: false,
      })

      render(<MembersPage />)
    })

    it('zeigt den Fehlerzustand korrekt', () => {
      expect(screen.getByText('Fehler beim Laden.')).toBeInTheDocument()
    })
  })

  describe('Leerer Zustand', () => {
    beforeEach(() => {
      vi.mocked(useMembers).mockReturnValue({
        data: { data: [] },
        isLoading: false,
        isError: false,
      })

      vi.mocked(useLocations).mockReturnValue({
        data: { data: mockLocationsData },
        isLoading: false,
        isError: false,
      })

      render(<MembersPage />)
    })

    it('zeigt "Keine Mitglieder vorhanden" wenn keine Mitglieder vorhanden sind', () => {
      expect(screen.getByText('Keine Mitglieder vorhanden.')).toBeInTheDocument()
    })
  })
})