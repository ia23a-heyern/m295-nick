package org.example.m295nick.services;

import org.example.m295nick.models.Vehicle;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VehicleService {

    // ─── Read (CRUD) ───
    Optional<Vehicle> getById(Long id);
    boolean existsById(Long id);
    List<Vehicle> getAll();
    List<Vehicle> getByAirConditioning(Boolean hasAir);
    List<Vehicle> getByBrand(String brandPart);

    // ─── Create ───
    Vehicle create(Vehicle vehicle);
    List<Vehicle> createAll(List<Vehicle> vehicles);

    // ─── Update ───
    Vehicle update(Long id, Vehicle vehicleToUpdate);

    // ─── Delete ───
    void deleteById(Long id);
    void deleteAll();
    void deleteByFirstRegistrationBefore(LocalDate date);
}
