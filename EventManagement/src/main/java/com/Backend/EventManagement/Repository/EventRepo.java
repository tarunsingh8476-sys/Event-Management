package com.Backend.EventManagement.Repository;

import com.Backend.EventManagement.Entity.Event;
import com.Backend.EventManagement.Entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepo extends JpaRepository<Event,Long> , JpaSpecificationExecutor<Event> {

 @Query(""" 
         select count(e) > 0 from Event
                  where lower(e.name) = lower(:name)
                           and lower(e.venue) = lower(:venue)
                                    and e.startTime = :startTime and e.status <> :cancelledstatus """)
    boolean existsDuplicate(@Param("name") String name ,
    @Param("venue") String venue,
    @Param("startTime")
    LocalDateTime startTime,
    @Param("cancelledStatus")
    EventStatus cancelledStatus );
}
