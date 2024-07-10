package ru.practicum.explorewithme.mainsvc.location.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lon;

    private Double radius;

    private String name;

    @Column(nullable = false)
    private Boolean verified;
}
