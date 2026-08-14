package de.thkoeln.ccq.firemanager.course;

import de.thkoeln.ccq.firemanager.course.enrollment.CourseEnrollmentService;
import de.thkoeln.ccq.firemanager.course.exception.CourseNotFoundException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseService courseServiceStub;

    @MockitoBean
    private CourseEnrollmentService courseEnrollmentServiceStub;

    @Test
    void getAll_returnsOk() throws Exception {
        // Arrange
        var instructorId = UUID.randomUUID();
        var course = new Course("Kurs 1", "Beschreibung 1", 5, instructorId, "Instructor 1", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        when(courseServiceStub.getAll()).thenReturn(List.of(course));

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Kurs 1"));
    }

    @Test
    void createCourse_returnsCreated() throws Exception {
        // Arrange
        var instructorId = UUID.randomUUID();
        var startDate = OffsetDateTime.now();
        var endDate = startDate.plusDays(7);
        var course = new Course("Neuer Kurs", "Neue Beschreibung", 10, instructorId, "Instructor", startDate, endDate);
        when(courseServiceStub.create(any(String.class), any(String.class), any(Integer.class), any(UUID.class), any(String.class), any(OffsetDateTime.class), any(OffsetDateTime.class), any(String.class)))
                .thenReturn(course);

        // Act & Assert
        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Neuer Kurs", "description": "Neue Beschreibung", "maxParticipants": 10, 
                                 "instructorId": "%s", "instructorName": "Instructor", 
                                 "startDate": "%s", "endDate": "%s", "status": "PLANNED"}
                                """.formatted(instructorId, startDate.toString(), endDate.toString())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Neuer Kurs"));
    }

    @Test
    void getCourseById_returnsOk() throws Exception {
        // Arrange
        var course = new Course("Kurs", "Beschreibung", 5, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        when(courseServiceStub.getById(course.getId())).thenReturn(course);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses/{id}", course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kurs"));
    }

    @Test
    void getCourseById_returns404WhenNotFound() throws Exception {
        // Arrange
        var courseId = UUID.randomUUID();
        when(courseServiceStub.getById(courseId))
                .thenThrow(new CourseNotFoundException(courseId));

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses/{id}", courseId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Course with id " + courseId + " not found"));
    }

    @Test
    void updateCourse_returnsOk() throws Exception {
        // Arrange
        var course = new Course("Aktualisierter Kurs", "Aktualisierte Beschreibung", 10, UUID.randomUUID(), "Aktualisierter Instructor", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        when(courseServiceStub.update(any(UUID.class), any(String.class), any(String.class), any(Integer.class), any(UUID.class), any(String.class), any(OffsetDateTime.class), any(OffsetDateTime.class), any(String.class)))
                .thenReturn(course);

        // Act & Assert
        mockMvc.perform(put("/api/v1/courses/{id}", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Aktualisierter Kurs", "description": "Aktualisierte Beschreibung", "maxParticipants": 10, 
                                 "instructorId": "%s", "instructorName": "Aktualisierter Instructor", 
                                 "startDate": "%s", "endDate": "%s", "status": "PLANNED"}
                                """.formatted(course.getInstructorId(), course.getStartDate().toString(), course.getEndDate().toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aktualisierter Kurs"));
    }

    @Test
    void deleteCourse_returnsNoContent() throws Exception {
        // Arrange
        var courseId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/v1/courses/{id}", courseId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCourse_returns404WhenNotFound() throws Exception {
        // Arrange
        var courseId = UUID.randomUUID();
        doThrow(new CourseNotFoundException(courseId)).when(courseServiceStub).deleteById(courseId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/courses/{id}", courseId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createEnrollment_returnsCreated() throws Exception {
        // Arrange
        var courseId = UUID.randomUUID();
        var memberId = UUID.randomUUID();
        var enrollment = new CourseEnrollment(courseId, "Kurs", memberId, "Max Mustermann", "PENDING", "Kommentar");
        when(courseEnrollmentServiceStub.create(any(UUID.class), any(UUID.class), any(String.class), any(String.class), any(String.class)))
                .thenReturn(enrollment);

        // Act & Assert
        mockMvc.perform(post("/api/v1/courses/{courseId}/enrollments", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"memberId": "%s", "memberName": "Max Mustermann", "status": "PENDING", "comment": "Kommentar"}
                                """.formatted(memberId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberName").value("Max Mustermann"));
    }

    @Test
    void getEnrollmentsByCourse_returnsOk() throws Exception {
        // Arrange
        var courseId = UUID.randomUUID();
        var memberId = UUID.randomUUID();
        var enrollment = new CourseEnrollment(courseId, "Kurs", memberId, "Max Mustermann", "PENDING", "Kommentar");
        when(courseEnrollmentServiceStub.getAllByCourse(courseId)).thenReturn(List.of(enrollment));

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses/{courseId}/enrollments", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].memberName").value("Max Mustermann"));
    }

    @Test
    void cancelEnrollment_returnsNoContent() throws Exception {
        // Arrange
        var courseId = UUID.randomUUID();
        var enrollmentId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/v1/courses/{courseId}/enrollments/{enrollmentId}", courseId, enrollmentId))
                .andExpect(status().isNoContent());
    }
}