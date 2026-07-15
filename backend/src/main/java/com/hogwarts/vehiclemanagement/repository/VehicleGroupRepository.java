package com.hogwarts.vehiclemanagement.repository;

import com.hogwarts.vehiclemanagement.entity.VehicleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleGroupRepository extends JpaRepository<VehicleGroup, Long>, JpaSpecificationExecutor<VehicleGroup> {

    Optional<VehicleGroup> findByIdAndDeletedFalse(Long id);

    boolean existsByNameAndDeletedFalse(String name);
}
