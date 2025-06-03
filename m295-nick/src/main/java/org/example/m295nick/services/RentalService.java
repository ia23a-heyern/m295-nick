package org.example.m295nick.services;

import lombok.RequiredArgsConstructor;
import org.example.m295nick.models.Rental;
import org.example.m295nick.models.Vehicle;
import org.example.m295nick.repositories.RentalRepository;
import org.example.m295nick.repositories.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public Optional<Rental> getRentalById(Long id) {
        return rentalRepository.findById(id);
    }

    public Rental createRental(Rental rental) {
        Long vehicleId = rental.getVehicle().getId();
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));
        rental.setVehicle(vehicle);
        return rentalRepository.save(rental);
    }

    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }

    public Rental updateRental(Long id, Rental updated) {
        return rentalRepository.findById(id).map(rental -> {
            rental.setCustomer(updated.getCustomer());
            rental.setStartDate(updated.getStartDate());
            rental.setEndDate(updated.getEndDate());
            rental.setTotalCost(updated.getTotalCost());

            Long vehicleId = updated.getVehicle().getId();
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new RuntimeException("Vehicle not found with ID: " + vehicleId));
            rental.setVehicle(vehicle);

            return rentalRepository.save(rental);
        }).orElseThrow(() -> new RuntimeException("Rental not found"));
    }

    public void deleteAllRentals() {
        rentalRepository.deleteAll();
    }
}
