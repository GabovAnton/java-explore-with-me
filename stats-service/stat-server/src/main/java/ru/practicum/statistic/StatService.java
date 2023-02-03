package ru.practicum.statistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.EndpointHitDto;
import ru.practicum.ServiceStatsDto;
import ru.practicum.service.ServiceModel;
import ru.practicum.service.ServiceRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatService {

    private final StatisticRepository statisticRepository;

    private final ServiceRepository serviceRepository;

    public EndpointHitDto saveHit(@Valid EndpointHitDto hitWithHttpInfoDto) {

        boolean serviceExist = serviceRepository.existsByServiceStatistics_Service_AppAndServiceStatistics_Service_Uri(
                hitWithHttpInfoDto.getApp(),
                hitWithHttpInfoDto.getUri());

        StatisticModel stat = StatMapper.INSTANCE.hitDtoToStatistic(hitWithHttpInfoDto);

        if (!serviceExist) {
            ServiceModel newService = serviceRepository.save(new ServiceModel(null,
                    hitWithHttpInfoDto.getApp(),
                    hitWithHttpInfoDto.getUri(),
                    null));
            log.debug("new app created: {}", newService.getApp());

            stat.setService(newService);
        } else {
            ServiceModel
                    existedService =
                    serviceRepository.findByServiceStatistics_Service_AppAndServiceStatistics_Service_Uri(
                            hitWithHttpInfoDto.getApp(),
                            hitWithHttpInfoDto.getUri());
            stat.setService(existedService);

        }

        StatisticModel statistic = statisticRepository.save(stat);
        log.debug("new hit recorded for app: {},  ip: {}, timestamp: {}",
                hitWithHttpInfoDto.getApp(),
                hitWithHttpInfoDto.getIp(),
                hitWithHttpInfoDto.getTimestamp());
        return StatMapper.INSTANCE.statisticDtoToHitDto(statistic);

    }

    public List<ServiceStatsDto> getStatistic(String start, String end, List<String> uris, Boolean unique) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);

        return uris.stream().map(x -> {
            if (Boolean.TRUE.equals(unique)) {
                log.debug("unique statistic for url: {} and period from {} to {}", x, start, end);
                return serviceRepository.getServiceStatisticDistinct(x, startDate, endDate);
            } else {
                log.debug("statistic for url: {} and period from {} to {}", x, start, end);
                return serviceRepository.getServiceStatistic(x, startDate, endDate);
            }

        }).filter(Objects::nonNull).sorted(Comparator.comparingLong(ServiceStatsDto::getHits).reversed()).collect(
                Collectors.toUnmodifiableList());
    }

}
