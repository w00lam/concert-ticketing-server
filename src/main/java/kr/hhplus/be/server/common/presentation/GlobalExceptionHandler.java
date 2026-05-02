package kr.hhplus.be.server.common.presentation;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ClientInputException;
import kr.hhplus.be.server.common.exception.CodedException;
import kr.hhplus.be.server.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientInputException.class)
    public ProblemDetail handleClientInput(ClientInputException exception) {
        return problem(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ProblemDetail handleBusinessRuleViolation(BusinessRuleViolationException exception) {
        return problem(HttpStatus.CONFLICT, exception);
    }

    private ProblemDetail problem(HttpStatus status, RuntimeException exception) {
        // ProblemDetail keeps API error responses consistent without custom DTO plumbing.
        var problem = ProblemDetail.forStatus(status);
        problem.setDetail(exception.getMessage());
        if (exception instanceof CodedException codedException) {
            problem.setProperty("code", codedException.errorCode().name());
        }
        return problem;
    }
}
