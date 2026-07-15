package de.thkoeln.ccq.vehicle.service;

import de.thkoeln.ccq.vehicle.Vehicle;
import de.thkoeln.ccq.vehicle.VehicleGroup;
import de.thkoeln.ccq.vehicle.VehicleRepository;
import de.thkoeln.ccq.vehicle.VehicleStatus;
import de.thkoeln.ccq.vehicle.dto.VehicleRequest;
import de.thkoeln.ccq.vehicle.exception.VehicleLicensePlateAlreadyExistsException;
import de.thkoeln.ccq.vehicle.exception.VehicleNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleGroupService vehicleGroupService;

    public Vehicle createVehicle(VehicleRequest request) {
        VehicleGroup gruppe = vehicleGroupService.getVehicleGroupById(request.getGruppeId());

        if (request.getKennzeichen() != null && vehicleRepository.existsByKennzeichenAndArchiviertFalse(request.getKennzeichen())) {
            throw new VehicleLicensePlateAlreadyExistsException(request.getKennzeichen());
        }

        Vehicle vehicle = Vehicle.builder()
                .name(request.getName())
                .funkrufname(request.getFunkrufname())
                .kennzeichen(request.getKennzeichen())
                .baujahr(request.getBaujahr())
                .beschreibung(request.getBeschreibung())
                .status(request.getStatus())
                .gruppe(gruppe)
                .build();

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findByArchiviertFalse();
    }

    public List<Vehicle> getVehiclesByStatus(VehicleStatus status) {
        return vehicleRepository.findByArchiviertFalseAndStatus(status);
    }

    public List<Vehicle> getVehiclesByGroup(UUID gruppeId) {
        VehicleGroup gruppe = vehicleGroupService.getVehicleGroupById(gruppeId);
        return vehicleRepository.findByArchiviertFalseAndGruppe(gruppe);
    }

    public Vehicle getVehicleById(UUID id) {
        return vehicleRepository.findByArchiviertFalseAndId(id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new VehicleNotFoundException(id));
    }

    public Vehicle updateVehicle(UUID id, VehicleRequest request) {
        Vehicle existingVehicle = getVehicleById(id);

        if (request.getKennzeichen() != null && !request.getKennzeichen().equals(existingVehicle.getKennzeichen()) && 
                vehicleRepository.existsByKennzeichenAndArchiviertFalse(request.getKennzeichen())) {
            throw new VehicleLicensePlateAlreadyExistsException(request.getKennzeichen());
        }

        existingVehicle.setName(request.getName());
        existingVehicle.setFunkrufname(request.getFunkrufname());
        existingVehicle.setKennzeichen(request.getKennzeichen());
        existingVehicle.setBaujahr(request.getBaujahr());
        existingVehicle.setBeschreibung(request.getBeschreibung());
        existingVehicle.setStatus(request.getStatus());

        if (request.getGruppeId() != null) {
            VehicleGroup gruppe = vehicleGroupService.getVehicleGroupById(request.getGruppeId());
            existingVehicle.setGruppe(gruppe);
        }

        return vehicleRepository.save(existingVehicle);
    }

    public void deleteVehicle(UUID id) {
        Vehicle vehicle = getVehicleById(id);
        vehicle.setArchiviert(true);
        vehicleRepository.save(vehicle);
    }
}
