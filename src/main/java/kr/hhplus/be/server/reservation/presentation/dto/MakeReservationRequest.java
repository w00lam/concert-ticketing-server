package kr.hhplus.be.server.reservation.presentation.dto;

import java.util.UUID;

public record MakeReservationRequest(UUID userId, UUID concertId, UUID seatId) {
}