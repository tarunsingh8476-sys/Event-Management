package com.Backend.EventManagement.Repository;

import com.Backend.EventManagement.Entity.Event;
import com.Backend.EventManagement.Entity.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepo extends JpaRepository<Event,Long> {


    @Query("""
        SELECT COUNT(e) > 0
        FROM Event e
        WHERE LOWER(e.name) = LOWER(:name)
          AND LOWER(e.venue) = LOWER(:venue)
          AND e.startTime = :startTime
          AND (:excludeId IS NULL OR e.id <> :excludeId)
        """)
    boolean existsDuplicate(
            @Param("name") String name,
            @Param("venue") String venue,
            @Param("startTime") LocalDateTime startTime,
            @Param("excludeId") Long excludeId
    );

    Page<Event> findByNameContainingIgnoreCase(String trim, Pageable pageable);
}
