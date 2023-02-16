package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.statistic.StatisticModel;

import javax.persistence.*;
import java.util.List;

@Table(name = "services")
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ServiceModel {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String app;

    private String uri;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "service", cascade = CascadeType.ALL)
    private List<StatisticModel> serviceStatistics;

}
