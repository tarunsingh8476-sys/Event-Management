package com.Backend.EventManagement.DTO;

import com.Backend.EventManagement.Entity.Event;
import com.Backend.EventManagement.Entity.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {

    private Long id;
    private String name;
    private String description;
    private String venue;
    private LocalDate eventDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxCapacity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private EventStatus status;


    public static EventResponse from(Event event ,LocalDateTime now){
        return new EventResponse(event.getId(),
                event.getName(),
                event.getDescription(),
                event.getVenue(),
                event.getEventDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getMaxCapacity(),
                event.getCreatedAt(),
                event.getUpdatedAt(),
                event.getStatus(now)

        );
    }

 }
