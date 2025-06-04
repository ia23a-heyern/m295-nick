package org.example.m295nick.repositories;

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
class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle createVehicle(String brand, Boolean hasAC) {
        Vehicle v = new Vehicle();
        v.setBrand(brand);
        v.setModel("TestModel");
        v.setFirstRegistration(LocalDate.of(2020, 1, 1));
        v.setHasAirConditioning(hasAC);
        v.setPricePerDay(new BigDecimal("50.00"));
        v.setSeats(4);
        return v;
    }

    @Test
    @DisplayName("findByHasAirConditioning should return only vehicles matching the boolean filter")
    void findByHasAirConditioning_returnsFilteredList() {
        Vehicle v1 = createVehicle("VW", true);
        Vehicle v2 = createVehicle("Audi", false);
        vehicleRepository.save(v1);
        vehicleRepository.save(v2);

        List<Vehicle> withAC = vehicleRepository.findByHasAirConditioning(true);
        List<Vehicle> withoutAC = vehicleRepository.findByHasAirConditioning(false);

        assertThat(withAC).hasSize(1).extracting(Vehicle::getBrand).containsExactly("VW");
        assertThat(withoutAC).hasSize(1).extracting(Vehicle::getBrand).containsExactly("Audi");
    }

    @Test
    @DisplayName("findByBrandContainingIgnoreCase should find partial matches regardless of case")
    void findByBrandContainingIgnoreCase_returnsCorrectVehicles() {
        Vehicle v1 = createVehicle("Volkswagen", true);
        Vehicle v2 = createVehicle("BMW", false);
        Vehicle v3 = createVehicle("vwClassic", true);
        vehicleRepository.save(v1);
        vehicleRepository.save(v2);
        vehicleRepository.save(v3);

        List<Vehicle> result = vehicleRepository.findByBrandContainingIgnoreCase("vw");
        assertThat(result).hasSize(2)
                .extracting(Vehicle::getBrand)
                .containsExactlyInAnyOrder("Volkswagen", "vwClassic");
    }
}
