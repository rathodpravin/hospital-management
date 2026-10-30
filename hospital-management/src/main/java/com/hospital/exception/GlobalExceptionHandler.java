package com.hospital.exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j @RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<Map<String, Object>> build(HttpStatus s, String msg) {
        return ResponseEntity.status(s).body(Map.of("status", s.value(), "error", s.getReasonPhrase(),
            "message", msg, "timestamp", LocalDateTime.now().toString()));
    }
    @ExceptionHandler(ResourceNotFoundException.class) ResponseEntity<?> notFound(ResourceNotFoundException e) { return build(HttpStatus.NOT_FOUND, e.getMessage()); }
    @ExceptionHandler(NoResourceFoundException.class) ResponseEntity<?> noResource(NoResourceFoundException e) { return build(HttpStatus.NOT_FOUND, "Resource not found"); }
    @ExceptionHandler(BadRequestException.class) ResponseEntity<?> bad(BadRequestException e) { return build(HttpStatus.BAD_REQUEST, e.getMessage()); }
    @ExceptionHandler(HttpMessageNotReadableException.class) ResponseEntity<?> unreadable(HttpMessageNotReadableException e) { return build(HttpStatus.BAD_REQUEST, "Malformed JSON or invalid value"); }
    @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> invalid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
            .map(f -> f.getField() + ": " + f.getDefaultMessage()).collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, msg);
    }
    @ExceptionHandler(BadCredentialsException.class) ResponseEntity<?> creds(BadCredentialsException e) { return build(HttpStatus.UNAUTHORIZED, "Invalid username or password"); }
    @ExceptionHandler(AccessDeniedException.class) ResponseEntity<?> denied(AccessDeniedException e) { return build(HttpStatus.FORBIDDEN, "You do not have permission for this action"); }
    @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<?> integrity(DataIntegrityViolationException e) { return build(HttpStatus.CONFLICT, "Operation conflicts with existing data (duplicate or record in use)"); }
    @ExceptionHandler(Exception.class) ResponseEntity<?> other(Exception e) { log.error("Unexpected error", e); return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong"); }
}
