package de.feuerwehr.geraet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/geraete")
public class GeraetController {

    private final GeraetService geraetService;

    @Autowired
    public GeraetController(GeraetService geraetService) {
        this.geraetService = geraetService;
    }

    @PostMapping
    public ResponseEntity<Geraet> createGeraet(@RequestBody Geraet geraet) {
        Geraet createdGeraet = geraetService.createGeraet(geraet);
        return ResponseEntity.ok(createdGeraet);
    }

    @GetMapping
    public ResponseEntity<List<Geraet>> getAllGeraete() {
        List<Geraet> geraete = geraetService.getAllGeraete();
        return ResponseEntity.ok(geraete);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Geraet> getGeraetById(@PathVariable UUID id) {
        Geraet geraet = geraetService.getGeraetById(id);
        if (geraet != null) {
            return ResponseEntity.ok(geraet);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Geraet> updateGeraet(@PathVariable UUID id, @RequestBody Geraet geraetDetails) {
        Geraet updatedGeraet = geraetService.updateGeraet(id, geraetDetails);
        if (updatedGeraet != null) {
            return ResponseEntity.ok(updatedGeraet);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGeraet(@PathVariable UUID id) {
        geraetService.deleteGeraet(id);
        return ResponseEntity.noContent().build();
    }
}