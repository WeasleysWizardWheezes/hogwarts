package de.feuerwehr.geraet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GeraetServiceTest {

    @Mock
    private GeraetRepository geraetRepository;

    @InjectMocks
    private GeraetService geraetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGeraet_shouldSaveAndReturnGeraet() {
        Geraet geraet = new Geraet("Testgeraet", 10.5, LocalDate.now().plusYears(1));
        when(geraetRepository.save(any(Geraet.class))).thenReturn(geraet);

        Geraet result = geraetService.createGeraet(geraet);

        assertEquals(geraet, result);
        verify(geraetRepository, times(1)).save(geraet);
    }

    @Test
    void getAllGeraete_shouldReturnListOfGeraete() {
        Geraet geraet1 = new Geraet("Geraet1", 5.0, LocalDate.now().plusYears(1));
        Geraet geraet2 = new Geraet("Geraet2", 7.5, LocalDate.now().plusYears(2));
        List<Geraet> geraete = Arrays.asList(geraet1, geraet2);
        when(geraetRepository.findAll()).thenReturn(geraete);

        List<Geraet> result = geraetService.getAllGeraete();

        assertEquals(2, result.size());
        assertEquals(geraete, result);
        verify(geraetRepository, times(1)).findAll();
    }

    @Test
    void getGeraetById_shouldReturnGeraetWhenFound() {
        UUID id = UUID.randomUUID();
        Geraet geraet = new Geraet("Testgeraet", 10.0, LocalDate.now().plusYears(1));
        when(geraetRepository.findById(id)).thenReturn(Optional.of(geraet));

        Geraet result = geraetService.getGeraetById(id);

        assertEquals(geraet, result);
        verify(geraetRepository, times(1)).findById(id);
    }

    @Test
    void getGeraetById_shouldReturnNullWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(geraetRepository.findById(id)).thenReturn(Optional.empty());

        Geraet result = geraetService.getGeraetById(id);

        assertNull(result);
        verify(geraetRepository, times(1)).findById(id);
    }

    @Test
    void updateGeraet_shouldUpdateAndReturnGeraetWhenFound() {
        UUID id = UUID.randomUUID();
        Geraet existingGeraet = new Geraet("OldGeraet", 10.0, LocalDate.now().plusYears(1));
        Geraet geraetDetails = new Geraet("UpdatedGeraet", 15.0, LocalDate.now().plusYears(2));
        
        when(geraetRepository.findById(id)).thenReturn(Optional.of(existingGeraet));
        when(geraetRepository.save(any(Geraet.class))).thenReturn(existingGeraet);

        Geraet result = geraetService.updateGeraet(id, geraetDetails);

        assertEquals(existingGeraet, result);
        assertEquals("UpdatedGeraet", result.getName());
        assertEquals(15.0, result.getGewicht());
        assertEquals(geraetDetails.getAblaufdatum(), result.getAblaufdatum());
        verify(geraetRepository, times(1)).findById(id);
        verify(geraetRepository, times(1)).save(existingGeraet);
    }

    @Test
    void updateGeraet_shouldReturnNullWhenNotFound() {
        UUID id = UUID.randomUUID();
        Geraet geraetDetails = new Geraet("UpdatedGeraet", 15.0, LocalDate.now().plusYears(2));
        
        when(geraetRepository.findById(id)).thenReturn(Optional.empty());

        Geraet result = geraetService.updateGeraet(id, geraetDetails);

        assertNull(result);
        verify(geraetRepository, times(1)).findById(id);
        verify(geraetRepository, never()).save(any(Geraet.class));
    }

    @Test
    void deleteGeraet_shouldCallDeleteById() {
        UUID id = UUID.randomUUID();
        doNothing().when(geraetRepository).deleteById(id);

        geraetService.deleteGeraet(id);

        verify(geraetRepository, times(1)).deleteById(id);
    }
}