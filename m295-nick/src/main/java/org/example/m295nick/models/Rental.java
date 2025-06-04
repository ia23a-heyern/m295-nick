package org.example.m295nick.models;

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

    // TEXT‐Feld: Kundenname
    @NotBlank(message = "Kundenname darf nicht leer sein")
    @Size(max = 100, message = "Kundenname darf maximal 100 Zeichen lang sein")
    @Column(nullable = false, length = 100)
    private String customer;

    // DATUM‐Feld: Startdatum (Pflicht, darf nicht in der Vergangenheit liegen)
    @NotNull(message = "Startdatum ist Pflicht")
    @FutureOrPresent(message = "Mietbeginn darf nicht in der Vergangenheit liegen")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // DATUM‐Feld: Enddatum (Pflicht, muss in der Zukunft liegen)
    @NotNull(message = "Enddatum ist Pflicht")
    @Future(message = "Mietende muss in der Zukunft liegen")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // BIGDECIMAL‐Feld: Gesamtkosten (Pflicht, positiv, wird im Service berechnet)
    @NotNull(message = "Gesamtkosten sind Pflicht")
    @DecimalMin(value = "0.0", inclusive = false, message = "Gesamtkosten müssen positiv sein")
    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;

    // Verknüpfung zum Vehicle – jedes Rental gehört genau zu einem Vehicle
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    public Rental() {
    }

    // ————————— Getter / Setter —————————

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
}
