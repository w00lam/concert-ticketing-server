package kr.hhplus.be.server.reservation.domain.model;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;

public final class ReservationExceptions {
    private static final String SEAT_ALREADY_RESERVED_MESSAGE = "이미 예약 중인 좌석입니다.";
    private static final String ALREADY_CANCELLED_MESSAGE = "이미 취소된 예약입니다.";
    private static final String EXPIRED_OR_PROCESSED_MESSAGE = "예약이 만료되었거나 이미 처리되었습니다.";

    private ReservationExceptions() {
    }

    public static BusinessRuleViolationException seatAlreadyReserved() {
        return new BusinessRuleViolationException(
                ErrorCode.SEAT_ALREADY_RESERVED,
                SEAT_ALREADY_RESERVED_MESSAGE
        );
    }

    public static BusinessRuleViolationException alreadyCancelled() {
        return new BusinessRuleViolationException(
                ErrorCode.RESERVATION_ALREADY_CANCELLED,
                ALREADY_CANCELLED_MESSAGE
        );
    }

    public static BusinessRuleViolationException expiredOrProcessed() {
        return new BusinessRuleViolationException(
                ErrorCode.RESERVATION_EXPIRED_OR_PROCESSED,
                EXPIRED_OR_PROCESSED_MESSAGE
        );
    }
}
