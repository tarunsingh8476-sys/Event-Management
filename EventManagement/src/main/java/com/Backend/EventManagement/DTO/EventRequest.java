package com.Backend.EventManagement.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EventRequest {

    @NotBlank(message = "Event name is required")
    @Size(min= 5 , max = 50)
    private String name;
    private String description;

    @NotBlank(message = "Venue is required and cannot be blank")
    private String venue;

    @NotNull(message = "Date cannot be null")
    private LocalDate eventDate;

    @NotNull(message = "Start time is required (format : 2026-04-06T11:00:00)")
    private LocalDateTime startTime;
    @NotNull(message = "End time is required (format : 2026-04-06T12:00:00)")
    private LocalDateTime endTime;

    @Positive(message = "Capacity should be in positive numbers ")
    @Max(value = 200 , message = "Maximum capacity cannot be more than 200")
    private int maxCapacity;

}
