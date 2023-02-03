package ru.practicum.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ServiceStatsDto;

import java.time.LocalDateTime;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceModel, Long>, JpaSpecificationExecutor<ServiceModel> {

    ServiceModel findByServiceStatistics_Service_AppAndServiceStatistics_Service_Uri(String app, String uri);

    @Query("select new ru.practicum.ServiceStatsDto(s.app, s.uri ,count(serviceStatistics.ip)) from " +
           "ServiceModel s" +
           "   inner join s.serviceStatistics serviceStatistics where s.uri = ?1 and serviceStatistics.service.uri = ?1  and " +
           "serviceStatistics.timestamp between ?2 and  ?3 ")
    ServiceStatsDto getServiceStatistic(String uri, LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ServiceStatsDto(s.app, s.uri ,count(distinct serviceStatistics.ip)) from " +
           "ServiceModel s" +
           "   inner join s.serviceStatistics serviceStatistics where s.uri = ?1 and serviceStatistics.service.uri = ?1  and " +
           "serviceStatistics.timestamp between ?2 and  ?3 ")
    ServiceStatsDto getServiceStatisticDistinct(String uri, LocalDateTime start, LocalDateTime end);

    boolean existsByServiceStatistics_Service_AppAndServiceStatistics_Service_Uri(String app, String uri);

}
