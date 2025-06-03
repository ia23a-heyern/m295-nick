package org.example.m295nick.services;

import org.example.m295nick.models.Vehicle;
import org.example.m295nick.repositories.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    // ✅ Manueller Konstruktor (Lombok wird nicht verwendet)
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public boolean vehicleExists(Long id) {
        return vehicleRepository.existsById(id);
    }

    public List<Vehicle> filterByAirConditioning(boolean hasAC) {
        return vehicleRepository.findByHasAirConditioning(hasAC);
    }

    public List<Vehicle> filterByBrand(String brand) {
        return vehicleRepository.findByBrandContainingIgnoreCase(brand);
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> createMultipleVehicles(List<Vehicle> vehicles) {
        return vehicleRepository.saveAll(vehicles);
    }

    public Vehicle updateVehicle(Long id, Vehicle updated) {
        return vehicleRepository.findById(id).map(vehicle -> {
            vehicle.setBrand(updated.getBrand());
            vehicle.setModel(updated.getModel());
            vehicle.setPricePerDay(updated.getPricePerDay());
            vehicle.setFirstRegistration(updated.getFirstRegistration());
            vehicle.setSeats(updated.getSeats());
            vehicle.setHasAirConditioning(updated.isHasAirConditioning());
            return vehicleRepository.save(vehicle);
        }).orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public void deleteById(Long id) {
        vehicleRepository.deleteById(id);
    }

    public void deleteAll() {
        vehicleRepository.deleteAll();
    }

    public void deleteByRegistrationDateBefore(java.time.LocalDate date) {
        List<Vehicle> toDelete = vehicleRepository.findAll().stream()
                .filter(v -> v.getFirstRegistration().isBefore(date))
                .toList();
        vehicleRepository.deleteAll(toDelete);
    }
}
