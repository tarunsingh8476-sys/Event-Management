package com.Backend.EventManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private LocalDateTime timeStamp;
    private int status;
    private String error;
    private String code;
    private String message;
    private String path;
    private List<FieldIssue> details;


    public static class FieldIssue {
        private String field;
        private String message;

        public FieldIssue(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }
        public String getMessage() {
            return message;
        }

    }

}
