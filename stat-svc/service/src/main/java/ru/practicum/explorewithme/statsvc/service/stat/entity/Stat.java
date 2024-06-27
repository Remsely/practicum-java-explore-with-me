package ru.practicum.explorewithme.statsvc.service.stat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stats")
@IdClass(AppAndUriPrimaryKey.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stat {
    @Id
    @Column(nullable = false)
    private String app;

    @Id
    @Column(nullable = false)
    private String uri;

    @Id
    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private Long hits;
}
