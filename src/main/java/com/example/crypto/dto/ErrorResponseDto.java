package com.example.crypto.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard error response for API exceptions")
public class ErrorResponseDto {

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error type", example = "Bad Request")
    private String error;

    @Schema(description = "Detailed error message", example = "Invalid date format, expected yyyy-MM-dd")
    private String message;

    @Schema(description = "Timestamp of the error", example = "2025-08-27T14:30:00")
    private LocalDateTime timestamp;
}

