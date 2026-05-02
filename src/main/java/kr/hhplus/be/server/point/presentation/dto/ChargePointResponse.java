package kr.hhplus.be.server.point.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.point.application.port.in.ChargePointResult;

import java.util.UUID;

@Schema(description = "포인트 충전 응답")
public record ChargePointResponse(
        @Schema(description = "사용자 ID")
        UUID userId,
        @Schema(description = "충전 후 포인트 잔액", example = "10000")
        int balance
) {
    public static ChargePointResponse from(ChargePointResult result) {
        return new ChargePointResponse(result.userId(), result.balance());
    }
}
