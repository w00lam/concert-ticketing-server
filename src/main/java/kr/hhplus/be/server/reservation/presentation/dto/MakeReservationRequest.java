package kr.hhplus.be.server.reservation.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MakeReservationRequest(
        @NotNull UUID userId,
        @NotNull UUID concertId,
        @NotNull UUID seatId
) {
}
