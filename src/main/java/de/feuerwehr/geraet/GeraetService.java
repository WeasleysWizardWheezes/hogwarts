package de.feuerwehr.geraet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GeraetService {

    private final GeraetRepository geraetRepository;

    @Autowired
    public GeraetService(GeraetRepository geraetRepository) {
        this.geraetRepository = geraetRepository;
    }

    public Geraet createGeraet(Geraet geraet) {
        return geraetRepository.save(geraet);
    }

    public List<Geraet> getAllGeraete() {
        return geraetRepository.findAll();
    }

    public Geraet getGeraetById(UUID id) {
        return geraetRepository.findById(id).orElse(null);
    }

    public Geraet updateGeraet(UUID id, Geraet geraetDetails) {
        Geraet geraet = geraetRepository.findById(id).orElse(null);
        if (geraet != null) {
            geraet.setName(geraetDetails.getName());
            geraet.setGewicht(geraetDetails.getGewicht());
            geraet.setAblaufdatum(geraetDetails.getAblaufdatum());
            return geraetRepository.save(geraet);
        }
        return null;
    }

    public void deleteGeraet(UUID id) {
        geraetRepository.deleteById(id);
    }
}