package com.Backend.EventManagement.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String description;

    private String venue;

    @Column( name = "event_date", nullable = false)
    private LocalDate eventDate;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private int maxCapacity;
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EventStatus getStatus(LocalDateTime now) {
        if (now.isBefore(startTime)) {
            return EventStatus.UPCOMING;
        }
        if (now.isBefore(endTime)) {
            return EventStatus.PENDING;
        }
        return  EventStatus.COMPLETED;
    }
}
