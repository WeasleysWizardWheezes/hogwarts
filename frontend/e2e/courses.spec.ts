import { test, expect } from "@playwright/test"

test.describe("Courses Management E2E Tests", () => {
  test.beforeEach(async ({ page }) => {
    // Mock API responses for the tests
    await page.route("/api/v1/courses", async (route) => {
      const json = {
        data: [
          {
            id: "1",
            name: "Atemschutzgeräteträger",
            description: "Grundausbildung Atemschutz",
            maxParticipants: 20,
            currentParticipants: 15,
            startDate: "2026-09-01T00:00:00Z",
            endDate: "2026-09-05T00:00:00Z",
            instructorId: "instructor-1",
            instructorName: "Max Mustermann",
            status: "CONFIRMED",
          },
        ],
        page: 0,
        size: 20,
        totalElements: 1,
        totalPages: 1,
      }
      await route.fulfill({ json })
    })

    await page.route("/api/v1/courses/1", async (route) => {
      const json = {
        id: "1",
        name: "Atemschutzgeräteträger",
        description: "Grundausbildung Atemschutz",
        maxParticipants: 20,
        currentParticipants: 15,
        startDate: "2026-09-01T00:00:00Z",
        endDate: "2026-09-05T00:00:00Z",
        instructorId: "instructor-1",
        instructorName: "Max Mustermann",
        status: "CONFIRMED",
      }
      await route.fulfill({ json })
    })

    await page.route("/api/v1/courses/1/enrollments", async (route) => {
      const json = {
        data: [
          {
            id: "enrollment-1",
            memberId: "member-1",
            status: "CONFIRMED",
            createdAt: "2026-08-01T10:00:00Z",
          },
        ],
        page: 0,
        size: 20,
        totalElements: 1,
        totalPages: 1,
      }
      await route.fulfill({ json })
    })

    // Post requests
    await page.route("/api/v1/courses", async (route) => {
      if (route.request().method() === "POST") {
        const json = {
          id: "2",
          name: "Neuer Lehrgang",
          description: "Testbeschreibung",
          maxParticipants: 10,
          currentParticipants: 0,
          startDate: "2026-10-01T00:00:00Z",
          endDate: "2026-10-05T00:00:00Z",
          instructorId: "instructor-2",
          instructorName: "Test Leiter",
          status: "PENDING",
        }
        await route.fulfill({ json })
      }
    })

    await page.route("/api/v1/courses/1/enrollments", async (route) => {
      if (route.request().method() === "POST") {
        const json = {
          id: "enrollment-2",
          memberId: "member-2",
          status: "CONFIRMED",
          createdAt: "2026-08-15T10:00:00Z",
        }
        await route.fulfill({ json })
      }
    })

    // Put requests
    await page.route("/api/v1/courses/1", async (route) => {
      if (route.request().method() === "PUT") {
        const json = {
          id: "1",
          name: "Atemschutzgeräteträger (aktualisiert)",
          description: "Grundausbildung Atemschutz",
          maxParticipants: 20,
          currentParticipants: 15,
          startDate: "2026-09-01T00:00:00Z",
          endDate: "2026-09-05T00:00:00Z",
          instructorId: "instructor-1",
          instructorName: "Max Mustermann",
          status: "CONFIRMED",
        }
        await route.fulfill({ json })
      }
    })

    // Delete requests
    await page.route("/api/v1/courses/1", async (route) => {
      if (route.request().method() === "DELETE") {
        await route.fulfill({ status: 204 })
      }
    })

    await page.route("/api/v1/courses/1/enrollments/enrollment-1", async (route) => {
      if (route.request().method() === "DELETE") {
        await route.fulfill({ status: 204 })
      }
    })

    // Navigate to courses page
    await page.goto("/courses")
  })

  test("should display courses list", async ({ page }) => {
    await expect(page.getByText("Lehrgänge")).toBeVisible()
    await expect(page.getByText("Atemschutzgeräteträger")).toBeVisible()
    await expect(page.getByText("Grundausbildung Atemschutz")).toBeVisible()
    await expect(page.getByText("Max Mustermann")).toBeVisible()
    await expect(page.getByText("15 / 20")).toBeVisible()
    await expect(page.getByText("01.09.2026 - 05.09.2026")).toBeVisible()
    await expect(page.getByText("Bestätigt")).toBeVisible()
  })

  test("should open create course dialog", async ({ page }) => {
    await page.getByRole("button", { name: "Lehrgang erstellen" }).click()
    await expect(page.getByRole("dialog", { name: "Lehrgang erstellen" })).toBeVisible()
    await expect(page.getByLabel("Name")).toBeVisible()
    await expect(page.getByLabel("Beschreibung")).toBeVisible()
    await expect(page.getByLabel("Maximale Teilnehmer")).toBeVisible()
  })

  test("should create a new course", async ({ page }) => {
    await page.getByRole("button", { name: "Lehrgang erstellen" }).click()

    await page.getByLabel("Name").fill("Neuer Lehrgang")
    await page.getByLabel("Beschreibung").fill("Testbeschreibung")
    await page.getByLabel("Maximale Teilnehmer").fill("10")
    await page.getByLabel("Startdatum").fill("2026-10-01")
    await page.getByLabel("Enddatum").fill("2026-10-05")
    await page.getByLabel("Lehrgangsleiter ID").fill("instructor-2")

    await page.getByRole("button", { name: "Erstellen" }).click()

    // Wait for the dialog to close and the new course to appear
    await expect(page.getByRole("dialog")).not.toBeVisible()
    await expect(page.getByText("Neuer Lehrgang")).toBeVisible()
  })

  test("should open edit course dialog", async ({ page }) => {
    const editButtons = page.getByRole("button", { name: "Bearbeiten" })
    await editButtons.first().click()

    await expect(page.getByRole("dialog", { name: "Lehrgang bearbeiten" })).toBeVisible()
    await expect(page.getByLabel("Name")).toHaveValue("Atemschutzgeräteträger")
  })

  test("should update a course", async ({ page }) => {
    const editButtons = page.getByRole("button", { name: "Bearbeiten" })
    await editButtons.first().click()

    await page.getByLabel("Name").fill("Atemschutzgeräteträger (aktualisiert)")
    await page.getByRole("button", { name: "Speichern" }).click()

    await expect(page.getByRole("dialog")).not.toBeVisible()
    await expect(page.getByText("Atemschutzgeräteträger (aktualisiert)")).toBeVisible()
  })

  test("should open delete confirmation and delete course", async ({ page }) => {
    const deleteButtons = page.getByRole("button", { name: "Löschen" })
    await deleteButtons.first().click()

    await expect(page.getByRole("dialog", { name: "Lehrgang löschen" })).toBeVisible()
    await expect(page.getByText('Möchten Sie "Atemschutzgeräteträger" wirklich löschen?')).toBeVisible()

    await page.getByRole("button", { name: "Löschen" }).click()

    await expect(page.getByRole("dialog")).not.toBeVisible()
    await expect(page.getByText("Keine Lehrgänge vorhanden.")).toBeVisible()
  })

  test("should navigate to course enrollments page", async ({ page }) => {
    const enrollmentsButtons = page.getByRole("button", { name: "Anmeldungen" })
    await enrollmentsButtons.first().click()

    await expect(page).toHaveURL("/courses/1/enrollments")
    await expect(page.getByText("Anmeldungen für Atemschutzgeräteträger")).toBeVisible()
    await expect(page.getByText("15 / 20 Plätze belegt")).toBeVisible()
  })
})

