package kr.hhplus.be.server.common.exception;
/**
 * Defines detailed error codes used in API error responses.
 */

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
    INVALID_REQUEST_BODY,
    INVALID_REQUEST_PARAMETER,
    INVALID_REQUEST_FIELD,

    // Requested aggregate or resource cannot be found.
    RESOURCE_NOT_FOUND,
    USER_NOT_FOUND,
    SEAT_NOT_FOUND,
    RESERVATION_NOT_FOUND,

    // Request is valid but blocked by current domain state.
    INSUFFICIENT_POINTS,
    SEAT_ALREADY_RESERVED,
    RESERVATION_ALREADY_CANCELLED,
    RESERVATION_EXPIRED_OR_PROCESSED,

    // Unexpected server-side failure.
    INTERNAL_SERVER_ERROR
}
