package org.example.m295nick.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer name must not be blank")
    private String customer;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Positive(message = "Total cost must be positive")
    private double totalCost;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonBackReference
    private Vehicle vehicle;

    public Rental() {}
    // ... andere Konstruktoren und Getter/Setter ...
}
