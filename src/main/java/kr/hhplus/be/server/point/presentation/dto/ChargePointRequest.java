package kr.hhplus.be.server.point.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;
/**
 * Carries point API request values.
 */

@Schema(description = "포인트 충전 요청")
public record ChargePointRequest(
        @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull UUID userId,
        @Schema(description = "충전 금액", example = "10000", requiredMode = Schema.RequiredMode.REQUIRED)
        @Positive int amount
) {
}
