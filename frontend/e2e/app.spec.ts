import { test, expect } from "@playwright/test"

test.describe("App", () => {
  test("loads without errors", async ({ page }) => {
    await page.goto("/")
    await expect(page.locator("body")).not.toContainText("Application Error")
  })
})
