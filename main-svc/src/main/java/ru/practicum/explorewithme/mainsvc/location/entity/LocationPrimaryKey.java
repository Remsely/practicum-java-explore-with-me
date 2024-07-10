package ru.practicum.explorewithme.mainsvc.location.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LocationPrimaryKey implements Serializable {
    private Double lat;
    private Double lon;
}
