package kr.hhplus.be.server.point.presentation.dto;

import kr.hhplus.be.server.point.application.port.in.ChargePointResult;

import java.util.UUID;

public record ChargePointResponse(UUID userId, int balance) {
    public static ChargePointResponse from(ChargePointResult result) {
        return new ChargePointResponse(result.userId(), result.balance());
    }
}
