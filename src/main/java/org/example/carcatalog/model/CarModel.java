package org.example.carcatalog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "models")
public class CarModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String model;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;
}
