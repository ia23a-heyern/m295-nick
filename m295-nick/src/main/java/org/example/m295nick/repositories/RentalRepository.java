package org.example.m295nick.repositories;

import org.example.m295nick.models.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    // Filter: alle Rentals, die an oder nach einem bestimmten Startdatum beginnen
    List<Rental> findByStartDateAfter(LocalDate date);

    // Filter: alle Rentals, die vor einem bestimmten Enddatum enden
    List<Rental> findByEndDateBefore(LocalDate date);
}
