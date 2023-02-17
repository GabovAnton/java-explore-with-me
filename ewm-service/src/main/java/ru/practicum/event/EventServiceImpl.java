package ru.practicum.event;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Named;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.EndpointHitDto;
import ru.practicum.EwmStatFeignClient;
import ru.practicum.eventcompilation.*;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.notification.CustomSpringEventPublisher;
import ru.practicum.notification.NotificationType;
import ru.practicum.request.QRequest;
import ru.practicum.subscriptions.EventSubscription;
import ru.practicum.subscriptions.EventSubscriptionRepository;
import ru.practicum.subscriptions.UserSubscription;
import ru.practicum.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.event.StateEvent.*;
import static ru.practicum.event.StateEventUserAction.CANCEL_REVIEW;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventServiceImpl implements EventService {

    private final LocationMapper locationMapper;

    private final EventMapper eventMapper;

    private final EventCompilationMapper eventCompilationMapper;

    private final EventRepository eventRepository;

    private final EventSubscriptionRepository eventSubscriptionRepository;

    private final LocationRepository locationRepository;

    private final EventCompilationRepository eventCompilationRepository;

    private final UserRepository userRepository;

    private final EwmStatFeignClient statFeignClient;

    private final CustomSpringEventPublisher customSpringEventPublisher;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Event getEvent(Long id) {

        return eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "event with id: " + id + " " + "not found"));
    }

    @Override
    public List<EventFullDto> searchAdminEvents(@Valid List<Long> users,
            @Valid List<String> states,
            @Valid List<Long> categories,
            @Valid String rangeStart,
            @Valid String rangeEnd,
            @Valid Integer from,
            @Valid Integer size) {

        QEvent qEvent = QEvent.event;
        JPAQuery<Event> query = new JPAQuery<>(entityManager);

        BooleanBuilder builder = new BooleanBuilder();

        if (users != null) {
            builder.and(qEvent.initiator.id.in(users));
        }
        if (states != null) {
            builder.and(qEvent.eventStatus.stringValue().in(states));
        }
        if (categories != null) {
            builder.and(qEvent.category.id.in(categories));
        }
        if (StringUtils.isNoneBlank(rangeStart)) {
            builder.and(qEvent.eventDate.after(stringToDate(rangeStart)));
        }
        if (StringUtils.isNoneBlank(rangeEnd)) {
            builder.and(qEvent.eventDate.before(stringToDate(rangeEnd)));
        }
        int offset = from != null ? (from > 1 ? --from : from) : 0;
        long totalItems = eventRepository.count() + 1;
        List<EventFullDto> events = query
                .from(qEvent)
                .where(builder)
                .limit(size != null ? size : totalItems)
                .offset(offset)
                .fetch()
                .stream()
                .map(eventMapper::toDto)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        log.debug("<admin> query for events successfully executed, result {}", events);
        return events;
    }

    @Override
    public EventFullDto updateEvent(Event event) {

        Event oldEvent = eventRepository.findById(event.getId()).orElseThrow(() -> new EntityNotFoundException(
                "event with id:" + event.getId() + " not found"));

        Event savedEvent = eventRepository.save(event);

        EventFullDto eventFullDto = eventMapper.toDto(savedEvent);
        log.debug("<private> event with id: {}  updated: {}", event.getId(), eventFullDto);
        Set<EventSubscription> eventSubscriptions = savedEvent.getEventSubscriptions();
        if (eventSubscriptions != null) {
            String publicationInfo = compareEventFields(event, oldEvent);
            if (StringUtils.isNoneBlank(publicationInfo)) {
                customSpringEventPublisher.publishCustomEvent(
                        "event with id:" + savedEvent.getId() + " updated -> " + publicationInfo,
                        event.getId(),
                        eventSubscriptions
                                .stream()
                                .filter(x -> x.getNotifyChangeEvents().equals(true))
                                .map(EventSubscription::getSubscriberId)
                                .collect(Collectors.toList()),
                        NotificationType.CHANGE);
            }
        }
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getEventsPrivate(Long userId, @Valid Integer from, @Valid Integer size) {

        QEvent qEvent = QEvent.event;
        JPAQuery<Event> query = new JPAQuery<>(entityManager);
        int offset = from != null ? (from > 1 ? --from : from) : 0;
        long totalItems = eventRepository.count() + 1;

        List<EventShortDto> events = query.from(qEvent).where(qEvent.initiator.id.eq(userId)).limit(
                size != null ? size : totalItems).offset(offset).fetch().stream().map(eventMapper::toShortDto).collect(
                Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        log.debug("<private> query for events successfully executed for userId: {}, result {}", userId, events);
        return events;
    }

    @Override
    public List<EventShortDto> getEventsPublic(@Valid String text,
            @Valid List<Long> categories,
            @Valid Boolean paid,
            @Valid String rangeStart,
            @Valid String rangeEnd,
            @Valid Boolean onlyAvailable,
            @Valid String sort,
            @Valid Integer from,
            @Valid Integer size,
            @Valid String clientIp,
            @Valid String endpointPath) {

        saveViewStatistic(clientIp, endpointPath);

        QEvent qEvent = QEvent.event;
        JPAQuery<Event> query = new JPAQuery<>(entityManager);

        QRequest qRequest = QRequest.request;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qEvent.eventStatus.eq(PUBLISHED));
        if (StringUtils.isBlank(rangeStart) && StringUtils.isBlank(rangeEnd)) {
            builder.and(qEvent.eventDate.after(LocalDateTime.now()));
        } else if (StringUtils.isNotBlank(rangeStart)) {
            builder.and(qEvent.eventDate.after(stringToDate(rangeStart)));
        } else if (StringUtils.isNotBlank(rangeEnd)) {
            builder.and(qEvent.eventDate.before(stringToDate(rangeStart)));
        }
        if (categories != null) {
            builder.and(qEvent.category.id.in(categories));
        }
        if (paid != null) {
            builder.and((qEvent.paid.eq(paid)));
        }
        if (Boolean.TRUE.equals(onlyAvailable)) {
            builder.and((qEvent.participantLimit.lt(qEvent.eventRequests.any().id
                    .eq(qEvent.id)
                    .and(qRequest.status.eq(RequestStatusEnum.CONFIRMED))
                    .count())));
        }

        if (StringUtils.isNotBlank(text)) {
            builder.and(qEvent.annotation.containsIgnoreCase(text).or(qEvent.description.containsIgnoreCase(text)));
        }
        int offset = from != null ? (from > 1 ? --from : from) : 0;
        long totalItems = userRepository.count() + 1;

        List<EventShortDto> eventShortDtos = query.from(qEvent).where(builder).orderBy(sort == null ? qEvent.id.desc() :
                                                                                       sort.equals("EVENT_DATE") ?
                                                                                       qEvent.eventDate.desc() :
                                                                                       qEvent.views.desc()).limit(
                        size != null ? size : totalItems).offset(offset).fetch().stream()

                .map(eventMapper::toShortDto).collect(Collectors.toList());
        eventShortDtos.forEach(x -> {
            int viewsCount = eventRepository.updateIncrementViewsById(x.getId());
            log.debug("<public> increase event view count to: {}", viewsCount);
        });
        return eventShortDtos;

    }

    private void saveViewStatistic(String clientIp, String endpointPath) {

        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setUri(endpointPath);
        endpointHitDto.setIp(clientIp);
        endpointHitDto.setApp("ewm_service");
        endpointHitDto.setTimestamp(LocalDateTime.now());

        EndpointHitDto endpointHit = statFeignClient.create(endpointHitDto);
        log.debug("<public> save event view to statistic: {}", endpointHit);
    }

    @Override
    public EventFullDto getEventPublic(Long id, String clientIp, String endpointPath) {

        saveViewStatistic(clientIp, endpointPath);
        Event event = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                "event with id: " + id + " not found"));

        int viewsCount = eventRepository.updateIncrementViewsById(id);

        log.debug("<public> increase event view count to: {}", viewsCount);

        EventFullDto eventFullDto = eventMapper.toDto(event);
        log.debug("<public> event with id {} requested, returned  {}", id, eventFullDto);

        return eventFullDto;
    }

    @Override
    public EventFullDto updateEventPrivate(Long userId,
            Long eventId,
            @Valid UpdateEventUserRequestDto updateEventUserRequestDto) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(
                    "<private> error while trying to update Event with id: " + eventId + "Reason: category doesn't " +
                    "exist");
        }

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                "<private> error while trying to update Event with id: " + eventId + "Reason: event doesn't exist"));

        if (updateEventUserRequestDto.getEventDate() != null) {
            checkEventUpdateConstraints(updateEventUserRequestDto.getEventDate(), event, 2);
        }

        if (event.getEventStatus() != null && event.getEventStatus().equals(PUBLISHED)) {
            throw new DataIntegrityViolationException("Only pending or canceled events can be changed");
        }
        Event updatedEvent = eventMapper.partialUpdateUser(updateEventUserRequestDto, event);
        if (updateEventUserRequestDto.getStateAction() != null) {
            if (updateEventUserRequestDto.getStateAction().equals(CANCEL_REVIEW)) {
                updatedEvent.setEventStatus(CANCELED);
            } else {
                updatedEvent.setEventStatus(PENDING);
            }
        }

        EventFullDto updatedEventDto = eventMapper.toDto(eventRepository.save(updatedEvent));
        log.debug("<private> event with id: {}  updated: {}", eventId, updatedEventDto);
        Set<EventSubscription> eventSubscriptions = updatedEvent.getEventSubscriptions();
        if (eventSubscriptions != null) {
            String publicationInfo = "event with id: " + eventId + " updated:" + getUpdatedFields(
                    updateEventUserRequestDto);

            customSpringEventPublisher.publishCustomEvent("event with id:" + eventId + " updated -> " + publicationInfo,
                    eventId,
                    eventSubscriptions
                            .stream()
                            .filter(x -> x.getNotifyChangeEvents().equals(true))
                            .map(EventSubscription::getSubscriberId)
                            .collect(Collectors.toList()),
                    NotificationType.CHANGE);

        }
        return updatedEventDto;
    }

    private String getUpdatedFields(UpdateEventUserRequestDto updatedEventDto) {

        StringBuilder sb = new StringBuilder();
        if (updatedEventDto.getAnnotation() != null) {
            sb.append("<annotation>");
        }
        if (updatedEventDto.getEventDate() != null) {
            sb.append("<event date>");
        }
        if (updatedEventDto.getTitle() != null) {
            sb.append("<title>");
        }
        if (updatedEventDto.getDescription() != null) {
            sb.append("<description>");
        }
        if (updatedEventDto.getPaid() != null) {
            sb.append("<payment>");
        }
        if (updatedEventDto.getCategory() != null) {
            sb.append("<category>");
        }
        if (updatedEventDto.getLocation() != null) {
            sb.append("<location>");
        }
        if (updatedEventDto.getParticipantLimit() != null) {
            sb.append("<participants limit>");
        }
        if (updatedEventDto.getStateAction() != null) {
            sb.append("<status>");
        }
        return sb.toString();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public EventFullDto addEventPrivate(Long userId, @Valid NewEventDto newEventDto) {

        if (userRepository.existsById(userId)) {
            checkNewEventDateConstraints(newEventDto.getEventDate());

            if (newEventDto.getCategory() == null) {
                throw new DataIntegrityViolationException("Field: category. Error: must not be blank. Value: null");
            }
            Location location = locationMapper.toEntity(newEventDto.getLocation());
            Location savedLocation = locationRepository.save(location);
            Event newEvent = eventMapper.fromNewEventDtoToEntity(newEventDto);
            newEvent.setCreatedOn(LocalDateTime.now());
            newEvent.setLocation(savedLocation);
            newEvent.setInitiator(userRepository.getReferenceById(userId));
            newEvent.setEventStatus(PENDING);
            Event savedEvent = eventRepository.save(newEvent);
            log.debug("<private> New event with id: {}, name: {}  successfully created, status: {}",
                    savedEvent.getId(),
                    savedEvent.getTitle(),
                    savedEvent.getEventStatus());

            EventFullDto eventFullDto = eventMapper.toDto(savedEvent);
            addEventSubscriptions(savedEvent);
            Set<EventSubscription> eventSubscriptions = savedEvent.getEventSubscriptions();
            if (eventSubscriptions != null) {
                customSpringEventPublisher.publishCustomEvent(
                        "New event with id:" + savedEvent.getId() + " created -> " + eventFullDto.getTitle() + " on " +
                        eventFullDto.getEventDate(),
                        savedEvent.getId(),
                        eventSubscriptions.stream().filter(x -> x
                                .getNotifyNewEvents()
                                .equals(true)).map(EventSubscription::getSubscriberId).collect(Collectors.toList()),
                        NotificationType.NEW);

            }
            return eventFullDto;

        } else {
            throw new EntityNotFoundException(
                    "<private > can't create event: " + newEventDto.getTitle() + ". Reason: user with id: " + userId +
                    " doesn't exists");
        }

    }

    private Event addEventSubscriptions(Event event) {

        if (event.getInitiator().getUserSubscriptions() != null) {
            for (UserSubscription userSubscription : event.getInitiator().getUserSubscriptions()) {
                EventSubscription eventSubscription = new EventSubscription(null,
                        userSubscription.getUserId(),
                        LocalDateTime.now(),
                        userSubscription.getSubscribeNewEvents(),
                        userSubscription.getSubscribeChangeEvents(),
                        userSubscription.getSubscribeDeleteEvents(),
                        event,
                        null,
                        event.getInitiator().getId());
                if (event.getEventSubscriptions() != null) {
                    event.getEventSubscriptions().add(eventSubscriptionRepository.save(eventSubscription));
                } else {
                    event.setEventSubscriptions(Set.of(eventSubscriptionRepository.save(eventSubscription)));

                }
                log.debug(
                        " new event subscriptions for userId: {} and friendId: {}  and eventId successfully created: {}",
                        userSubscription.getUserId(),
                        event.getInitiator().getId(),
                        event.getId());
            }
        }
        return event;
    }

    private void checkNewEventDateConstraints(LocalDateTime newDateTime) {

        if (newDateTime != null && newDateTime.plusHours(2).isBefore(LocalDateTime.now())) {
            throw new DataIntegrityViolationException(
                    "<private> >Error:событие должно содержать дату, которая еще не наступила. Value: " +
                    newDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

    }

    private void checkEventUpdateConstraints(LocalDateTime newDateTime, Event event, int hours) {

        checkNewEventDateConstraints(newDateTime);
        if (!LocalDateTime.now().plusHours(hours).isBefore(event.getEventDate())) {
            throw new ForbiddenException(
                    "<private> дата начала изменяемого события (" + event.getEventDate() + ") " + "должна " + "быть " +
                    "не ранее чем за 2 часа от текущего момента (" + LocalDateTime.now().minusHours(2) + ")");
        }

    }

    @Override
    public EventFullDto getEventPrivate(Long userId, Long eventId) {

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(
                    "<private> error while trying to update Event with id: " + eventId + "Reason: category doesn't " +
                    "exist");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException(
                    "<private> error while trying to update Event with id: " + eventId + "Reason: event doesn't exist");
        }
        QEvent qEvent = QEvent.event;
        JPAQuery<Event> query = new JPAQuery<>(entityManager);
        EventFullDto eventFullDto = eventMapper.toDto(query
                .from(qEvent)
                .where(qEvent.initiator.id
                        .eq(userId)
                        .and(qEvent.id.eq(eventId)))
                .fetch()
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("<private> cannot find Event with id: " + eventId)));

        log.debug("<private> query for events successfully executed for userId: {}, eventId: {} result {}",
                userId,
                eventId,
                eventFullDto);
        return eventFullDto;

    }

    @Override
    public EventFullDto updateAdminEvent(Long eventId, @Valid UpdateEventAdminRequest updateEventAdminRequest) {

        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException(
                    "<admin> error while trying to update Category with id: " + eventId + "Reason: category " +
                    "doesn't exist");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                "<admin> event with id: " + eventId + " not found"));

        checkEventUpdateConstraints(updateEventAdminRequest.getEventDate(), event, 1);

        switch (updateEventAdminRequest.getStateAction()) {
            case PUBLISH_EVENT:
                if (!event.getEventStatus().equals(PENDING)) {
                    throw new DataIntegrityViolationException(
                            "<admin> событие можно публиковать, только если оно в состоянии ожидания публикации");
                } else {
                    event.setPublishedOn(LocalDateTime.now());
                    event.setEventStatus(PUBLISHED);
                }
                break;
            case REJECT_EVENT:
                if (event.getEventStatus().equals(PUBLISHED)) {
                    throw new DataIntegrityViolationException(
                            "<admin> событие можно отклонить, только если оно не " + "опубликовано");
                } else {
                    event.setEventStatus(CANCELED);
                }
                break;
            default:
                throw new DataIntegrityViolationException("<admin> there is no requested action on admin update event");
        }

        Event updatedEvent = eventMapper.partialUpdateAdmin(updateEventAdminRequest, event);
        EventFullDto updatedEventDto = eventMapper.toDto(eventRepository.save(updatedEvent));
        log.debug("<admin> event with id: {}  updated: {}, status: {}",
                eventId,
                updatedEvent,
                updatedEvent.getEventStatus());
        Set<EventSubscription> eventSubscriptions = updatedEvent.getEventSubscriptions();
        if (eventSubscriptions != null) {
            customSpringEventPublisher.publishCustomEvent(
                    "event with id:" + eventId + " updated by admin -> " + updatedEventDto.getTitle() + " on " +
                    updatedEventDto.getEventDate() + " new status: " + event.getEventStatus(),
                    eventId,
                    eventSubscriptions
                            .stream()
                            .filter(x -> x.getNotifyChangeEvents().equals(true))
                            .map(EventSubscription::getSubscriberId)
                            .collect(Collectors.toList()),
                    NotificationType.CHANGE);

        }

        return updatedEventDto;
    }

    private LocalDateTime stringToDate(String dateTime) {

        try {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        } catch (DateTimeParseException e) {
            throw new BadRequestException("can't parse dateTime from string: " + dateTime);
        }

    }

    @Override
    @Named("eventIdsToEvents")
    public Set<Event> eventIdsToEvents(Set<Long> ids) {

        if (ids != null) {
            return eventRepository.findByIdIn(ids);
        } else {
            return Set.of(new Event());
        }
    }

    @Transactional
    @Override
    public CompilationDto saveCompilationAdmin(@Valid NewCompilationDto newCompilationDto) {

        EventCompilation eventCompilation = newCompilationDtoToEntity(newCompilationDto);
        EventCompilation savedCompilation = eventCompilationRepository.save(eventCompilation);
        log.debug("<admin> compilation with id: {},  title: {} created",
                savedCompilation.getId(),
                savedCompilation.getTitle());

        return eventCompilationMapper.compilationToDto(savedCompilation);
    }

    private EventCompilation newCompilationDtoToEntity(NewCompilationDto newCompilationDto) {

        EventCompilation eventCompilation = new EventCompilation();
        eventCompilation.setEventsCompilationGroupEvents(eventIdsToEvents(newCompilationDto.getEvents()));
        eventCompilation.setTitle(newCompilationDto.getTitle());
        eventCompilation.setPinned(newCompilationDto.getPinned());
        return eventCompilation;
    }

    @Override
    public void deleteCompilationAdmin(Long compId) {

        eventCompilationRepository.deleteById(compId);
        log.debug("<admin> compilation with id: {} deleted", compId);
    }

    public EventCompilation compilationPartialUpdate(UpdateCompilationRequestDto updateCompilationRequestDto,
            EventCompilation eventCompilation) {

        if (updateCompilationRequestDto == null) {
            return eventCompilation;
        } else {
            Set set;
            if (eventCompilation.getEventsCompilationGroupEvents() != null) {
                set = eventIdsToEvents(updateCompilationRequestDto.getEvents());
                if (set != null) {
                    eventCompilation.getEventsCompilationGroupEvents().clear();
                    eventCompilation.getEventsCompilationGroupEvents().addAll(set);
                }
            } else {
                set = eventIdsToEvents(updateCompilationRequestDto.getEvents());
                if (set != null) {
                    eventCompilation.setEventsCompilationGroupEvents(set);
                }
            }

            if (updateCompilationRequestDto.getTitle() != null) {
                eventCompilation.setTitle(updateCompilationRequestDto.getTitle());
            }

            if (updateCompilationRequestDto.getPinned() != null) {
                eventCompilation.setPinned(updateCompilationRequestDto.getPinned());
            }

            return eventCompilation;
        }
    }

    @Override
    @Transactional
    public CompilationDto updateCompilationAdmin(Long compId,
            @Valid UpdateCompilationRequestDto updateCompilationRequestDto) {

        EventCompilation
                eventCompilation =
                eventCompilationRepository.findById(compId).orElseThrow(() -> new EntityNotFoundException(
                        "compilation with id:" + compId + " not found"));
        EventCompilation updatedEventCompilation = compilationPartialUpdate(updateCompilationRequestDto,
                eventCompilation);
        EventCompilation savedCompilation = eventCompilationRepository.save(updatedEventCompilation);
        log.debug("<admin> compilation with id: {} updated", compId, savedCompilation);

        return eventCompilationMapper.compilationToDto(savedCompilation);
    }

    @Override
    public List<CompilationDto> getCompilationsPublic(@Valid Boolean pinned, @Valid Integer from, @Valid Integer size) {

        QEventCompilation qEventCompilation = QEventCompilation.eventCompilation;
        JPAQuery<EventCompilation> query = new JPAQuery<>(entityManager);

        int offset = from != null ? (from > 1 ? --from : from) : 0;
        long totalItems = userRepository.count() + 1;

        BooleanBuilder builder = new BooleanBuilder();
        if (pinned != null) {
            builder.and(qEventCompilation.pinned.eq(pinned));
        }
        List<CompilationDto> compilationDtos = query
                .from(qEventCompilation)
                .where(builder)
                .limit(size != null ? size : totalItems)
                .offset(offset)
                .fetch()
                .stream()
                .map(eventCompilationMapper::compilationToDto)
                .collect(Collectors.toUnmodifiableList());

        log.debug("<public> set of compilation  requested from {} to with size {} pinned={}", from, size, pinned);

        return compilationDtos;
    }

    @Override
    public CompilationDto getCompilationPublic(Long compId) {

        EventCompilation
                eventCompilation =
                eventCompilationRepository.findById(compId).orElseThrow(() -> new EntityNotFoundException(
                        "compilation with id:" + compId + " not found"));

        CompilationDto compilationDto = eventCompilationMapper.compilationToDto(eventCompilation);
        log.debug("<public> Event compilation  requested with id {}, returned: {}", compId, compilationDto);

        return compilationDto;
    }

    private String compareEventFields(Event one, Event two) {

        StringBuilder sb = new StringBuilder();
        if (!one.getEventDate().equals(two.getEventDate())) {
            sb.append("<event date>");
        }
        if (!one.getCategory().equals(two.getCategory())) {
            sb.append("<category>");
        }
        if (!one.getEventStatus().equals(two.getEventStatus())) {
            sb.append("<status>");
        }
        if (!one.getDescription().equals(two.getDescription())) {
            sb.append("<description>");
        }
        if (!one.getPaid().equals(two.getPaid())) {
            sb.append("<payment>");
        }
        if (!one.getAnnotation().equals(two.getAnnotation())) {
            sb.append("<annotation>");
        }
        if (!one.getLocation().equals(two.getLocation())) {
            sb.append("<location>");
        }
        if (!one.getTitle().equals(two.getTitle())) {
            sb.append("<title>");
        }

        return sb.toString();
    }

}
