package com.Backend.EventManagement.Controllers;

import com.Backend.EventManagement.DTO.EventRequest;
import com.Backend.EventManagement.DTO.EventResponse;
import com.Backend.EventManagement.DTO.PagedResponse;
import com.Backend.EventManagement.Service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;
    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest eventRequest) {
       EventResponse created = eventService.create(eventRequest);

       URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
               .buildAndExpand(created.getId())
               .toUri();

       return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public PagedResponse<EventResponse> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String venue,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "date") String sort,
            @RequestParam(defaultValue = "asc") String order) {

        return eventService.getAllEvents(page,limit,sort,order,search);
    }

    @PostMapping("/addEvent")
    public ResponseEntity<EventResponse> addEvent(@RequestBody EventRequest eventRequest) {
        return ResponseEntity.ok(eventService.addEvent(eventRequest));

    }
    @GetMapping("/geteventbyId/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/updateEvent/{id}")
    public ResponseEntity<EventResponse> updateEvent( @PathVariable Long id , @RequestParam EventRequest eventRequest) {
        return ResponseEntity.ok(eventService.updateEvent(id , eventRequest));
    }


    @DeleteMapping("/deleteEvent/{id}")
    public ResponseEntity<Void> deleteEvent(Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }



}
