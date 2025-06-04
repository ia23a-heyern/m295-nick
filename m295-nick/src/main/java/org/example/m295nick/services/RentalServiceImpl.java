package org.example.m295nick.services;

import org.example.m295nick.exceptions.ResourceNotFoundException;
import org.example.m295nick.models.Rental;
import org.example.m295nick.models.Vehicle;
import org.example.m295nick.repositories.RentalRepository;
import org.example.m295nick.repositories.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RentalServiceImpl implements RentalService {

    private static final Logger logger = LoggerFactory.getLogger(RentalServiceImpl.class);

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;

    public RentalServiceImpl(RentalRepository rentalRepository,
                             VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Optional<Rental> getById(Long id) {
        logger.debug("Lese Rental mit ID {}", id);
        return rentalRepository.findById(id);
    }

    @Override
    public boolean existsById(Long id) {
        logger.debug("Prüfe Existenz Rental-ID {}", id);
        return rentalRepository.existsById(id);
    }

    @Override
    public List<Rental> getAll() {
        logger.debug("Lese alle Rentals");
        return rentalRepository.findAll();
    }

    @Override
    public List<Rental> getByStartDateAfter(LocalDate date) {
        logger.debug("Filtere Rentals nach StartDate nach {}", date);
        return rentalRepository.findByStartDateAfter(date);
    }

    @Override
    public List<Rental> getByEndDateBefore(LocalDate date) {
        logger.debug("Filtere Rentals nach EndDate vor {}", date);
        return rentalRepository.findByEndDateBefore(date);
    }

    @Override
    public Rental create(Rental rental) {
        logger.debug("Erstelle neuen Rental: {}", rental);

        Long vehicleId = rental.getVehicle().getId();
        Vehicle fahrzeug = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vehicleId));

        // Cross‐Field‐Check: startDate ≤ endDate
        if (rental.getStartDate().isAfter(rental.getEndDate())) {
            throw new IllegalArgumentException("Startdatum darf nicht nach dem Enddatum liegen");
        }

        // Berechne Anzahl der Tage inklusive (z. B. Start 2025-06-01, Ende 2025-06-03 = 3 Tage)
        long tage = ChronoUnit.DAYS.between(rental.getStartDate(), rental.getEndDate()) + 1;

        // pricePerDay ist jetzt BigDecimal
        BigDecimal pricePerDay = fahrzeug.getPricePerDay();

        // Multipliziere: tage × pricePerDay
        BigDecimal tageBD = BigDecimal.valueOf(tage);
        BigDecimal kosten = pricePerDay.multiply(tageBD);

        // Setze totalCost als BigDecimal
        rental.setTotalCost(kosten);

        rental.setVehicle(fahrzeug);
        return rentalRepository.save(rental);
    }

    @Override
    public List<Rental> createAll(List<Rental> rentals) {
        logger.debug("Erstelle mehrere Rentals, Anzahl={}", rentals.size());

        for (Rental r : rentals) {
            Long vid = r.getVehicle().getId();
            Vehicle v = vehicleRepository.findById(vid)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vid));

            if (r.getStartDate().isAfter(r.getEndDate())) {
                throw new IllegalArgumentException("Startdatum darf nicht nach dem Enddatum liegen");
            }

            long tage = ChronoUnit.DAYS.between(r.getStartDate(), r.getEndDate()) + 1;
            BigDecimal pricePerDay = v.getPricePerDay();
            BigDecimal tageBD = BigDecimal.valueOf(tage);
            BigDecimal kosten = pricePerDay.multiply(tageBD);

            r.setTotalCost(kosten);
            r.setVehicle(v);
        }

        return rentalRepository.saveAll(rentals);
    }

    @Override
    public Rental update(Long id, Rental rentalToUpdate) {
        logger.debug("Update Rental mit ID {}: {}", id, rentalToUpdate);
        return rentalRepository.findById(id)
                .map(existing -> {
                    existing.setCustomer(rentalToUpdate.getCustomer());
                    existing.setStartDate(rentalToUpdate.getStartDate());
                    existing.setEndDate(rentalToUpdate.getEndDate());

                    // Neues Fahrzeug ggf. verknüpfen
                    Long newVid = rentalToUpdate.getVehicle().getId();
                    Vehicle v = vehicleRepository.findById(newVid)
                            .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", newVid));
                    existing.setVehicle(v);

                    // Kosten neu berechnen mit BigDecimal
                    long tage = ChronoUnit.DAYS.between(existing.getStartDate(), existing.getEndDate()) + 1;
                    BigDecimal pricePerDay = v.getPricePerDay();
                    BigDecimal tageBD = BigDecimal.valueOf(tage);
                    BigDecimal kosten = pricePerDay.multiply(tageBD);

                    existing.setTotalCost(kosten);
                    return rentalRepository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Rental", "id", id));
    }

    @Override
    public void deleteById(Long id) {
        logger.debug("Lösche Rental mit ID {}", id);
        if (!rentalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rental", "id", id);
        }
        rentalRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        logger.debug("Lösche alle Rentals");
        rentalRepository.deleteAll();
    }

    @Override
    public void deleteByStartDateAfter(LocalDate date) {
        logger.debug("Lösche Rentals mit StartDate nach {}", date);
        List<Rental> zuLoeschen = rentalRepository.findByStartDateAfter(date);
        rentalRepository.deleteAll(zuLoeschen);
    }
}
