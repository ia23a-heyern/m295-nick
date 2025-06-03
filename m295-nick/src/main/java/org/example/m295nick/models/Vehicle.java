package org.example.m295nick.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Brand must not be blank")
    @Size(min = 2, max = 100, message = "Brand length must be between 2 and 100")
    private String brand;

    @NotBlank(message = "Model must not be blank")
    @Size(min = 1, max = 100, message = "Model length must be between 1 and 100")
    private String model;

    @Positive(message = "Price per day must be greater than zero")
    private double pricePerDay;

    @NotNull(message = "First registration date is required")
    @PastOrPresent(message = "First registration date cannot be in the future")
    private LocalDate firstRegistration;

    @Min(value = 1, message = "Seats must be at least 1")
    private int seats;

    private boolean hasAirConditioning;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Rental> rentals;

    // Getter, Setter, Konstruktoren wie bisher...
    public Vehicle() {}
    // ... andere Konstruktoren und Getter/Setter ...
    // (aus Platzgründen weggelassen, wenn gewünscht liefere ich den kompletten Boilerplate nach)
}
