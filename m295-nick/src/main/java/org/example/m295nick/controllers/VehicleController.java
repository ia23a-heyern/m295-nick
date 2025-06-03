package org.example.m295nick.controllers;

import lombok.RequiredArgsConstructor;
import org.example.m295nick.models.Vehicle;
import org.example.m295nick.services.VehicleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exists/{id}")
    public boolean existsById(@PathVariable Long id) {
        return vehicleService.vehicleExists(id);
    }

    @GetMapping("/filter/ac")
    public List<Vehicle> filterByAC(@RequestParam boolean value) {
        return vehicleService.filterByAirConditioning(value);
    }

    @GetMapping("/filter/brand")
    public List<Vehicle> filterByBrand(@RequestParam String value) {
        return vehicleService.filterByBrand(value);
    }

    @PostMapping
    public Vehicle create(@RequestBody Vehicle vehicle) {
        return vehicleService.createVehicle(vehicle);
    }

    @PostMapping("/batch")
    public List<Vehicle> createMultiple(@RequestBody List<Vehicle> vehicles) {
        return vehicleService.createMultipleVehicles(vehicles);
    }

    @PutMapping("/{id}")
    public Vehicle update(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        return vehicleService.updateVehicle(id, vehicle);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        vehicleService.deleteById(id);
    }

    @DeleteMapping("/older-than")
    public void deleteByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        vehicleService.deleteByRegistrationDateBefore(date);
    }

    @DeleteMapping
    public void deleteAll() {
        vehicleService.deleteAll();
    }
}
