package org.example.m295nick.repositories;

import org.example.m295nick.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByHasAirConditioning(Boolean hasAirConditioning);
    List<Vehicle> findByBrandContainingIgnoreCase(String brand);
}
