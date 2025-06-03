package org.example.m295nick.controllers;

import jakarta.validation.Valid;
import org.example.m295nick.models.Rental;
import org.example.m295nick.services.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@Validated
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Rental create(@Valid @RequestBody Rental rental) {
        return rentalService.createRental(rental);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Rental update(@PathVariable Long id, @Valid @RequestBody Rental rental) {
        return rentalService.updateRental(id, rental);
    }

    // ... weitere Methoden (delete etc.) falls nötig ...
}
