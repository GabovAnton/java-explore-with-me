package ru.practicum.statistic;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.practicum.EndpointHitDto;
import ru.practicum.ServiceStatsDto;
import ru.practicum.service.ServiceModel;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {StatService.class})
public interface StatMapper {

    StatMapper INSTANCE = Mappers.getMapper(StatMapper.class);

    @Named("sumHitsStatistic")
    static int sumHitsStatistic(List<StatisticModel> statistics) {

        return statistics.size();
    }

    @Named("sumHitsStatisticDistinct")
    static int sumHitsStatisticDistinct(List<StatisticModel> statistics) {

        return Math.toIntExact(statistics.stream().map(x -> x.getId()).distinct().count());
    }

    StatisticModel hitDtoToStatistic(EndpointHitDto hitDto);

    EndpointHitDto statisticDtoToHitDto(StatisticModel statistic);

    @Mapping(source = "serviceStatistics", target = "hits", qualifiedByName = "sumHitsStatistic")
    ServiceStatsDto statToServiceStatDto(ServiceModel service);

    @Mapping(source = "serviceStatistics", target = "hits", qualifiedByName = "sumHitsStatisticDistinct")
    ServiceStatsDto statToServiceStatDtoDistinct(ServiceModel service);

}
