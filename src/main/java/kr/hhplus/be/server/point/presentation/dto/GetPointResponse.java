package kr.hhplus.be.server.point.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.point.application.port.in.GetPointResult;

import java.util.UUID;

@Schema(description = "포인트 잔액 조회 응답")
public record GetPointResponse(
        @Schema(description = "사용자 ID")
        UUID userId,
        @Schema(description = "포인트 잔액", example = "10000")
        int balance
) {
    public static GetPointResponse from(GetPointResult result) {
        return new GetPointResponse(result.userId(), result.balance());
    }
}
