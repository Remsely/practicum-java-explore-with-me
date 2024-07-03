package ru.practicum.explorewithme.mainsvc.event.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "locations")
@IdClass(LocationPrimaryKey.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @Column(nullable = false)
    private Double lat;

    @Id
    @Column(nullable = false)
    private Double lon;
}
