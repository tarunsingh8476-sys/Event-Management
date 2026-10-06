package com.Backend.EventManagement.Service;

import com.Backend.EventManagement.DTO.EventRequest;
import com.Backend.EventManagement.DTO.EventResponse;
import com.Backend.EventManagement.DTO.PagedResponse;
import com.Backend.EventManagement.Entity.Event;
import com.Backend.EventManagement.Entity.EventStatus;
import com.Backend.EventManagement.ExceptionHandler.DuplicateEventException;
import com.Backend.EventManagement.ExceptionHandler.EventNotFoundException;
import com.Backend.EventManagement.ExceptionHandler.InvalidEventStateException;
import com.Backend.EventManagement.ExceptionHandler.InvalidRequestException;
import com.Backend.EventManagement.Repository.EventRepo;
import com.sun.jdi.request.InvalidRequestStateException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EventService {

    private static final int MAX_PAGE_SIZE = 100;

    private static final Map<String,String> SORT_FIELDS = Map.of(
            "date","startTime",
            "name" , "name",
            "venue" , "venue",
            "capacity" ,"maxCapacity",
            "created" ,"createdAt"
    );

    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private Clock clock;

    @Transactional
    public EventResponse addEvent(EventRequest eventRequest) {

        LocalDateTime now = now();

        String eventName = eventRequest.getName().trim();
        String venue = eventRequest.getVenue().trim();

        validateTimes(eventRequest.getStartTime(),eventRequest.getEndTime());

        validateStartIsInFuture(eventRequest.getStartTime(),now);

        if(eventRepo.existsDuplicate( eventRequest.getName(), eventRequest.getVenue(),eventRequest.getStartTime(),null)){
            throw new DuplicateEventException(eventRequest.getName() ,eventRequest.getVenue());
        }
        Event  event = new Event();
        event.setName(eventName);
        event.setVenue(venue);
        event.setStartTime(eventRequest.getStartTime());
        event.setEndTime(eventRequest.getEndTime());
        event.setMaxCapacity(eventRequest.getMaxCapacity());
        event.setCreatedAt(now);

        return EventResponse.from(eventRepo.save(event),now);
    }

    public PagedResponse<EventResponse> getAllEvents(int page, int limit, String sort, String order, String search) {
        LocalDateTime now = now();

        if (page < 1) {
            throw new InvalidRequestException("page", "page must be 1 or greater");
        }
        if (limit < 1 || limit > MAX_PAGE_SIZE) {
            throw new InvalidRequestException("limit", "limit must be between 1 and " + MAX_PAGE_SIZE);
        }

        String sortKey = Optional.ofNullable(sort)
                .map(value -> value.trim().toLowerCase(Locale.ROOT))
                .filter(value -> !value.isEmpty())
                .orElse("date");
        String sortField = Optional.ofNullable(SORT_FIELDS.get(sortKey))
                .orElseThrow(() -> new InvalidRequestException("sort",
                        "sort must be one of: " + SORT_FIELDS.keySet().stream()
                                .sorted()
                                .collect(Collectors.joining(", "))));
        Sort.Direction direction = parseDirection(order);


        Sort sorting = Sort.by(direction, sortField).and(Sort.by(Sort.Direction.ASC, "id"));
        Pageable pageable = PageRequest.of(page - 1, limit, sorting);


        Page<Event> result = (search == null || search.isBlank())
                ? eventRepo.findAll(pageable)
                : eventRepo.findByNameContainingIgnoreCase(search.trim(), pageable);

        List<EventResponse> items = result.getContent().stream()
                .map(event -> EventResponse.from(event, now))
                .toList();

        return new PagedResponse<>(items,
                new PagedResponse.Pagination(page, limit, result.getTotalElements(), result.getTotalPages()));
    }

    @Transactional
    public EventResponse updateEvent(Long id, EventRequest eventRequest) {

      LocalDateTime now = now();
      Event event = findOrThrow(id);
      EventStatus status = event.getStatus(now);

        switch (status) {
            case COMPLETED, CANCELLED -> throw new InvalidEventStateException(
                    "A " + status.name().toLowerCase(Locale.ROOT) + " event cannot be edited");
            case ONGOING -> updateOngoing(event, eventRequest);
            case UPCOMING -> updateUpcoming(event, eventRequest, now);
        }

        event.setUpdatedAt(now);
        return EventResponse.from(eventRepo.save(event), now);
    }
    private void updateUpcoming(Event event, EventRequest eventRequest, LocalDateTime now) {
        String name = eventRequest.getName().trim();
        String venue = eventRequest.getVenue().trim();

        validateTimes(eventRequest.getStartTime() , eventRequest.getEndTime());
        validateStartIsInFuture(eventRequest.getStartTime() ,now);

        if(eventRepo.existsDuplicate( eventRequest.getName(), eventRequest.getVenue(),eventRequest.getStartTime(),null)){
            throw new DuplicateEventException( eventRequest.getName() ,eventRequest.getVenue());
        }
        event.setVenue(venue);
        event.setStartTime(eventRequest.getStartTime());
        event.setEndTime(eventRequest.getEndTime());
        event.setMaxCapacity(eventRequest.getMaxCapacity());
        event.setDescription(eventRequest.getDescription());
        event.setName(name);

    }

    private void updateOngoing(Event event , EventRequest eventRequest){

        List<String> lockedChanges = Stream.of(
                ChangedField("name" , event.getName() ,eventRequest.getName().trim()),
                ChangedField("venue" , event.getVenue() ,eventRequest.getVenue().trim()),
                ChangedField("startTime" , event.getStartTime() ,eventRequest.getStartTime()),
                ChangedField("endTime" , event.getEndTime() ,eventRequest.getEndTime()),
                ChangedField("maxCapacity" , event.getMaxCapacity() ,eventRequest.getMaxCapacity()))
                .flatMap(Optional::stream)
                .toList();

        if(!lockedChanges.isEmpty()){
            throw new InvalidEventStateException("An ongoing event can not be changed .Only description can be changed"
            );
        }
        event.setDescription(eventRequest.getDescription());

    }

    private Optional<String> ChangedField(String fieldName , Object storedValue , Object requestValue){
        return Objects.equals(storedValue , requestValue) ? Optional.empty(): Optional.of(fieldName);
    }





    @Transactional
    public void deleteEvent(Long id) {
        Event event = findOrThrow(id);
        EventStatus status = event.getStatus(now());

        if (status == EventStatus.CANCELLED || status == EventStatus.COMPLETED) {
            throw new InvalidEventStateException("A" +status.name().toLowerCase(Locale.ROOT)
            + "ëvent cannot be deleted");
        }
        eventRepo.delete(event);

    }

    public EventResponse create(@Valid EventRequest eventRequest) {
        LocalDateTime now = now();

        String eventName = eventRequest.getName().trim();
        String venue = eventRequest.getVenue().trim();

        validateTimes(eventRequest.getStartTime(),eventRequest.getEndTime());

        validateStartIsInFuture(eventRequest.getStartTime(),now);

        if(eventRepo.existsDuplicate( eventRequest.getName(), eventRequest.getVenue(),eventRequest.getStartTime(), null)){
            throw new DuplicateEventException(eventRequest.getName() ,eventRequest.getVenue());
        }
        Event  event = new Event();
        event.setName(eventName);
        event.setVenue(venue);
        event.setEventDate(eventRequest.getEventDate());
        event.setStartTime(eventRequest.getStartTime());
        event.setEndTime(eventRequest.getEndTime());
        event.setMaxCapacity(eventRequest.getMaxCapacity());
        event.setCreatedAt(now);

        return EventResponse.from(eventRepo.save(event),now);
    }

    private void validateStartIsInFuture(LocalDateTime startTime, LocalDateTime now) {
        if (!startTime.isAfter(now)) {
            throw new InvalidRequestStateException("Start time must be after end time.");
        }
    }
    public void validateTimes(LocalDateTime startTime , LocalDateTime endTime){
            if(!endTime.isAfter(startTime)){
                throw new InvalidRequestStateException("End time must be before start time.");
            }
        }


    @Transactional()
    public EventResponse getEventById(Long id) {
        return EventResponse.from(findOrThrow(id) ,now());

    }

    private Event findOrThrow(Long id) {
        return eventRepo.findById(id).orElseThrow(()-> new EventNotFoundException(id));
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock);
    }


    private EventStatus parseStatus(String value) {
        return Arrays.stream(EventStatus.values())
                .filter(status -> status.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(()-> new InvalidEventStateException( "status must be of " + Arrays.stream(EventStatus.values())
                        .map(status-> status.name().toLowerCase(Locale.ROOT))
                                .collect(Collectors.joining(","))
                ));
    }


    private Optional<Specification<Event>> textFilter(String rawValue, Function<String , Specification<Event>> builder) {
        return Optional.ofNullable(rawValue)
                .map(String::trim)
                .filter(text-> !text.isEmpty())
                .map(builder);
    }


    private Sort.Direction parseDirection(String order) {
        if (order == null || order.isBlank()) {
            return Sort.Direction.ASC;
        }
        return Arrays.stream(Sort.Direction.values())
                .filter(direction -> direction.name().equalsIgnoreCase(order.trim()))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("order", "order must be 'asc' or 'desc'"));
    }
}
