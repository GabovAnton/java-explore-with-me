package ru.practicum.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.service.ServiceModel;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "statistics")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticModel {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String app;

    @Column(nullable = false, updatable = false, length = 512)
    private String ip;

    @Column(nullable = false)
    private String uri;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServiceModel service;

}
