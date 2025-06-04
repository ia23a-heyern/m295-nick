package org.example.m295nick.repositories;

import org.example.m295nick.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // 1) Lese nach Boolean‐Feld
    List<Vehicle> findByHasAirConditioning(Boolean hasAirConditioning);

    // 2) Lese nach Text‐Feld (Teilstring‐Suche auf brand)
    List<Vehicle> findByBrandContainingIgnoreCase(String brandPart);
}
