package de.feuerwehr.geraet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GeraetControllerTest {

    @Mock
    private GeraetService geraetService;

    @InjectMocks
    private GeraetController geraetController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGeraet_shouldReturnCreatedGeraet() {
        Geraet geraet = new Geraet("Testgeraet", 10.5, LocalDate.now().plusYears(1));
        when(geraetService.createGeraet(any(Geraet.class))).thenReturn(geraet);

        ResponseEntity<Geraet> response = geraetController.createGeraet(geraet);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(geraet, response.getBody());
    }

    @Test
    void getAllGeraete_shouldReturnListOfGeraete() {
        Geraet geraet1 = new Geraet("Geraet1", 5.0, LocalDate.now().plusYears(1));
        Geraet geraet2 = new Geraet("Geraet2", 7.5, LocalDate.now().plusYears(2));
        List<Geraet> geraete = Arrays.asList(geraet1, geraet2);
        when(geraetService.getAllGeraete()).thenReturn(geraete);

        ResponseEntity<List<Geraet>> response = geraetController.getAllGeraete();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getGeraetById_shouldReturnGeraetWhenFound() {
        UUID id = UUID.randomUUID();
        Geraet geraet = new Geraet("Testgeraet", 10.0, LocalDate.now().plusYears(1));
        when(geraetService.getGeraetById(id)).thenReturn(geraet);

        ResponseEntity<Geraet> response = geraetController.getGeraetById(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(geraet, response.getBody());
    }

    @Test
    void getGeraetById_shouldReturnNotFoundWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(geraetService.getGeraetById(id)).thenReturn(null);

        ResponseEntity<Geraet> response = geraetController.getGeraetById(id);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void updateGeraet_shouldReturnUpdatedGeraetWhenFound() {
        UUID id = UUID.randomUUID();
        Geraet geraetDetails = new Geraet("UpdatedGeraet", 15.0, LocalDate.now().plusYears(2));
        Geraet updatedGeraet = new Geraet("UpdatedGeraet", 15.0, LocalDate.now().plusYears(2));
        when(geraetService.updateGeraet(eq(id), any(Geraet.class))).thenReturn(updatedGeraet);

        ResponseEntity<Geraet> response = geraetController.updateGeraet(id, geraetDetails);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedGeraet, response.getBody());
    }

    @Test
    void updateGeraet_shouldReturnNotFoundWhenNotFound() {
        UUID id = UUID.randomUUID();
        Geraet geraetDetails = new Geraet("UpdatedGeraet", 15.0, LocalDate.now().plusYears(2));
        when(geraetService.updateGeraet(eq(id), any(Geraet.class))).thenReturn(null);

        ResponseEntity<Geraet> response = geraetController.updateGeraet(id, geraetDetails);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void deleteGeraet_shouldReturnNoContent() {
        UUID id = UUID.randomUUID();
        doNothing().when(geraetService).deleteGeraet(id);

        ResponseEntity<Void> response = geraetController.deleteGeraet(id);

        assertEquals(204, response.getStatusCodeValue());
        verify(geraetService, times(1)).deleteGeraet(id);
    }
}