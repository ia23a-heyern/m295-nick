package org.example.m295nick.controllers;

import org.example.m295nick.exceptions.ResourceNotFoundException;
import org.example.m295nick.models.Vehicle;
import org.example.m295nick.services.VehicleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/vehicles", produces = "application/json")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // ─────── READ ───────

    /** 1) GET /api/v1/vehicles/{id} → Fahrzeug nach ID lesen */
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        return ResponseEntity.ok(vehicle);
    }

    /** 2) HEAD /api/v1/vehicles/{id} → Existenz prüfen */
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> existsById(@PathVariable Long id) {
        boolean exists = vehicleService.existsById(id);
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /** 3) GET /api/v1/vehicles → Alle Fahrzeuge lesen */
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> alle = vehicleService.getAll();
        return ResponseEntity.ok(alle);
    }

    /** 4) GET /api/v1/vehicles/filter/air-conditioning?enabled=true */
    @GetMapping("/filter/air-conditioning")
    public ResponseEntity<List<Vehicle>> getByAirConditioning(@RequestParam Boolean enabled) {
        List<Vehicle> filtered = vehicleService.getByAirConditioning(enabled);
        return ResponseEntity.ok(filtered);
    }

    /** 5) GET /api/v1/vehicles/filter/brand?brand=VW */
    @GetMapping("/filter/brand")
    public ResponseEntity<List<Vehicle>> getByBrand(@RequestParam String brand) {
        List<Vehicle> filtered = vehicleService.getByBrand(brand);
        return ResponseEntity.ok(filtered);
    }

    // ─────── CREATE ───────

    /** 6) POST /api/v1/vehicles → Ein Fahrzeug anlegen */
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody Vehicle vehicle) {
        Vehicle created = vehicleService.create(vehicle);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /** 7) POST /api/v1/vehicles/bulk → Mehrere Fahrzeuge anlegen */
    @PostMapping(path = "/bulk", consumes = "application/json")
    public ResponseEntity<List<Vehicle>> createVehiclesBulk(@Valid @RequestBody List<Vehicle> vehicles) {
        List<Vehicle> createdList = vehicleService.createAll(vehicles);
        return new ResponseEntity<>(createdList, HttpStatus.CREATED);
    }

    // ─────── UPDATE ───────

    /** 8) PUT /api/v1/vehicles/{id} → Fahrzeug updaten */
    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<Vehicle> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody Vehicle vehicleRequest) {

        Vehicle updated = vehicleService.update(id, vehicleRequest);
        return ResponseEntity.ok(updated);
    }

    // ─────── DELETE ───────

    /** 9) DELETE /api/v1/vehicles/{id} → Fahrzeug nach ID löschen */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicleById(@PathVariable Long id) {
        vehicleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** 10) DELETE /api/v1/vehicles/filter/first-registration?before=2020-01-01 */
    @DeleteMapping("/filter/first-registration")
    public ResponseEntity<Void> deleteVehiclesByFirstRegistrationBefore(
            @RequestParam("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beforeDate) {
        vehicleService.deleteByFirstRegistrationBefore(beforeDate);
        return ResponseEntity.noContent().build();
    }

    /** 11) DELETE /api/v1/vehicles → Alle Fahrzeuge löschen */
    @DeleteMapping
    public ResponseEntity<Void> deleteAllVehicles() {
        vehicleService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
