package kr.hhplus.be.server.point.presentation.dto;

import kr.hhplus.be.server.point.application.port.in.GetPointResult;

import java.util.UUID;

public record GetPointResponse(UUID userId, int balance) {
    public static GetPointResponse from(GetPointResult result) {
        return new GetPointResponse(result.userId(), result.balance());
    }
}
