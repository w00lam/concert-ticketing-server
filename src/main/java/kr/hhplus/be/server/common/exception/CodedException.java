package kr.hhplus.be.server.common.exception;
/**
 * Represents an application exception that exposes a structured error code.
 */

public interface CodedException {
    // Stable error code used by API clients instead of parsing human-readable details.
    ErrorCode errorCode();
}
