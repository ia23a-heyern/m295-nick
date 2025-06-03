package org.example.m295nick.controllers;

import org.example.m295nick.models.Rental;
import org.example.m295nick.services.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    // 🔓 GET ist für beide Rollen erlaubt
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public List<Rental> getAll() {
        return rentalService.getAllRentals();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Rental> getById(@PathVariable Long id) {
        return rentalService.getRentalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✏️ Nur Admin darf POST
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Rental create(@RequestBody Rental rental) {
        return rentalService.createRental(rental);
    }

    // ✏️ Nur Admin darf PUT
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Rental update(@PathVariable Long id, @RequestBody Rental rental) {
        return rentalService.updateRental(id, rental);
    }

    // 🗑️ Nur Admin darf DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        rentalService.deleteRental(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public void deleteAll() {
        rentalService.deleteAllRentals();
    }
}
