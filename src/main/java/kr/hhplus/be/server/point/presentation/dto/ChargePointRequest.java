package kr.hhplus.be.server.point.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ChargePointRequest(
        @NotNull UUID userId,
        @Positive int amount
) {
}
