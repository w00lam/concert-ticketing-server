package kr.hhplus.be.server.common.exception;

public enum ErrorCode {
    // Request payload or command value is invalid.
    REQUEST_BODY_REQUIRED,
    USER_ID_REQUIRED,
    RESERVATION_ID_REQUIRED,
    CONCERT_ID_REQUIRED,
    SEAT_ID_REQUIRED,
    AMOUNT_MUST_BE_POSITIVE,
    AMOUNT_MUST_BE_NON_NEGATIVE,
    PAYMENT_METHOD_REQUIRED,

    // Requested aggregate or resource cannot be found.
    USER_NOT_FOUND,
    SEAT_NOT_FOUND,
    RESERVATION_NOT_FOUND,

    // Request is valid but blocked by current domain state.
    INSUFFICIENT_POINTS,
    RESERVATION_ALREADY_CANCELLED,
    RESERVATION_EXPIRED_OR_PROCESSED
}
