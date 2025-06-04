package org.example.m295nick.services;

import org.example.m295nick.exceptions.ResourceNotFoundException;
import org.example.m295nick.models.Vehicle;
import org.example.m295nick.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private Vehicle sampleVehicle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleVehicle = new Vehicle();
        sampleVehicle.setId(1L);
        sampleVehicle.setBrand("VW");
        sampleVehicle.setModel("Golf");
        sampleVehicle.setFirstRegistration(LocalDate.of(2020, 1, 1));
        sampleVehicle.setHasAirConditioning(true);
        sampleVehicle.setPricePerDay(new BigDecimal("50.00"));
        sampleVehicle.setSeats(5);
    }

    @Test
    @DisplayName("getById mit existierender ID liefert Optional<Vehicle>")
    void whenGetById_existingId_thenReturnVehicle() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));

        Optional<Vehicle> result = vehicleService.getById(1L);
        assertThat(result).isPresent().contains(sampleVehicle);
        verify(vehicleRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getById mit nicht existierender ID liefert leeres Optional")
    void whenGetById_nonExistingId_thenReturnEmpty() {
        when(vehicleRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Vehicle> result = vehicleService.getById(2L);
        assertThat(result).isEmpty();
        verify(vehicleRepository, times(1)).findById(2L);
    }

    @Test
    @DisplayName("create valid Vehicle ruft save() auf und gibt das gespeicherte Entity zurück")
    void whenCreate_validVehicle_thenSaveAndReturn() {
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(sampleVehicle);

        Vehicle toSave = new Vehicle();
        toSave.setBrand("Audi");
        toSave.setModel("A3");
        toSave.setFirstRegistration(LocalDate.of(2022, 5, 5));
        toSave.setHasAirConditioning(false);
        toSave.setPricePerDay(new BigDecimal("60.00"));
        toSave.setSeats(4);

        Vehicle saved = vehicleService.create(toSave);
        // Da vehicleRepository.save(...) gemockt ist, wird immer sampleVehicle zurückgegeben
        assertThat(saved).isSameAs(sampleVehicle);
        verify(vehicleRepository, times(1)).save(toSave);
    }

    @Test
    @DisplayName("create Vehicle älter als 30 Jahre wirft IllegalArgumentException")
    void whenCreate_oldVehicle_thenThrowIllegalArgumentException() {
        Vehicle oldVehicle = new Vehicle();
        oldVehicle.setBrand("Oldtimer");
        oldVehicle.setModel("Classic");
        oldVehicle.setFirstRegistration(LocalDate.now().minusYears(31));
        oldVehicle.setHasAirConditioning(false);
        oldVehicle.setPricePerDay(new BigDecimal("20.00"));
        oldVehicle.setSeats(4);

        assertThatThrownBy(() -> vehicleService.create(oldVehicle))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("älter als 30 Jahre");
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("update existierendes Vehicle passt Felder an und gibt aktualisiertes Entity zurück")
    void whenUpdate_existingVehicle_thenReturnUpdated() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(sampleVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Vehicle updateData = new Vehicle();
        updateData.setBrand("Seat");
        updateData.setModel("Ibiza");
        updateData.setFirstRegistration(LocalDate.of(2021, 3, 3));
        updateData.setHasAirConditioning(true);
        updateData.setPricePerDay(new BigDecimal("45.00"));
        updateData.setSeats(4);

        Vehicle result = vehicleService.update(1L, updateData);
        assertThat(result.getBrand()).isEqualTo("Seat");
        assertThat(result.getModel()).isEqualTo("Ibiza");
        assertThat(result.getPricePerDay()).isEqualByComparingTo("45.00");
        verify(vehicleRepository, times(1)).findById(1L);
        verify(vehicleRepository, times(1)).save(sampleVehicle);
    }

    @Test
    @DisplayName("update nicht existierendes Vehicle wirft ResourceNotFoundException")
    void whenUpdate_nonExistingVehicle_thenThrowResourceNotFoundException() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        Vehicle dummy = new Vehicle();
        dummy.setBrand("Test");
        dummy.setModel("TestModel");
        dummy.setFirstRegistration(LocalDate.of(2021, 1, 1));
        dummy.setHasAirConditioning(true);
        dummy.setPricePerDay(new BigDecimal("30.00"));
        dummy.setSeats(3);

        assertThatThrownBy(() -> vehicleService.update(99L, dummy))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle wurde nicht gefunden");
        verify(vehicleRepository, times(1)).findById(99L);
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteById nicht existierende ID wirft ResourceNotFoundException")
    void whenDeleteById_nonExisting_thenThrowResourceNotFoundException() {
        when(vehicleRepository.existsById(2L)).thenReturn(false);

        assertThatThrownBy(() -> vehicleService.deleteById(2L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle wurde nicht gefunden");
        verify(vehicleRepository, times(1)).existsById(2L);
        verify(vehicleRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deleteById existierende ID ruft deleteById() auf")
    void whenDeleteById_existing_thenCallDeleteById() {
        when(vehicleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(vehicleRepository).deleteById(1L);

        vehicleService.deleteById(1L);
        verify(vehicleRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteAll ruft repository.deleteAll() auf")
    void whenDeleteAll_thenCallRepositoryDeleteAll() {
        doNothing().when(vehicleRepository).deleteAll();
        vehicleService.deleteAll();
        verify(vehicleRepository, times(1)).deleteAll();
    }

    @Test
    @DisplayName("deleteByFirstRegistrationBefore filtert korrekt und löscht betroffene Fahrzeuge")
    void deleteByFirstRegistrationBefore_deletesCorrectOnes() {
        Vehicle vOld = createVehicle("Old", LocalDate.of(1980, 1, 1));
        Vehicle vNew = createVehicle("New", LocalDate.of(2020, 1, 1));

        when(vehicleRepository.findAll()).thenReturn(List.of(vOld, vNew));
        doNothing().when(vehicleRepository).deleteAll(anyList());

        vehicleService.deleteByFirstRegistrationBefore(LocalDate.of(2000, 1, 1));

        verify(vehicleRepository, times(1))
                .deleteAll(argThat((Iterable<Vehicle> iterable) -> {
                    // Aus Iterable eine List machen
                    List<Vehicle> tmp = new ArrayList<>();
                    iterable.forEach(tmp::add);
                    return tmp.size() == 1 && tmp.get(0).getBrand().equals("Old");
                }));
    }

    @Test
    @DisplayName("existsById gibt true zurück, wenn Vehicle existiert")
    void whenExistsById_existing_thenTrue() {
        when(vehicleRepository.existsById(1L)).thenReturn(true);

        boolean exists = vehicleService.existsById(1L);

        assertThat(exists).isTrue();
        verify(vehicleRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("existsById gibt false zurück, wenn Vehicle nicht existiert")
    void whenExistsById_nonExisting_thenFalse() {
        when(vehicleRepository.existsById(99L)).thenReturn(false);

        boolean exists = vehicleService.existsById(99L);

        assertThat(exists).isFalse();
        verify(vehicleRepository, times(1)).existsById(99L);
    }

    @Test
    @DisplayName("getAll liefert Liste aller Fahrzeuge")
    void whenGetAll_thenReturnList() {
        List<Vehicle> list = List.of(sampleVehicle);
        when(vehicleRepository.findAll()).thenReturn(list);

        List<Vehicle> result = vehicleService.getAll();

        assertThat(result).isEqualTo(list);
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getByAirConditioning filtert Fahrzeuge korrekt")
    void whenGetByAirConditioning_thenReturnFilteredList() {
        List<Vehicle> acVehicles = List.of(sampleVehicle);
        when(vehicleRepository.findByHasAirConditioning(true)).thenReturn(acVehicles);

        List<Vehicle> result = vehicleService.getByAirConditioning(true);

        assertThat(result).isEqualTo(acVehicles);
        verify(vehicleRepository, times(1)).findByHasAirConditioning(true);
    }

    @Test
    @DisplayName("getByBrand filtert Fahrzeuge nach Markenname")
    void whenGetByBrand_thenReturnFilteredList() {
        List<Vehicle> brandVehicles = List.of(sampleVehicle);
        when(vehicleRepository.findByBrandContainingIgnoreCase("vw")).thenReturn(brandVehicles);

        List<Vehicle> result = vehicleService.getByBrand("vw");

        assertThat(result).isEqualTo(brandVehicles);
        verify(vehicleRepository, times(1)).findByBrandContainingIgnoreCase("vw");
    }

    @Test
    @DisplayName("createAll mit gültigen Fahrzeugen speichert alle")
    void whenCreateAll_validVehicles_thenSaveAll() {
        Vehicle v1 = createVehicle("Ford", LocalDate.now().minusYears(5));
        Vehicle v2 = createVehicle("Toyota", LocalDate.now().minusYears(10));

        List<Vehicle> vehicles = List.of(v1, v2);

        when(vehicleRepository.saveAll(vehicles)).thenReturn(vehicles);

        List<Vehicle> saved = vehicleService.createAll(vehicles);

        assertThat(saved).isEqualTo(vehicles);
        verify(vehicleRepository, times(1)).saveAll(vehicles);
    }

    @Test
    @DisplayName("createAll mit mindestens einem alten Fahrzeug wirft IllegalArgumentException")
    void whenCreateAll_withOldVehicle_thenThrowIllegalArgumentException() {
        Vehicle v1 = createVehicle("OldCar", LocalDate.now().minusYears(31));
        Vehicle v2 = createVehicle("NewCar", LocalDate.now().minusYears(10));

        List<Vehicle> vehicles = List.of(v1, v2);

        assertThatThrownBy(() -> vehicleService.createAll(vehicles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Mindestens ein Fahrzeug ist älter als 30 Jahre");

        verify(vehicleRepository, never()).saveAll(anyList());
    }


    // Hilfsmethode, um ein Fahrzeug einfach zu erzeugen
    private Vehicle createVehicle(String brand, LocalDate date) {
        Vehicle v = new Vehicle();
        v.setBrand(brand);
        v.setModel("X");
        v.setFirstRegistration(date);
        v.setHasAirConditioning(true);
        v.setPricePerDay(new BigDecimal("20.00"));
        v.setSeats(2);
        return v;
    }
}
