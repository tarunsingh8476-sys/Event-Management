package com.Backend.EventManagement.Repository;

import com.Backend.EventManagement.Entity.Event;
import com.Backend.EventManagement.Entity.EventStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventSpecifications {

        /** Matches events whose name OR description contains the text (case-insensitive). */
        public static Specification<Event> nameOrDescriptionContains(String text) {
            String pattern = likePattern(text);
            return (root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), pattern, '\\'),
                    cb.like(cb.lower(root.get("description")), pattern, '\\')
            );
        }


        public static Specification<Event> venueContains(String text) {
            String pattern = likePattern(text);
            return (root, query, cb) -> cb.like(cb.lower(root.get("venue")), pattern, '\\');
        }


        public static Specification<Event> startsOnOrAfter(LocalDateTime moment) {
            return (root, query, cb) -> cb.greaterThanOrEqualTo(root.<LocalDateTime>get("startTime"), moment);
        }


        public static Specification<Event> startsBefore(LocalDateTime moment) {
            return (root, query, cb) -> cb.lessThan(root.<LocalDateTime>get("startTime"), moment);
        }

        public static Specification<Event> hasStatus(EventStatus status, LocalDateTime now) {
            return (root, query, cb) -> switch (status) {
                case CANCELLED -> cb.isTrue(root.get("cancelled"));
                case UPCOMING -> cb.and(
                        cb.isFalse(root.get("cancelled")),
                        cb.greaterThan(root.<LocalDateTime>get("startTime"), now));
                case ONGOING -> cb.and(
                        cb.isFalse(root.get("cancelled")),
                        cb.lessThanOrEqualTo(root.<LocalDateTime>get("startTime"), now),
                        cb.greaterThan(root.<LocalDateTime>get("endTime"), now));
                case COMPLETED -> cb.and(
                        cb.isFalse(root.get("cancelled")),
                        cb.lessThanOrEqualTo(root.<LocalDateTime>get("endTime"), now));
                default -> throw new IllegalStateException("Unexpected value: " + status);
            };
        }
        private static String likePattern(String text) {
            String escaped = text.toLowerCase()
                    .replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_");
            return "%" + escaped + "%";
        }
    }

