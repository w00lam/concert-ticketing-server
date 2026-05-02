package kr.hhplus.be.server.common.exception;

public class ResourceNotFoundException extends RuntimeException implements CodedException {
    public ResourceNotFoundException(String resourceName, Object resourceId) {
        // Keep lookup failures explicit so HTTP adapters can map them to 404.
        super(resourceName + " not found: " + resourceId);
    }

    @Override
    public ErrorCode errorCode() {
        return ErrorCode.RESOURCE_NOT_FOUND;
    }
}
