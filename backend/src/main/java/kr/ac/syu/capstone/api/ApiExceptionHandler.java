package kr.ac.syu.capstone.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.*;
import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.ErrorResponse;
import java.time.Clock;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private final Clock clock;
    public ApiExceptionHandler(Clock clock) { this.clock = clock; }
    @ExceptionHandler(ApiException.class)
    ResponseEntity<ApiModels.Failure> business(ApiException e, HttpServletRequest request) {
        log.info("participation_api_rejected requestId={} code={}", RequestMetadataFilter.requestId(request), e.code());
        return ResponseEntity.status(e.code().status()).body(ApiResponses.failure(e.code(), e.evaluatedAt(), e.details(), request));
    }
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConstraintViolationException.class})
    ResponseEntity<ApiModels.Failure> invalid(Exception e, HttpServletRequest request) { return error(ErrorCode.INVALID_QUERY_PARAMETER, request); }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiModels.Failure> body(Exception e, HttpServletRequest request) { return error(ErrorCode.INVALID_REQUEST, request); }
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class, HttpMediaTypeNotSupportedException.class,
        HttpMediaTypeNotAcceptableException.class, NoResourceFoundException.class})
    ResponseEntity<ApiModels.Failure> protocol(Exception e, HttpServletRequest request) {
        ErrorResponse response = (ErrorResponse) e;
        ErrorCode code = switch (response.getStatusCode().value()) {
            case 405 -> ErrorCode.METHOD_NOT_ALLOWED;
            case 406 -> ErrorCode.NOT_ACCEPTABLE;
            case 415 -> ErrorCode.UNSUPPORTED_MEDIA_TYPE;
            default -> ErrorCode.API_NOT_FOUND;
        };
        return ResponseEntity.status(code.status()).headers(response.getHeaders())
            .body(ApiResponses.failure(code, clock.instant(), Map.of(), request));
    }
    @ExceptionHandler(TransientDataAccessException.class)
    ResponseEntity<ApiModels.Failure> temporary(Exception e, HttpServletRequest request) {
        log.warn("participation_api_transient_failure requestId={}", RequestMetadataFilter.requestId(request), e);
        return error(ErrorCode.PARTICIPATION_TEMPORARILY_UNAVAILABLE, request);
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiModels.Failure> unexpected(Exception e, HttpServletRequest request) {
        log.error("participation_api_failure requestId={}", RequestMetadataFilter.requestId(request), e);
        return error(ErrorCode.INTERNAL_SERVER_ERROR, request);
    }
    private ResponseEntity<ApiModels.Failure> error(ErrorCode code, HttpServletRequest req) {
        return ResponseEntity.status(code.status()).body(ApiResponses.failure(code, clock.instant(), Map.of(), req));
    }
}
