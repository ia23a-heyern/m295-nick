package org.example.m295nick.repositories;

import org.example.m295nick.models.Rental;
import org.example.m295nick.models.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RentalRepositoryTest {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle createVehicle() {
        Vehicle v = new Vehicle();
        v.setBrand("TestBrand");
        v.setModel("TestModel");
        v.setFirstRegistration(LocalDate.of(2022, 1, 1));
        v.setHasAirConditioning(true);
        v.setPricePerDay(new BigDecimal("30.00"));
        v.setSeats(5);
        return vehicleRepository.save(v);
    }

    private Rental createRental(Vehicle v, LocalDate start, LocalDate end) {
        Rental r = new Rental();
        r.setCustomer("Tester");
        r.setStartDate(start);
        r.setEndDate(end);
        // totalCost wird nicht benötigt, denn wir speichern manuell:
        r.setTotalCost(BigDecimal.ZERO);
        r.setVehicle(v);
        return r;
    }

    @Test
    @DisplayName("findByStartDateAfter should return only rentals after given start date")
    void findByStartDateAfter_filtersCorrectly() {
        Vehicle v = createVehicle();
        Rental r1 = createRental(v, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 3));
        Rental r2 = createRental(v, LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 5));
        rentalRepository.save(r1);
        rentalRepository.save(r2);

        List<Rental> result = rentalRepository.findByStartDateAfter(LocalDate.of(2024, 12, 31));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStartDate()).isEqualTo(LocalDate.of(2025, 1, 1));
    }

    @Test
    @DisplayName("findByEndDateBefore should return only rentals ending before given end date")
    void findByEndDateBefore_filtersCorrectly() {
        Vehicle v = createVehicle();
        Rental r1 = createRental(v, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 5));
        Rental r2 = createRental(v, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 10));
        rentalRepository.save(r1);
        rentalRepository.save(r2);

        List<Rental> result = rentalRepository.findByEndDateBefore(LocalDate.of(2023, 6, 1));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEndDate()).isEqualTo(LocalDate.of(2023, 1, 5));
    }
}
