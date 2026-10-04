package com.Backend.EventManagement.DTO;

import java.util.List;

public record PagedResponse<T>(List<T> data, Pagination pagination) {

    public record Pagination(int page ,int limit , long totalItems , int totalPages) {

    }
}
