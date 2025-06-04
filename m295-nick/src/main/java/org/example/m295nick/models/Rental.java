package org.example.m295nick.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rental")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Kunde ist Pflicht")
    @Column(nullable = false)
    private String customer;

    @NotNull(message = "Startdatum ist Pflicht")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Enddatum ist Pflicht")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull(message = "Gesamtkosten sind Pflicht")
    @PositiveOrZero(message = "Gesamtkosten müssen positiv oder 0 sein")
    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;

    // Fahrzeug-Relation (viele Rentals können ein Vehicle haben)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonIgnore // verhindert zyklische Serialisierung / JSON-Ausgabe
    private Vehicle vehicle;

    // Hilfsfeld: Fahrzeug-ID für JSON-Ein-/Ausgabe (nicht in DB gespeichert)
    @Transient
    private Long vehicleId;

    // --- Konstruktor ---

    public Rental() {
    }

    // --- Getter / Setter ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    // vehicleId für JSON-Serialisierung und Deserialisierung
    public Long getVehicleId() {
        if (vehicle != null) {
            return vehicle.getId();
        }
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
}
