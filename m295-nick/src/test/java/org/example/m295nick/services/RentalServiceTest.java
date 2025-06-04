package org.example.m295nick.services;

import org.example.m295nick.exceptions.ResourceNotFoundException;
import org.example.m295nick.models.Rental;
import org.example.m295nick.models.Vehicle;
import org.example.m295nick.repositories.RentalRepository;
import org.example.m295nick.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private Vehicle sampleVehicle;
    private Vehicle anotherVehicle;
    private Rental sampleRental;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Beispiel‐Fahrzeug
        sampleVehicle = new Vehicle();
        sampleVehicle.setId(1L);
        sampleVehicle.setBrand("VW");
        sampleVehicle.setModel("Golf");
        sampleVehicle.setFirstRegistration(LocalDate.of(2020, 1, 1));
        sampleVehicle.setHasAirConditioning(true);
        sampleVehicle.setPricePerDay(new BigDecimal("100.00"));
        sampleVehicle.setSeats(5);

        anotherVehicle = new Vehicle();
        anotherVehicle.setId(2L);
        anotherVehicle.setBrand("BMW");
        anotherVehicle.setModel("X3");
        anotherVehicle.setFirstRegistration(LocalDate.of(2021, 3, 15));
        anotherVehicle.setHasAirConditioning(true);
        anotherVehicle.setPricePerDay(new BigDecimal("150.00"));
        anotherVehicle.setSeats(5);

        // Beispiel‐Miete
        sampleRental = new Rental();
        sampleRental.setId(1L);
        sampleRental.setCustomer("Max Mustermann");
        sampleRental.setStartDate(LocalDate.of(2025, 6, 1));
        sampleRental.setEndDate(LocalDate.of(2025, 6, 3));
        sampleRental.setVehicle(sampleVehicle);
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
    @DisplayName("getByStartDateAfter filtert korrekt mit Ergebnissen")
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
    @DisplayName("getByStartDateAfter filtert korrekt ohne Ergebnisse")
    void whenGetByStartDateAfter_thenReturnEmptyList() {
        when(rentalRepository.findByStartDateAfter(LocalDate.of(2025, 12, 31)))
                .thenReturn(Collections.emptyList());

        List<Rental> result = rentalService.getByStartDateAfter(LocalDate.of(2025, 12, 31));
        assertThat(result).isEmpty();
        verify(rentalRepository, times(1)).findByStartDateAfter(LocalDate.of(2025, 12, 31));
    }

    @Test
    @DisplayName("getByEndDateBefore filtert korrekt mit Ergebnissen")
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
    @DisplayName("getByEndDateBefore filtert korrekt ohne Ergebnisse")
    void whenGetByEndDateBefore_thenReturnEmptyList() {
        when(rentalRepository.findByEndDateBefore(LocalDate.of(2000, 1, 1)))
                .thenReturn(Collections.emptyList());

        List<Rental> result = rentalService.getByEndDateBefore(LocalDate.of(2000, 1, 1));
        assertThat(result).isEmpty();
        verify(rentalRepository, times(1)).findByEndDateBefore(LocalDate.of(2000, 1, 1));
    }

    @Test
    @DisplayName("create valid Rental berechnet totalCost für mehrere Tage und speichert")
    void whenCreate_validRentalMultipleDays_thenCalculateTotalCostAndSave() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rental toSave = new Rental();
        toSave.setCustomer("Anna");
        toSave.setStartDate(LocalDate.of(2025, 7, 1));
        toSave.setEndDate(LocalDate.of(2025, 7, 4)); // 4 Tage
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
    @DisplayName("create valid Rental berechnet totalCost für einen Tag und speichert")
    void whenCreate_validRentalSingleDay_thenCalculateTotalCostAndSave() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rental toSave = new Rental();
        toSave.setCustomer("Ben");
        toSave.setStartDate(LocalDate.of(2025, 8, 15));
        toSave.setEndDate(LocalDate.of(2025, 8, 15)); // 1 Tag
        toSave.setVehicle(sampleVehicle);

        Rental saved = rentalService.create(toSave);

        // 1 Tag * 100.00 = 100.00
        BigDecimal expectedCost = sampleVehicle.getPricePerDay().multiply(BigDecimal.valueOf(1));
        assertThat(saved.getTotalCost()).isEqualByComparingTo(expectedCost);
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    @DisplayName("create Rental mit startDate > endDate wirft IllegalArgumentException")
    void whenCreate_invalidDates_thenThrowIllegalArgumentException() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));

        Rental bad = new Rental();
        bad.setCustomer("Cara");
        bad.setStartDate(LocalDate.of(2025, 9, 10));
        bad.setEndDate(LocalDate.of(2025, 9, 8)); // Start > Ende
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
        r.setCustomer("Dana");
        r.setStartDate(LocalDate.of(2025, 10, 1));
        r.setEndDate(LocalDate.of(2025, 10, 2));
        Vehicle missing = new Vehicle();
        missing.setId(99L);
        r.setVehicle(missing);

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
        verifyvehicleCalledNever:
        verify(vehicleRepository, never()).findById(anyLong());
        verify(rentalRepository, never()).save(any());
    }

    @Test
    @DisplayName("update mit nicht vorhandenem Vehicle wirft ResourceNotFoundException")
    void whenUpdate_existingRentalVehicleNotFound_thenThrowResourceNotFoundException() {
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(sampleRental));
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        Rental updateData = new Rental();
        updateData.setCustomer("Felix");
        updateData.setStartDate(LocalDate.of(2025, 12, 1));
        updateData.setEndDate(LocalDate.of(2025, 12, 2));
        Vehicle missing = new Vehicle();
        missing.setId(99L);
        updateData.setVehicle(missing);

        assertThatThrownBy(() -> rentalService.update(1L, updateData))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle");

        verify(rentalRepository, times(1)).findById(1L);
        verify(rentalRepository, never()).save(any());
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
    @DisplayName("deleteByStartDateAfter filtert und löscht vorhandene Rentals")
    void deleteByStartDateAfter_deletesFiltered() {
        Rental old = new Rental();
        old.setId(1L);
        old.setCustomer("A");
        old.setStartDate(LocalDate.of(2024, 1, 1));
        old.setEndDate(LocalDate.of(2024, 1, 2));
        old.setVehicle(sampleVehicle);
        old.setTotalCost(BigDecimal.ZERO);

        Rental future = new Rental();
        future.setId(2L);
        future.setCustomer("B");
        future.setStartDate(LocalDate.of(2025, 1, 1));
        future.setEndDate(LocalDate.of(2025, 1, 2));
        future.setVehicle(sampleVehicle);
        future.setTotalCost(BigDecimal.ZERO);

        when(rentalRepository.findByStartDateAfter(LocalDate.of(2024, 6, 1)))
                .thenReturn(List.of(future));
        doNothing().when(rentalRepository).deleteAll(anyList());

        rentalService.deleteByStartDateAfter(LocalDate.of(2024, 6, 1));

        ArgumentCaptor<Iterable<Rental>> captor = ArgumentCaptor.forClass(Iterable.class);
        verify(rentalRepository, times(1)).deleteAll(captor.capture());
        List<Rental> deleted = List.copyOf((Collection<? extends Rental>) captor.getValue());
        assertThat(deleted).hasSize(1).extracting(Rental::getId).containsExactly(2L);
    }

    @Test
    @DisplayName("deleteByStartDateAfter ohne gefundene Rentals ruft deleteAll mit leerer Liste")
    void deleteByStartDateAfter_noMatches_deletesNothing() {
        when(rentalRepository.findByStartDateAfter(LocalDate.of(2030, 1, 1)))
                .thenReturn(Collections.emptyList());
        doNothing().when(rentalRepository).deleteAll(anyList());

        rentalService.deleteByStartDateAfter(LocalDate.of(2030, 1, 1));

        ArgumentCaptor<Iterable<Rental>> captor = ArgumentCaptor.forClass(Iterable.class);
        verify(rentalRepository, times(1)).deleteAll(captor.capture());
        List<Rental> deleted = List.copyOf((Collection<? extends Rental>) captor.getValue());
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("createAll mit gültigen Rentals speichert alle korrekt")
    void whenCreateAll_validRentals_thenCalculateTotalCostAndSaveAll() {
        Rental r1 = new Rental();
        r1.setCustomer("Klaus");
        r1.setStartDate(LocalDate.of(2025, 9, 1));
        r1.setEndDate(LocalDate.of(2025, 9, 3));
        r1.setVehicle(sampleVehicle);

        Rental r2 = new Rental();
        r2.setCustomer("Lisa");
        r2.setStartDate(LocalDate.of(2025, 10, 1));
        r2.setEndDate(LocalDate.of(2025, 10, 1));
        r2.setVehicle(anotherVehicle);

        when(vehicleRepository.findById(sampleVehicle.getId())).thenReturn(Optional.of(sampleVehicle));
        when(vehicleRepository.findById(anotherVehicle.getId())).thenReturn(Optional.of(anotherVehicle));
        when(rentalRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Rental> rentals = List.of(r1, r2);

        List<Rental> savedRentals = rentalService.createAll(rentals);

        assertThat(savedRentals).hasSize(2);
        assertThat(savedRentals.get(0).getTotalCost())
                .isEqualByComparingTo(sampleVehicle.getPricePerDay().multiply(BigDecimal.valueOf(3)));
        assertThat(savedRentals.get(1).getTotalCost())
                .isEqualByComparingTo(anotherVehicle.getPricePerDay().multiply(BigDecimal.valueOf(1)));

        verify(vehicleRepository, times(1)).findById(sampleVehicle.getId());
        verify(vehicleRepository, times(1)).findById(anotherVehicle.getId());
        verify(rentalRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("createAll mit ungültigen Daten wirft IllegalArgumentException")
    void whenCreateAll_invalidDates_thenThrowIllegalArgumentException() {
        Rental r = new Rental();
        r.setCustomer("Invalid");
        r.setStartDate(LocalDate.of(2025, 10, 10));
        r.setEndDate(LocalDate.of(2025, 10, 5)); // Start > Ende
        r.setVehicle(sampleVehicle);

        when(vehicleRepository.findById(sampleVehicle.getId())).thenReturn(Optional.of(sampleVehicle));

        List<Rental> rentals = List.of(r);

        assertThatThrownBy(() -> rentalService.createAll(rentals))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Startdatum darf nicht nach dem Enddatum liegen");

        verify(rentalRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("createAll mit fehlendem Fahrzeug wirft ResourceNotFoundException")
    void whenCreateAll_vehicleNotFound_thenThrowResourceNotFoundException() {
        Rental r = new Rental();
        r.setCustomer("NoVehicle");
        r.setStartDate(LocalDate.of(2025, 11, 1));
        r.setEndDate(LocalDate.of(2025, 11, 2));
        Vehicle missing = new Vehicle();
        missing.setId(99L);
        r.setVehicle(missing);

        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        List<Rental> rentals = List.of(r);

        assertThatThrownBy(() -> rentalService.createAll(rentals))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle");

        verify(rentalRepository, never()).saveAll(anyList());
    }

}
