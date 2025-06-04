package org.example.m295nick.services;

import org.example.m295nick.exceptions.ResourceNotFoundException;
import org.example.m295nick.models.Vehicle;
import org.example.m295nick.repositories.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleServiceImpl.class);

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Optional<Vehicle> getById(Long id) {
        logger.debug("Lese Fahrzeug mit ID {}", id);
        return vehicleRepository.findById(id);
    }

    @Override
    public boolean existsById(Long id) {
        logger.debug("Prüfe Existenz Fahrzeug-ID {}", id);
        return vehicleRepository.existsById(id);
    }

    @Override
    public List<Vehicle> getAll() {
        logger.debug("Lese alle Fahrzeuge");
        return vehicleRepository.findAll();
    }

    @Override
    public List<Vehicle> getByAirConditioning(Boolean hasAir) {
        logger.debug("Filtere Fahrzeuge nach Klimaanlage = {}", hasAir);
        return vehicleRepository.findByHasAirConditioning(hasAir);
    }

    @Override
    public List<Vehicle> getByBrand(String brandPart) {
        logger.debug("Filtere Fahrzeuge nach Marke enthält '{}'", brandPart);
        return vehicleRepository.findByBrandContainingIgnoreCase(brandPart);
    }

    @Override
    public Vehicle create(Vehicle vehicle) {
        logger.debug("Erstelle neues Fahrzeug: {}", vehicle);
        // Business‐Regel: Fahrzeugalter ≤ 30 Jahre
        if (vehicle.getFirstRegistration().isBefore(LocalDate.now().minusYears(30))) {
            throw new IllegalArgumentException("Fahrzeug darf nicht älter als 30 Jahre sein");
        }
        return vehicleRepository.save(vehicle);
    }

    @Override
    public List<Vehicle> createAll(List<Vehicle> vehicles) {
        logger.debug("Erstelle mehrere Fahrzeuge: Anzahl={}", vehicles.size());
        for (Vehicle v : vehicles) {
            if (v.getFirstRegistration().isBefore(LocalDate.now().minusYears(30))) {
                throw new IllegalArgumentException("Mindestens ein Fahrzeug ist älter als 30 Jahre");
            }
        }
        return vehicleRepository.saveAll(vehicles);
    }

    @Override
    public Vehicle update(Long id, Vehicle vehicleToUpdate) {
        logger.debug("Update Fahrzeug mit ID {}: {}", id, vehicleToUpdate);
        return vehicleRepository.findById(id)
                .map(existing -> {
                    existing.setBrand(vehicleToUpdate.getBrand());
                    existing.setModel(vehicleToUpdate.getModel());
                    existing.setFirstRegistration(vehicleToUpdate.getFirstRegistration());
                    existing.setHasAirConditioning(vehicleToUpdate.getHasAirConditioning());
                    existing.setPricePerDay(vehicleToUpdate.getPricePerDay());
                    existing.setSeats(vehicleToUpdate.getSeats());
                    return vehicleRepository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
    }

    @Override
    public void deleteById(Long id) {
        logger.debug("Lösche Fahrzeug mit ID {}", id);
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle", "id", id);
        }
        vehicleRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        logger.debug("Lösche alle Fahrzeuge");
        vehicleRepository.deleteAll();
    }

    @Override
    public void deleteByFirstRegistrationBefore(LocalDate date) {
        logger.debug("Lösche Fahrzeuge mit Erstzulassung vor {}", date);
        List<Vehicle> zuLoeschende = vehicleRepository.findAll().stream()
                .filter(v -> v.getFirstRegistration().isBefore(date))
                .toList();
        vehicleRepository.deleteAll(zuLoeschende);
    }
}
