package dev.estebannieva.tasks.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends RuntimeException {

  private HttpStatus status = HttpStatus.NOT_FOUND;

  public NotFoundException(String message) {
    super(message);
  }
}
