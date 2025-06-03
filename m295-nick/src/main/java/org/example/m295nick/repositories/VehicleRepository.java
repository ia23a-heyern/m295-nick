package org.example.m295nick.repositories;

import org.example.m295nick.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByHasAirConditioning(boolean hasAirConditioning);
    List<Vehicle> findByBrandContainingIgnoreCase(String brand);
}
