package org.example.m295nick.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Die Marke darf nicht leer sein")
    @Size(max = 50, message = "Die Marke darf maximal 50 Zeichen lang sein")
    @Column(nullable = false, length = 50)
    private String brand;

    @NotBlank(message = "Das Modell darf nicht leer sein")
    @Size(max = 50, message = "Das Modell darf maximal 50 Zeichen lang sein")
    @Column(nullable = false, length = 50)
    private String model;

    @NotNull(message = "Erstzulassung ist Pflicht")
    @PastOrPresent(message = "Erstzulassung darf nicht in der Zukunft liegen")
    @Column(name = "first_registration", nullable = false)
    private LocalDate firstRegistration;

    @NotNull(message = "Angabe zur Klimaanlage ist Pflicht")
    @Column(name = "has_air_conditioning", nullable = false)
    private Boolean hasAirConditioning;

    @NotNull(message = "Preis pro Tag ist Pflicht")
    @Positive(message = "Preis pro Tag muss positiv sein")
    @Digits(integer = 6, fraction = 2, message = "Preis pro Tag darf maximal 6 Stellen vor dem Komma und 2 Nachkommastellen haben")
    @Column(
            name = "price_per_day",
            nullable = false,
            precision = 8,  // 6 Stellen vor Komma + 2 Nachkommastellen
            scale = 2
    )
    private BigDecimal pricePerDay;

    @NotNull(message = "Anzahl der Sitzplätze ist Pflicht")
    @Min(value = 1, message = "Mindestens 1 Sitzplatz erforderlich")
    @Max(value = 9, message = "Maximal 9 Sitzplätze zulässig")
    @Column(nullable = false)
    private Integer seats;

    // 1:n Beziehung zu Rentals, aber beim JSON ignorieren, damit Rentals nicht mitgegeben werden müssen
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Rental> rentals = new ArrayList<>();

    public Vehicle() {
    }

    // Getter und Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LocalDate getFirstRegistration() {
        return firstRegistration;
    }

    public void setFirstRegistration(LocalDate firstRegistration) {
        this.firstRegistration = firstRegistration;
    }

    public Boolean getHasAirConditioning() {
        return hasAirConditioning;
    }

    public void setHasAirConditioning(Boolean hasAirConditioning) {
        this.hasAirConditioning = hasAirConditioning;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    // Hilfsmethoden für bi-direktionale Beziehung

    public void addRental(Rental rental) {
        rentals.add(rental);
        rental.setVehicle(this);
    }

    public void removeRental(Rental rental) {
        rentals.remove(rental);
        rental.setVehicle(null);
    }
}
