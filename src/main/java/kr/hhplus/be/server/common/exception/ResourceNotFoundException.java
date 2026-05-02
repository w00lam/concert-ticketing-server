package kr.hhplus.be.server.common.exception;

public class ResourceNotFoundException extends RuntimeException implements CodedException {
    private final ErrorCode errorCode;

    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        // Keep lookup failures explicit so HTTP adapters can map them to 404.
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public ErrorCode errorCode() {
        return errorCode;
    }
}
