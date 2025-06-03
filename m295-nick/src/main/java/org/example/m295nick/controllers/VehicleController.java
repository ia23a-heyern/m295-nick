package org.example.m295nick.controllers;

import org.example.m295nick.models.Vehicle;
import org.example.m295nick.services.VehicleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // 🔓 Lesen für ADMIN und USER
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/exists/{id}")
    public boolean existsById(@PathVariable Long id) {
        return vehicleService.vehicleExists(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/filter/ac")
    public List<Vehicle> filterByAC(@RequestParam boolean value) {
        return vehicleService.filterByAirConditioning(value);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/filter/brand")
    public List<Vehicle> filterByBrand(@RequestParam String value) {
        return vehicleService.filterByBrand(value);
    }

    // 🔐 Nur ADMIN darf hinzufügen
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Vehicle create(@RequestBody Vehicle vehicle) {
        return vehicleService.createVehicle(vehicle);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    public List<Vehicle> createMultiple(@RequestBody List<Vehicle> vehicles) {
        return vehicleService.createMultipleVehicles(vehicles);
    }

    // 🔐 Nur ADMIN darf aktualisieren
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Vehicle update(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        return vehicleService.updateVehicle(id, vehicle);
    }

    // 🔐 Nur ADMIN darf löschen
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        vehicleService.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public void deleteAll() {
        vehicleService.deleteAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/older-than")
    public void deleteByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        vehicleService.deleteByRegistrationDateBefore(date);
    }
}
