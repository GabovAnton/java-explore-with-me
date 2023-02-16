package ru.practicum.notification;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
@Setter
public class EwmServiceEventInformation extends ApplicationEvent {

    private String message;

    private Long eventId;

    private NotificationType eventType;

    private List<Long> recipientIds;

    public EwmServiceEventInformation(Object source,
            String message,
            Long eventId,
            List<Long> recipientIds,
            NotificationType eventType) {

        super(source);
        this.message = message;
        this.eventId = eventId;
        this.eventType = eventType;
        this.recipientIds = recipientIds;
    }

}
