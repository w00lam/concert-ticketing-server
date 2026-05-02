package kr.hhplus.be.server.common.exception;

public class ClientInputException extends IllegalArgumentException implements CodedException {
    private final ErrorCode errorCode;

    public ClientInputException(ErrorCode errorCode, String message) {
        // Client input errors keep bad requests separate from domain state conflicts.
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public ErrorCode errorCode() {
        return errorCode;
    }
}
