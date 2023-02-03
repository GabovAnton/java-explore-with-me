package ru.practicum;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "statsClient", path = "/hit", url = "${EWM_SERVER_URL}")
public interface EwmStatFeignClient {

    @PostMapping()
    EndpointHitDto create(@RequestBody EndpointHitDto endpointHitDto);

    @GetMapping("/stats")
    List<ServiceStatsDto> getAllFromOthers(@RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean unique);

}
