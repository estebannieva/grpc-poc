package dev.estebannieva.tasks.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Object> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
    Map<String, String> body = Map.of(
        "timestamp", LocalDateTime.now().toString(),
        "status", String.valueOf(ex.getStatus().value()),
        "error", ex.getStatus().name(),
        "message", ex.getMessage(),
        "path", request.getRequestURI()
    );

    return new ResponseEntity<>(body, ex.getStatus());
  }
}
