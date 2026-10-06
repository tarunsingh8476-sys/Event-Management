package com.Backend.EventManagement.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String description;

    private String venue;

    @Column( nullable = false)
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
            return EventStatus.ONGOING;
        }
        return  EventStatus.COMPLETED;
    }
}
