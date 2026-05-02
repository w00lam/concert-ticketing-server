package kr.hhplus.be.server.common.exception;

public enum ErrorCode {
    // Request payload or command value is invalid.
    CLIENT_INPUT_ERROR,
    // Requested aggregate or resource cannot be found.
    RESOURCE_NOT_FOUND,
    // Request is valid but blocked by current domain state.
    BUSINESS_RULE_VIOLATION
}
