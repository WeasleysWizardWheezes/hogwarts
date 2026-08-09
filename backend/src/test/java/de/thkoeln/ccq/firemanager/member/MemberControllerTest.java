package de.thkoeln.ccq.firemanager.member;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberLocationAssignmentService memberLocationAssignmentServiceStub;

    @Test
    void assignMemberToLocation_returnsOk() throws Exception {
        // Arrange
        UUID memberId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();
        
        de.thkoeln.ccq.firemanager.location.Location location = 
            new de.thkoeln.ccq.firemanager.location.Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "GERAETEHAUS");
        
        MemberLocationAssignment assignment = new MemberLocationAssignment(memberId, location);
        when(memberLocationAssignmentServiceStub.assignMemberToLocation(eq(memberId), eq(locationId)))
                .thenReturn(assignment);

        String requestBody = """
            {
                "locationId": "%s"
            }
            """.formatted(locationId);

        // Act & Assert
        mockMvc.perform(post("/api/v1/members/{memberId}/location", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId.toString()))
                .andExpect(jsonPath("$.locations[0].id").value(locationId.toString()))
                .andExpect(jsonPath("$.locations[0].name").value("Gerätehaus Köln"));
    }

    @Test
    void assignMemberToLocation_returnsBadRequestWhenLocationIdIsInvalid() throws Exception {
        // Arrange
        UUID memberId = UUID.randomUUID();
        String requestBody = """
            {
                "locationId": "invalid-uuid"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/members/{memberId}/location", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMember_returnsOkWhenMemberHasAssignment() throws Exception {
        // Arrange
        UUID memberId = UUID.randomUUID();
        de.thkoeln.ccq.firemanager.location.Location location = 
            new de.thkoeln.ccq.firemanager.location.Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "GERAETEHAUS");
        
        MemberLocationAssignment assignment = new MemberLocationAssignment(memberId, location);
        when(memberLocationAssignmentServiceStub.getAssignmentsByMember(memberId))
                .thenReturn(List.of(assignment));

        // Act & Assert
        mockMvc.perform(get("/api/v1/members/{memberId}", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(memberId.toString()))
                .andExpect(jsonPath("$.locations[0].id").value(location.getId().toString()))
                .andExpect(jsonPath("$.locations[0].name").value("Gerätehaus Köln"));
    }

    @Test
    void getMember_returnsNotFoundWhenMemberHasNoAssignment() throws Exception {
        // Arrange
        UUID memberId = UUID.randomUUID();
        when(memberLocationAssignmentServiceStub.getAssignmentsByMember(memberId))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/members/{memberId}", memberId))
                .andExpect(status().isNotFound());
    }

    @Test
    void listMembers_returnsOk() throws Exception {
        // Arrange
        UUID memberId1 = UUID.randomUUID();
        UUID memberId2 = UUID.randomUUID();
        de.thkoeln.ccq.firemanager.location.Location location = 
            new de.thkoeln.ccq.firemanager.location.Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "GERAETEHAUS");
        
        MemberLocationAssignment assignment1 = new MemberLocationAssignment(memberId1, location);
        MemberLocationAssignment assignment2 = new MemberLocationAssignment(memberId2, location);
        
        when(memberLocationAssignmentServiceStub.getAssignmentsByLocation(null))
                .thenReturn(List.of(assignment1, assignment2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void listMembers_returnsOkWhenFilteredByLocation() throws Exception {
        // Arrange
        UUID locationId = UUID.randomUUID();
        UUID memberId1 = UUID.randomUUID();
        UUID memberId2 = UUID.randomUUID();
        de.thkoeln.ccq.firemanager.location.Location location = 
            new de.thkoeln.ccq.firemanager.location.Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "GERAETEHAUS");
        
        MemberLocationAssignment assignment1 = new MemberLocationAssignment(memberId1, location);
        MemberLocationAssignment assignment2 = new MemberLocationAssignment(memberId2, location);
        
        when(memberLocationAssignmentServiceStub.getAssignmentsByLocation(locationId))
                .thenReturn(List.of(assignment1, assignment2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/members?locationId={locationId}", locationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void deleteMemberLocationAssignment_returnsNoContent() throws Exception {
        // Arrange
        UUID memberId = UUID.randomUUID();
        doNothing().when(memberLocationAssignmentServiceStub).deleteAssignmentByMember(memberId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/members/{memberId}/location", memberId))
                .andExpect(status().isNoContent());
    }
}