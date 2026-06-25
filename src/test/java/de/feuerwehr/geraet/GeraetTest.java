package de.feuerwehr.geraet;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class GeraetTest {

    @Test
    void testGeraetCreationWithConstructor() {
        String name = "Testgeraet";
        double gewicht = 10.5;
        LocalDate ablaufdatum = LocalDate.now().plusYears(1);

        Geraet geraet = new Geraet(name, gewicht, ablaufdatum);

        assertEquals(name, geraet.getName());
        assertEquals(gewicht, geraet.getGewicht());
        assertEquals(ablaufdatum, geraet.getAblaufdatum());
        assertNotNull(geraet.getId());
    }

    @Test
    void testGeraetCreationWithDefaultConstructor() {
        Geraet geraet = new Geraet();

        assertNull(geraet.getName());
        assertEquals(0.0, geraet.getGewicht());
        assertNull(geraet.getAblaufdatum());
        assertNull(geraet.getId());
    }

    @Test
    void testSettersAndGetters() {
        Geraet geraet = new Geraet();
        
        String name = "UpdatedGeraet";
        double gewicht = 15.0;
        LocalDate ablaufdatum = LocalDate.now().plusYears(2);

        geraet.setName(name);
        geraet.setGewicht(gewicht);
        geraet.setAblaufdatum(ablaufdatum);

        assertEquals(name, geraet.getName());
        assertEquals(gewicht, geraet.getGewicht());
        assertEquals(ablaufdatum, geraet.getAblaufdatum());
    }

    @Test
    void testToString() {
        String name = "Testgeraet";
        double gewicht = 10.5;
        LocalDate ablaufdatum = LocalDate.now().plusYears(1);
        Geraet geraet = new Geraet(name, gewicht, ablaufdatum);

        String toString = geraet.toString();

        assertTrue(toString.contains("Geraet"));
        assertTrue(toString.contains("name='" + name + "'"));
        assertTrue(toString.contains("gewicht=" + gewicht));
        assertTrue(toString.contains("ablaufdatum=" + ablaufdatum));
    }
}