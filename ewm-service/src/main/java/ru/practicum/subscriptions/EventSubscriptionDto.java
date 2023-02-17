package ru.practicum.subscriptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventSubscriptionDto {

    private Long id;

    @NotNull
    private Long subscriberId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    private Boolean notifyNewEvents;

    private Boolean notifyChangeEvents;

    private Boolean notifyDeleteEvents;

    @NotNull
    private Long event;

    private Long eventInitiatorId;

}
