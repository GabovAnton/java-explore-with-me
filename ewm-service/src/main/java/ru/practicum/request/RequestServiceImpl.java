package ru.practicum.request;

import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.event.EventService;
import ru.practicum.event.RequestStatusEnum;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.event.RequestStatusEnum.CONFIRMED;
import static ru.practicum.event.RequestStatusEnum.REJECTED;
import static ru.practicum.event.StateEvent.PUBLISHED;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class RequestServiceImpl implements RequestService {

    private final EventRepository eventRepository;

    private final RequestMapper requestMapper;

    private final RequestRepository requestRepository;

    private final EventService eventService;

    private final UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ParticipationRequestDto> getEventParticipantRequests(Long userId, Long eventId) {

        QRequest qRequest = QRequest.request;
        JPAQuery<Request> query = new JPAQuery<>(entityManager);
        List<ParticipationRequestDto> participationRequests = query.from(qRequest).where(qRequest.event.initiator.id
                .eq(userId)
                .and(qRequest.event.id.eq(eventId))).fetch().stream().map(RequestMapper.INSTANCE::toDto).collect(
                Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

        log.debug("<private> requests  participants for user id: {} and event id: {} returned: {}",
                userId,
                eventId,
                participationRequests);

        return participationRequests;
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId,
            Long eventId,
            @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        if (eventRequestStatusUpdateRequest.getRequestIds() == null) {
            //TODO
            return eventRequestStatusUpdateResult;
        }
        RequestStatusEnum newRequestStatus = eventRequestStatusUpdateRequest.getStatus();
        List<Request> requests = requestRepository.findByIdIn(eventRequestStatusUpdateRequest.getRequestIds());
        // Event event = eventService.getEvent(eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                "<private>  event with id: " + eventId + " " + "not found"));

        if (event.getParticipantLimit().equals(0) || event.getRequestModeration().equals(false)) {
            //TODO проверить логику(ничего не делаем...)
            return eventRequestStatusUpdateResult;
        }
        checkEventParticipationConstraints(event);
        requests.forEach(request -> {
            Long approvedRequestsCount = event
                    .getEventRequests()
                    .stream()
                    .filter(x -> x.getStatus().equals(CONFIRMED))
                    .count();
            Long participantLimit = Long.valueOf(event.getParticipantLimit());
            if (participantLimit.equals(approvedRequestsCount)) {
                request.setStatus(REJECTED);
                requestRepository.save(request);
                throw new DataIntegrityViolationException("<private> превышен лимит участников мероприятия");

            } else {
                if (eventRequestStatusUpdateRequest.getStatus().equals(REJECTED)) {

                    if (request.getStatus().equals(CONFIRMED)) {
                        throw new DataIntegrityViolationException(
                                "<private> нельзя внести изменения- заявка уже была " + "одобрена");
                    }
                    request.setStatus(REJECTED);

                    rejectedRequests.add(requestMapper.toDto(request));
                } else {
                    request.setStatus(CONFIRMED);
                    confirmedRequests.add(requestMapper.toDto(request));
                }

                eventRepository.save(event);
                log.debug("<private> event with id: {} updated: {}", event.getId(), event);

            }
            eventService.updateEvent(event);
            Request savedRequest = requestRepository.save(request);
            log.debug("<private> request with id: {} updated: {}", request.getId(), savedRequest);
        });
        eventRequestStatusUpdateResult.setConfirmedRequests(confirmedRequests);
        eventRequestStatusUpdateResult.setRejectedRequests(rejectedRequests);

        return eventRequestStatusUpdateResult;
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {

        QRequest qRequest = QRequest.request;
        JPAQuery<Request> query = new JPAQuery<>(entityManager);
        List<ParticipationRequestDto>
                participationRequests =
                query
                        .from(qRequest)
                        .where(qRequest.requester.id.eq(userId))
                        .fetch()
                        .stream()
                        .map(RequestMapper.INSTANCE::toDto)
                        .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

        log.debug("<private> participation requests for user with id: {} requested, returned: {}",
                userId,
                participationRequests);

        return participationRequests;
    }

    @Override
    @Transactional
    public ParticipationRequestDto addParticipationRequest(Long userId, @NotNull @Valid Long eventId) {

        Request request = new Request();
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                "<private>  event with id:" + eventId + " not found"));
        if (event.getInitiator().getId().equals(userId)) {
            throw new DataIntegrityViolationException(
                    "инициатор события не может добавить запрос на участие в своём " + "событии");
        }
        if (event.getEventRequests().stream().anyMatch(x -> x.getRequester().getId().equals(userId))) {
            throw new DataIntegrityViolationException("<private>  нельзя дважды сделать запрос на одно событие");
        }
        if (!requestRepository.findByEvent_Initiator_IdAndEvent_Id(userId, eventId).isEmpty()) {
            throw new DataIntegrityViolationException(
                    "<private>  инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getEventStatus().equals(PUBLISHED)) {
            throw new DataIntegrityViolationException("<private> нельзя участвовать в неопубликованном событии");
        }
        Long participantsCount = event.getEventRequests().stream().filter(x -> x.getStatus().equals(CONFIRMED)).count();
        Long participantLimit = Long.valueOf(event.getParticipantLimit());
        if (participantLimit.equals(participantsCount)) {
            throw new DataIntegrityViolationException("<private> у события достигнут лимит запросов на участие");

        }
        if (event.getRequestModeration().equals(false)) {
            request.setStatus(CONFIRMED);

            eventRepository.save(event);

        } else {
            request.setStatus(RequestStatusEnum.PENDING);
        }

        request.setRequester(userService.getUser(userId));
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        Request save = requestRepository.save(request);
        log.debug("<private> participation requests for user with id: {}, event id: {} created with {} status",
                userId,
                eventId,
                request.getStatus());

        ParticipationRequestDto participationRequestDto = requestMapper.toDto(request);
        return participationRequestDto;
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {

        Request request = requestRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException(
                "<private> request with id:" + requestId + " doesn't exists"));
        request.setStatus(RequestStatusEnum.CANCELED);

        requestRepository.save(request);
        log.debug("<private> participation requests for user with id: {}, request id: {} rejected by requester",
                userId,
                requestId);

        return requestMapper.toDto(request);
    }

    private void checkEventParticipationConstraints(Event event) {

        Long participantLimit = Long.valueOf(event.getParticipantLimit());
        Long count = event.getEventRequests().stream().filter(x -> x.getStatus().equals(CONFIRMED)).count();
        if (participantLimit.equals(count)) {
            throw new DataIntegrityViolationException(
                    "The participant limit for event id: " + event.getId() + "has been " + "reached");
        }
    }

}
