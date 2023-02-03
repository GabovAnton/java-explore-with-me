package ru.practicum.statistic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ServiceStatsDto;

import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatService statService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> hitWithHttpInfo(@RequestBody EndpointHitDto hitWithHttpInfoDto) {

        log.info("Saving hit: {}", hitWithHttpInfoDto);
        EndpointHitDto hitDto = statService.saveHit(hitWithHttpInfoDto);
        return ResponseEntity.ok(hitDto);
    }

    @GetMapping("/stats")
    public List<ServiceStatsDto> getStats(@RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean unique) {

        log.info("Get stat: start {}, end={}, uris:{}, unique: {} ", start, end, uris, unique);
        return statService.getStatistic(start, end, uris, unique);

    }

}