test.describe("Course Enrollments E2E Tests", () => {
  test.beforeEach(async ({ page }) => {
    // Mock API responses for enrollments tests
    await page.route("/api/v1/courses/1", async (route) => {
      const json = {
        id: "1",
        name: "Atemschutzgeräteträger",
        description: "Grundausbildung Atemschutz",
        maxParticipants: 20,
        currentParticipants: 15,
        startDate: "2026-09-01T00:00:00Z",
        endDate: "2026-09-05T00:00:00Z",
        instructorId: "instructor-1",
        instructorName: "Max Mustermann",
        status: "CONFIRMED",
      }
      await route.fulfill({ json })
    })

    await page.route("/api/v1/courses/1/enrollments", async (route) => {
      if (route.request().method() === "GET") {
        const json = {
          data: [
            {
              id: "enrollment-1",
              memberId: "member-1",
              status: "CONFIRMED",
              createdAt: "2026-08-01T10:00:00Z",
            },
            {
              id: "enrollment-2",
              memberId: "member-2",
              status: "WAITING_LIST",
              createdAt: "2026-08-02T11:00:00Z",
            },
          ],
          page: 0,
          size: 20,
          totalElements: 2,
          totalPages: 1,
        }
        await route.fulfill({ json })
      } else if (route.request().method() === "POST") {
        const json = {
          id: "enrollment-3",
          memberId: "member-3",
          status: "CONFIRMED",
          createdAt: "2026-08-15T10:00:00Z",
        }
        await route.fulfill({ json })
      }
    })

    await page.route("/api/v1/courses/1/enrollments/enrollment-1", async (route) => {
      if (route.request().method() === "DELETE") {
        await route.fulfill({ status: 204 })
      }
    })

    // Navigate to enrollments page
    await page.goto("/courses/1/enrollments")
  })

  test("should display course information and enrollments", async ({ page }) => {
    await expect(page.getByText("Anmeldungen für Atemschutzgeräteträger")).toBeVisible()
    await expect(page.getByText("15 / 20 Plätze belegt")).toBeVisible()
    await expect(page.getByText("📅")).toBeVisible()
    await expect(page.getByText("01.09.2026 - 05.09.2026")).toBeVisible()
    await expect(page.getByText("📖")).toBeVisible()
    await expect(page.getByText("Grundausbildung Atemschutz")).toBeVisible()
    await expect(page.getByText("👥")).toBeVisible()
    await expect(page.getByText("Lehrgangsleiter: Max Mustermann")).toBeVisible()

    await expect(page.getByText("member-1")).toBeVisible()
    await expect(page.getByText("Bestätigt")).toBeVisible()
    await expect(page.getByText("member-2")).toBeVisible()
    await expect(page.getByText("Warteliste")).toBeVisible()
  })

  test("should open create enrollment dialog", async ({ page }) => {
    await page.getByRole("button", { name: "Anmeldung erstellen" }).click()
    await expect(page.getByRole("dialog", { name: "Anmeldung erstellen" })).toBeVisible()
    await expect(page.getByLabel("Mitglied ID")).toBeVisible()
    await expect(page.getByLabel("Kommentar (optional)")).toBeVisible()
  })

  test("should create a new enrollment", async ({ page }) => {
    await page.getByRole("button", { name: "Anmeldung erstellen" }).click()

    await page.getByLabel("Mitglied ID").fill("member-3")
    await page.getByLabel("Kommentar (optional)").fill("Testanmeldung")

    await page.getByRole("button", { name: "Erstellen" }).click()

    await expect(page.getByRole("dialog")).not.toBeVisible()
    await expect(page.getByText("member-3")).toBeVisible()
  })

  test("should open cancel confirmation and cancel enrollment", async ({ page }) => {
    const cancelButtons = page.getByRole("button", { name: "Stornieren" })
    await cancelButtons.first().click()

    await expect(page.getByRole("dialog", { name: "Anmeldung stornieren" })).toBeVisible()
    await expect(page.getByText('Möchten Sie die Anmeldung von Mitglied member-1 wirklich stornieren?')).toBeVisible()

    await page.getByRole("button", { name: "Stornieren" }).click()

    await expect(page.getByRole("dialog")).not.toBeVisible()
    await expect(page.getByText("member-1")).not.toBeVisible()
  })

  test("should show confirm button for PENDING enrollments", async ({ page }) => {
    // Update one enrollment to PENDING status
    await page.route("/api/v1/courses/1/enrollments", async (route) => {
      const json = {
        data: [
          {
            id: "enrollment-1",
            memberId: "member-1",
            status: "PENDING",
            createdAt: "2026-08-01T10:00:00Z",
          },
        ],
        page: 0,
        size: 20,
        totalElements: 1,
        totalPages: 1,
      }
      await route.fulfill({ json })
    })

    await page.reload()

    await expect(page.getByRole("button", { name: "Bestätigen" })).toBeVisible()
  })
})
