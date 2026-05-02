package kr.hhplus.be.server.point.presentation.dto;

import kr.hhplus.be.server.user.domain.model.User;

public record ChargePointRequest(User user, int amount) {
}
