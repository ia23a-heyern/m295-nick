package org.example.m295nick.services;

import org.example.m295nick.exceptions.ResourceNotFoundException;
import org.example.m295nick.models.Rental;
import org.example.m295nick.models.Vehicle;
import org.example.m295nick.repositories.RentalRepository;
import org.example.m295nick.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private Vehicle sampleVehicle;
    private Rental sampleRental;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Muster‐Fahrzeug für Tests
        sampleVehicle = new Vehicle();
        sampleVehicle.setId(1L);
        sampleVehicle.setBrand("VW");
        sampleVehicle.setModel("Golf");
        sampleVehicle.setFirstRegistration(LocalDate.of(2020, 1, 1));
        sampleVehicle.setHasAirConditioning(true);
        sampleVehicle.setPricePerDay(new BigDecimal("100.00"));
        sampleVehicle.setSeats(5);

        // Muster‐Miete für Tests
        sampleRental = new Rental();
        sampleRental.setId(1L);
        sampleRental.setCustomer("Max Mustermann");
        sampleRental.setStartDate(LocalDate.of(2025, 6, 1));
        sampleRental.setEndDate(LocalDate.of(2025, 6, 3));
        sampleRental.setVehicle(sampleVehicle);
        // totalCost wird in create() oder update() gesetzt, deshalb kann es hier null bleiben
    }

    @Test
    @DisplayName("getById mit existierender ID gibt Rental zurück")
    void whenGetById_existingId_thenReturnRental() {
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(sampleRental));

        Optional<Rental> result = rentalService.getById(1L);
        assertThat(result).isPresent().contains(sampleRental);
        verify(rentalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getById mit nicht existierender ID gibt leeres Optional zurück")
    void whenGetById_nonExistingId_thenReturnEmpty() {
        when(rentalRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Rental> result = rentalService.getById(99L);
        assertThat(result).isEmpty();
        verify(rentalRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("existsById gibt true zurück, wenn Rental existiert")
    void whenExistsById_existing_thenTrue() {
        when(rentalRepository.existsById(1L)).thenReturn(true);

        boolean exists = rentalService.existsById(1L);
        assertThat(exists).isTrue();
        verify(rentalRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("existsById gibt false zurück, wenn Rental nicht existiert")
    void whenExistsById_nonExisting_thenFalse() {
        when(rentalRepository.existsById(2L)).thenReturn(false);

        boolean exists = rentalService.existsById(2L);
        assertThat(exists).isFalse();
        verify(rentalRepository, times(1)).existsById(2L);
    }

    @Test
    @DisplayName("getAll gibt alle Rentals zurück")
    void whenGetAll_thenReturnList() {
        when(rentalRepository.findAll()).thenReturn(List.of(sampleRental));

        List<Rental> all = rentalService.getAll();
        assertThat(all).hasSize(1).contains(sampleRental);
        verify(rentalRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getByStartDateAfter filtert korrekt")
    void whenGetByStartDateAfter_thenReturnFilteredList() {
        Rental r1 = new Rental();
        r1.setId(2L);
        r1.setCustomer("Anna");
        r1.setStartDate(LocalDate.of(2025, 7, 1));
        r1.setEndDate(LocalDate.of(2025, 7, 3));
        r1.setVehicle(sampleVehicle);
        r1.setTotalCost(BigDecimal.ZERO);

        when(rentalRepository.findByStartDateAfter(LocalDate.of(2025, 6, 30)))
                .thenReturn(List.of(r1));

        List<Rental> result = rentalService.getByStartDateAfter(LocalDate.of(2025, 6, 30));
        assertThat(result).hasSize(1).contains(r1);
        verify(rentalRepository, times(1)).findByStartDateAfter(LocalDate.of(2025, 6, 30));
    }

    @Test
    @DisplayName("getByEndDateBefore filtert korrekt")
    void whenGetByEndDateBefore_thenReturnFilteredList() {
        Rental r2 = new Rental();
        r2.setId(3L);
        r2.setCustomer("Bernd");
        r2.setStartDate(LocalDate.of(2023, 1, 1));
        r2.setEndDate(LocalDate.of(2023, 1, 5));
        r2.setVehicle(sampleVehicle);
        r2.setTotalCost(BigDecimal.ZERO);

        when(rentalRepository.findByEndDateBefore(LocalDate.of(2023, 6, 1)))
                .thenReturn(List.of(r2));

        List<Rental> result = rentalService.getByEndDateBefore(LocalDate.of(2023, 6, 1));
        assertThat(result).hasSize(1).contains(r2);
        verify(rentalRepository, times(1)).findByEndDateBefore(LocalDate.of(2023, 6, 1));
    }

    @Test
    @DisplayName("create valid Rental berechnet totalCost und speichert")
    void whenCreate_validRental_thenCalculateTotalCostAndSave() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rental toSave = new Rental();
        toSave.setCustomer("Anna");
        toSave.setStartDate(LocalDate.of(2025, 7, 1));
        toSave.setEndDate(LocalDate.of(2025, 7, 4)); // 4 Tage inklusive
        toSave.setVehicle(sampleVehicle);

        Rental saved = rentalService.create(toSave);

        long expectedDays = java.time.temporal.ChronoUnit.DAYS.between(
                toSave.getStartDate(), toSave.getEndDate()) + 1;
        BigDecimal expectedCost = sampleVehicle.getPricePerDay()
                .multiply(BigDecimal.valueOf(expectedDays));

        assertThat(saved.getTotalCost()).isEqualByComparingTo(expectedCost);
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    @DisplayName("create Rental mit startDate > endDate wirft IllegalArgumentException")
    void whenCreate_invalidDates_thenThrowIllegalArgumentException() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));

        Rental bad = new Rental();
        bad.setCustomer("Anna");
        bad.setStartDate(LocalDate.of(2025, 8, 5));
        bad.setEndDate(LocalDate.of(2025, 8, 3)); // startDate nach endDate
        bad.setVehicle(sampleVehicle);

        assertThatThrownBy(() -> rentalService.create(bad))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Startdatum darf nicht nach dem Enddatum liegen");
        verify(rentalRepository, never()).save(any());
    }

    @Test
    @DisplayName("create Rental ohne vorhandenes Vehicle wirft ResourceNotFoundException")
    void whenCreate_vehicleNotFound_thenThrowResourceNotFoundException() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        Rental r = new Rental();
        r.setCustomer("Anna");
        r.setStartDate(LocalDate.of(2025, 9, 1));
        r.setEndDate(LocalDate.of(2025, 9, 2));
        Vehicle deadRef = new Vehicle();
        deadRef.setId(99L);
        r.setVehicle(deadRef);

        assertThatThrownBy(() -> rentalService.create(r))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle");
        verify(rentalRepository, never()).save(any());
    }

    @Test
    @DisplayName("update bestehender Rental ändert Felder und totalCost")
    void whenUpdate_existingRental_thenReturnUpdated() {
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(sampleRental));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rental updateData = new Rental();
        updateData.setCustomer("Erika");
        updateData.setStartDate(LocalDate.of(2025, 10, 1));
        updateData.setEndDate(LocalDate.of(2025, 10, 2));
        updateData.setVehicle(sampleVehicle);

        Rental updated = rentalService.update(1L, updateData);

        assertThat(updated.getCustomer()).isEqualTo("Erika");
        long days = java.time.temporal.ChronoUnit.DAYS.between(
                updateData.getStartDate(), updateData.getEndDate()) + 1;
        BigDecimal expectedCost = sampleVehicle.getPricePerDay().multiply(BigDecimal.valueOf(days));
        assertThat(updated.getTotalCost()).isEqualByComparingTo(expectedCost);

        verify(rentalRepository, times(1)).findById(1L);
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    @DisplayName("update nicht vorhandener Rental wirft ResourceNotFoundException")
    void whenUpdate_nonExisting_thenThrowResourceNotFoundException() {
        when(rentalRepository.findById(99L)).thenReturn(Optional.empty());

        Rental dummy = new Rental();
        dummy.setCustomer("Nobody");
        dummy.setStartDate(LocalDate.of(2025, 5, 1));
        dummy.setEndDate(LocalDate.of(2025, 5, 2));
        dummy.setVehicle(sampleVehicle);

        assertThatThrownBy(() -> rentalService.update(99L, dummy))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rental");
        verify(rentalRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("deleteById nicht vorhandener Rental wirft ResourceNotFoundException")
    void whenDeleteById_nonExisting_thenThrowResourceNotFoundException() {
        when(rentalRepository.existsById(2L)).thenReturn(false);

        assertThatThrownBy(() -> rentalService.deleteById(2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Rental");
        verify(rentalRepository, times(1)).existsById(2L);
    }

    @Test
    @DisplayName("deleteById vorhandener Rental ruft Repository.deleteById auf")
    void whenDeleteById_existing_thenCallDelete() {
        when(rentalRepository.existsById(1L)).thenReturn(true);
        doNothing().when(rentalRepository).deleteById(1L);

        rentalService.deleteById(1L);

        verify(rentalRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteAll ruft Repository.deleteAll auf")
    void deleteAll_callsRepositoryDeleteAll() {
        doNothing().when(rentalRepository).deleteAll();
        rentalService.deleteAll();
        verify(rentalRepository, times(1)).deleteAll();
    }

    @Test
    @DisplayName("deleteByStartDateAfter filtert und löscht nur gefundene Rentals")
    void deleteByStartDateAfter_deletesFiltered() {
        // Altes Rental (liegt vor 2024-06-01)
        Rental rOld = new Rental();
        rOld.setId(1L);
        rOld.setCustomer("A");
        rOld.setStartDate(LocalDate.of(2024, 1, 1));
        rOld.setEndDate(LocalDate.of(2024, 1, 2));
        rOld.setVehicle(sampleVehicle);
        rOld.setTotalCost(BigDecimal.ZERO);

        // Neues Rental (liegt nach 2024-06-01)
        Rental rNew = new Rental();
        rNew.setId(2L);
        rNew.setCustomer("B");
        rNew.setStartDate(LocalDate.of(2025, 1, 1));
        rNew.setEndDate(LocalDate.of(2025, 1, 2));
        rNew.setVehicle(sampleVehicle);
        rNew.setTotalCost(BigDecimal.ZERO);

        // findByStartDateAfter liefert nur rNew
        when(rentalRepository.findByStartDateAfter(LocalDate.of(2024, 6, 1)))
                .thenReturn(List.of(rNew));

        // Mock deleteAll: nichts tun
        doNothing().when(rentalRepository).deleteAll(anyList());

        // Service‐Aufruf
        rentalService.deleteByStartDateAfter(LocalDate.of(2024, 6, 1));

        // Verifiziere, dass deleteAll(...) mit genau einer Liste [rNew] aufgerufen wurde
        verify(rentalRepository, times(1))
                .deleteAll(argThat((Iterable<Rental> iterable) -> {
                    // Iterable in List umwandeln
                    java.util.List<Rental> tmp = new java.util.ArrayList<>();
                    iterable.forEach(tmp::add);
                    // Prüfen: genau 1 Element und dieses hat ID=2
                    return tmp.size() == 1 && tmp.get(0).getId().equals(2L);
                }));
    }
}
