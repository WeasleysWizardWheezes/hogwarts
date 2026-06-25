package de.feuerwehr.geraet;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Geraet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;
    private double gewicht;
    private LocalDate ablaufdatum;

    // Standardkonstruktor
    public Geraet() {
    }

    // Konstruktor mit Parametern
    public Geraet(String name, double gewicht, LocalDate ablaufdatum) {
        this.name = name;
        this.gewicht = gewicht;
        this.ablaufdatum = ablaufdatum;
    }

    // Getter und Setter
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getGewicht() {
        return gewicht;
    }

    public void setGewicht(double gewicht) {
        this.gewicht = gewicht;
    }

    public LocalDate getAblaufdatum() {
        return ablaufdatum;
    }

    public void setAblaufdatum(LocalDate ablaufdatum) {
        this.ablaufdatum = ablaufdatum;
    }

    @Override
    public String toString() {
        return "Geraet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gewicht=" + gewicht +
                ", ablaufdatum=" + ablaufdatum +
                '}';
    }
}