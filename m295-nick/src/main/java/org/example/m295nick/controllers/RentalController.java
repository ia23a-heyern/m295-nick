package org.example.m295nick.controllers;

import org.example.m295nick.models.Rental;
import org.example.m295nick.services.RentalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    // 👇 Manueller Konstruktor statt @RequiredArgsConstructor
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public List<Rental> getAll() {
        return rentalService.getAllRentals();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rental> getById(@PathVariable Long id) {
        return rentalService.getRentalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Rental create(@RequestBody Rental rental) {
        return rentalService.createRental(rental);
    }

    @PutMapping("/{id}")
    public Rental update(@PathVariable Long id, @RequestBody Rental rental) {
        return rentalService.updateRental(id, rental);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        rentalService.deleteRental(id);
    }

    @DeleteMapping
    public void deleteAll() {
        rentalService.deleteAllRentals();
    }
}
