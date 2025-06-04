package org.example.m295nick.controllers;

import org.example.m295nick.exceptions.ResourceNotFoundException;
import org.example.m295nick.models.Rental;
import org.example.m295nick.services.RentalService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/rentals", produces = "application/json")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    // ─────── READ ───────

    /** GET /api/v1/rentals/{id} → Eine Miete nach ID lesen */
    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        Rental rental = rentalService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", id));
        return ResponseEntity.ok(rental);
    }

    /** HEAD /api/v1/rentals/{id} → Existenz prüfen */
    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> existsById(@PathVariable Long id) {
        boolean exists = rentalService.existsById(id);
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /** GET /api/v1/rentals → Alle Rentals lesen */
    @GetMapping
    public ResponseEntity<List<Rental>> getAllRentals() {
        List<Rental> alle = rentalService.getAll();
        return ResponseEntity.ok(alle);
    }

    /** GET /api/v1/rentals/filter/start-after?after=2024-01-01 */
    @GetMapping("/filter/start-after")
    public ResponseEntity<List<Rental>> getRentalsByStartDateAfter(
            @RequestParam("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate afterDate) {
        List<Rental> filtered = rentalService.getByStartDateAfter(afterDate);
        return ResponseEntity.ok(filtered);
    }

    /** GET /api/v1/rentals/filter/end-before?before=2024-12-31 */
    @GetMapping("/filter/end-before")
    public ResponseEntity<List<Rental>> getRentalsByEndDateBefore(
            @RequestParam("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beforeDate) {
        List<Rental> filtered = rentalService.getByEndDateBefore(beforeDate);
        return ResponseEntity.ok(filtered);
    }

    // ─────── CREATE ───────

    /** POST /api/v1/rentals → Eine Miete anlegen */
    @PostMapping(consumes = "application/json")
    public ResponseEntity<Rental> createRental(@Valid @RequestBody Rental rental) {
        Rental created = rentalService.create(rental);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /** POST /api/v1/rentals/bulk → Mehrere Rentals anlegen */
    @PostMapping(path = "/bulk", consumes = "application/json")
    public ResponseEntity<List<Rental>> createRentalsBulk(@Valid @RequestBody List<Rental> rentals) {
        List<Rental> createdList = rentalService.createAll(rentals);
        return new ResponseEntity<>(createdList, HttpStatus.CREATED);
    }

    // ─────── UPDATE ───────

    /** PUT /api/v1/rentals/{id} → Eine Miete aktualisieren */
    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<Rental> updateRental(
            @PathVariable Long id,
            @Valid @RequestBody Rental rentalRequest) {
        Rental updated = rentalService.update(id, rentalRequest);
        return ResponseEntity.ok(updated);
    }

    // ─────── DELETE ───────

    /** DELETE /api/v1/rentals/{id} → Miete nach ID löschen */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRentalById(@PathVariable Long id) {
        rentalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** DELETE /api/v1/rentals/filter/start-after?after=2024-01-01 */
    @DeleteMapping("/filter/start-after")
    public ResponseEntity<Void> deleteRentalsByStartDateAfter(
            @RequestParam("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate afterDate) {
        rentalService.deleteByStartDateAfter(afterDate);
        return ResponseEntity.noContent().build();
    }

    /** DELETE /api/v1/rentals → Alle Rentals löschen */
    @DeleteMapping
    public ResponseEntity<Void> deleteAllRentals() {
        rentalService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
