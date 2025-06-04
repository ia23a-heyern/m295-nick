package org.example.m295nick.services;

import org.example.m295nick.models.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentalService {

    // ─── Read (CRUD) ───
    Optional<Rental> getById(Long id);
    boolean existsById(Long id);
    List<Rental> getAll();
    List<Rental> getByStartDateAfter(LocalDate date);
    List<Rental> getByEndDateBefore(LocalDate date);

    // ─── Create ───
    Rental create(Rental rental);
    List<Rental> createAll(List<Rental> rentals);

    // ─── Update ───
    Rental update(Long id, Rental rentalToUpdate);

    // ─── Delete ───
    void deleteById(Long id);
    void deleteAll();
    void deleteByStartDateAfter(LocalDate date);
}
