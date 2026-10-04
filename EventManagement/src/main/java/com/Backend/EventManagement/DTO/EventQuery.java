package com.Backend.EventManagement.DTO;

public record EventQuery(
        String search,
        String venue,
        String status,
        int page,
        int limit,
        String sort,
        String order


) {
}
