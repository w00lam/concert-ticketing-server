package kr.hhplus.be.server.common.exception;

public interface CodedException {
    // Stable error code used by API clients instead of parsing human-readable details.
    ErrorCode errorCode();
}
