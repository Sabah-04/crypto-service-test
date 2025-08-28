package com.example.crypto.controller;


import com.example.crypto.dto.CryptoNormDto;
import com.example.crypto.dto.CryptoStatsDto;
import com.example.crypto.dto.ErrorResponseDto;
import com.example.crypto.service.CryptoPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;


@Tag(name = "Crypto", description = "Endpoints for cryptocurrency statistics and normalization")
@RestController
@RequestMapping("/crypto")
@RequiredArgsConstructor
@Validated
public class CryptoController {

    private final CryptoPriceService cryptoPriceService;


    @Operation(
            summary = "Get statistics of the given cryptocurrency",
            description = "Returns min, max, oldest, newest, and normalized range for the given cryptocurrency symbol."
    )
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CryptoStatsDto.class)))
    @ApiResponse(responseCode = "400", description = "Bad request (invalid input or unsupported crypto)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class,
                            example = "{ \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Crypto symbol not supported: pikabc\", \"timestamp\": \"2025-08-27T14:30:00\" }")
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "No crypto data or stats found for given crypto",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class,
                            example = "{ \"status\": 404, \"error\": \"Not Found\", \"message\": \"No stats found for the symbol: BTC\", \"timestamp\": \"2025-08-27T14:30:00\" }")
            )
    )

    @GetMapping("/stats")
    public CryptoStatsDto getCryptoStats(@RequestParam("symbol") @NotBlank String symbol){
        return cryptoPriceService.getCryptoStats(symbol);
    }


    @Operation(
            summary = "Get highest normalized crypto of the day",
            description = "Returns the cryptocurrency with the highest normalized range for the given day."
    )
    @ApiResponse(responseCode = "200", description = "Crypto with highest normalized range",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CryptoNormDto.class)))
    @ApiResponse(responseCode = "400", description = "Bad request (invalid date or validation)",
            content = @Content( mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class,
                    example = "{ \"status\": 404, \"error\": \"Not Found\", \"message\": \"Invalid date format, expected yyyy-MM-dd\", \"timestamp\": \"2025-08-27T14:30:00\" }")))
    @ApiResponse(
            responseCode = "404",
            description = "No crypto data found for the given day",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class,
                            example = "{ \"status\": 404, \"error\": \"Not Found\", \"message\": \"No crypto data found for the day: 2025-08-27\", \"timestamp\": \"2025-08-27T14:30:00\" }")
            )
    )
    @GetMapping("/highest")
    public CryptoNormDto getHighestCryptoOfDay(@RequestParam("day") @NotBlank String day){
        LocalDate localDate = LocalDate.parse(day);
        return cryptoPriceService.getHighestCryptoOfDay(localDate);
    }

    @Operation(
            summary = "Get all cryptocurrencies sorted by normalized range",
            description = "Returns a list of cryptocurrencies sorted descending by their normalized range."
    )
    @ApiResponse(responseCode = "200", description = "List of cryptos sorted by normalized range",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CryptoNormDto.class)))
    @ApiResponse(
            responseCode = "404",
            description = "No crypto data found for sorting",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class,
                            example = "{ \"status\": 404, \"error\": \"Not Found\", \"message\": \"No crypto data found \", \"timestamp\": \"2025-08-27T14:30:00\" }")
            )
    )
    @GetMapping("/sorted")
    public List<CryptoNormDto> getCryptoSortedByNormRange(){
        return cryptoPriceService.getCryptoSortedByNormRange();
    }

}


