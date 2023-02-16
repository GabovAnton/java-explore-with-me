package ru.practicum.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class SubscriptionNotificationDto {

    private Long id;

    private Long userId;

    private Boolean notified;

    @NotNull
    private Long subscription;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime notificationDate;

    private String notificationType;

    private String notificationMethod;

    private String payload;

}
