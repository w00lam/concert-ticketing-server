package kr.hhplus.be.server.common.exception;

public class ClientInputException extends IllegalArgumentException implements CodedException {
    public ClientInputException(String message) {
        // Client input errors keep bad requests separate from domain state conflicts.
        super(message);
    }

    @Override
    public ErrorCode errorCode() {
        return ErrorCode.CLIENT_INPUT_ERROR;
    }
}
