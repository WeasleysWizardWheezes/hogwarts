import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { api } from "@/shared/api"
import type { components } from "@/shared/api"
import { toast } from "sonner"

export type CourseResponse = components["schemas"]["CourseResponse"]
export type CreateCourseRequest = components["schemas"]["CreateCourseRequest"]
export type UpdateCourseRequest = components["schemas"]["UpdateCourseRequest"]
export type CourseEnrollmentResponse = components["schemas"]["EnrollmentResponse"]
export type CreateEnrollmentRequest = components["schemas"]["CreateEnrollmentRequest"]

export const courseKeys = {
  all: ["courses"] as const,
  lists: () => [...courseKeys.all, "list"] as const,
  list: (page: number, size: number) => [...courseKeys.lists(), { page, size }] as const,
  details: () => [...courseKeys.all, "detail"] as const,
  detail: (id: string) => [...courseKeys.details(), id] as const,
  enrollments: () => [...courseKeys.all, "enrollments"] as const,
  enrollmentsByCourse: (courseId: string) => [...courseKeys.enrollments(), courseId] as const,
}

export function useCourses(page = 0, size = 20) {
  return useQuery({
    queryKey: courseKeys.list(page, size),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/courses", {
        params: { query: { page, size } },
      })
      if (error) throw error
      return data
    },
  })
}

export function useCourse(courseId: string) {
  return useQuery({
    queryKey: courseKeys.detail(courseId),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/courses/{courseId}", {
        params: { path: { courseId } },
      })
      if (error) throw error
      return data
    },
  })
}

export function useCreateCourse() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (body: CreateCourseRequest) => {
      const { data, error } = await api.POST("/api/v1/courses", { body })
      if (error) throw error
      return data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: courseKeys.lists() })
      toast.success("Lehrgang erfolgreich erstellt")
    },
    onError: () => {
      toast.error("Fehler beim Erstellen")
    },
  })
}

export function useUpdateCourse() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({ courseId, body }: { courseId: string; body: UpdateCourseRequest }) => {
      const { data, error } = await api.PUT("/api/v1/courses/{courseId}", {
        params: { path: { courseId } },
        body,
      })
      if (error) throw error
      return data
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: courseKeys.lists() })
      queryClient.invalidateQueries({ queryKey: courseKeys.details() })
      toast.success("Lehrgang erfolgreich aktualisiert")
    },
    onError: () => {
      toast.error("Fehler beim Aktualisieren")
    },
  })
}

export function useDeleteCourse() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (courseId: string) => {
      const { error } = await api.DELETE("/api/v1/courses/{courseId}", {
        params: { path: { courseId } },
      })
      if (error) throw error
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: courseKeys.lists() })
      toast.success("Lehrgang erfolgreich gelöscht")
    },
    onError: () => {
      toast.error("Fehler beim Löschen")
    },
  })
}

export function useCourseEnrollments(courseId: string, page = 0, size = 20) {
  return useQuery({
    queryKey: courseKeys.enrollmentsByCourse(courseId),
    queryFn: async () => {
      const { data, error } = await api.GET("/api/v1/courses/{courseId}/enrollments", {
        params: { path: { courseId }, query: { page, size } },
      })
      if (error) throw error
      return data
    },
  })
}

export function useCreateEnrollment() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({ courseId, body }: { courseId: string; body: CreateEnrollmentRequest }) => {
      const { data, error } = await api.POST("/api/v1/courses/{courseId}/enrollments", {
        params: { path: { courseId } },
        body,
      })
      if (error) throw error
      return data
    },
    onSuccess: (_: unknown, variables: { courseId: string; body: CreateEnrollmentRequest }) => {
      queryClient.invalidateQueries({ queryKey: courseKeys.enrollmentsByCourse(variables.courseId) })
      toast.success("Anmeldung erfolgreich erstellt")
    },
    onError: () => {
      toast.error("Fehler beim Erstellen der Anmeldung")
    },
  })
}

export function useCancelEnrollment() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({ courseId, enrollmentId }: { courseId: string; enrollmentId: string }) => {
      const { error } = await api.DELETE("/api/v1/courses/{courseId}/enrollments/{enrollmentId}", {
        params: { path: { courseId, enrollmentId } },
      })
      if (error) throw error
    },
    onSuccess: (_: unknown, variables: { courseId: string; enrollmentId: string }) => {
      queryClient.invalidateQueries({ queryKey: courseKeys.enrollmentsByCourse(variables.courseId) })
      toast.success("Anmeldung erfolgreich storniert")
    },
    onError: () => {
      toast.error("Fehler beim Stornieren der Anmeldung")
    },
  })
}
