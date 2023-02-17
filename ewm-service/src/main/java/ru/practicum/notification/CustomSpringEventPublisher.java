package ru.practicum.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSpringEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishCustomEvent(final String message,
            Long eventId,
            List<Long> subscriptionIds,
            NotificationType eventType) {

        log.debug("publishing event: {}", message);
        EwmServiceEventInformation ewmServiceEventInformation = new EwmServiceEventInformation(this,
                message,
                eventId,
                subscriptionIds,
                eventType);
        applicationEventPublisher.publishEvent(ewmServiceEventInformation);
    }

}
