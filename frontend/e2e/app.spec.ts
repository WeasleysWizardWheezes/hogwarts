import { test, expect } from "@playwright/test"

test.describe("App", () => {
  test("shows the Fire Manager title", async ({ page }) => {
    await page.goto("/")
    await expect(page.getByRole("heading", { name: "Fire Manager" })).toBeVisible()
  })
})
