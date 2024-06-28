package ru.practicum.explorewithme.statsvc.service.stat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AppAndUriPrimaryKey implements Serializable {
    private String app;
    private String uri;
    private String ip;
}
