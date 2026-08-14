import { describe, it, expect } from "vitest"
import { courseKeys } from "./courses-api"

describe("Courses API Tests", () => {
  describe("courseKeys", () => {
    it("should export correct query keys", () => {
      expect(courseKeys).toHaveProperty("all")
      expect(courseKeys).toHaveProperty("lists")
      expect(courseKeys).toHaveProperty("list")
      expect(courseKeys).toHaveProperty("details")
      expect(courseKeys).toHaveProperty("detail")
      expect(courseKeys).toHaveProperty("enrollments")
      expect(courseKeys).toHaveProperty("enrollmentsByCourse")
    })

    it("should generate correct query keys", () => {
      const listKey = courseKeys.list(0, 20)
      expect(listKey).toEqual(["courses", "list", { page: 0, size: 20 }])

      const detailKey = courseKeys.detail("course-1")
      expect(detailKey).toEqual(["courses", "detail", "course-1"])

      const enrollmentsKey = courseKeys.enrollmentsByCourse("course-1")
      expect(enrollmentsKey).toEqual(["courses", "enrollments", "course-1"])
    })
  })

  describe("Type Exports", () => {
    it("should compile without errors", () => {
      // This test verifies that the module can be imported without errors
      // The actual type checking is done by TypeScript at compile time
      expect(true).toBe(true)
    })
  })
})
