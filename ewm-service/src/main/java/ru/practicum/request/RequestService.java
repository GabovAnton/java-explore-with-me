package ru.practicum.request;

import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getEventParticipantRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId,
            Long eventId,
            @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<ParticipationRequestDto> getUserRequests(Long userId);

    @Transactional
    ParticipationRequestDto addParticipationRequest(Long userId, @NotNull @Valid Long eventId);

    @Transactional
    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

}
