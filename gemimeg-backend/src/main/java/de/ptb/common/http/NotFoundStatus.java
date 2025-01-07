package de.ptb.common.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class NotFoundStatus extends HttpStatusCodeException {

  public NotFoundStatus(String message) {
    super(HttpStatus.NOT_FOUND, message);
  }

  public NotFoundStatus(Class<?> itemType, Object id) {
    this(itemType.getSimpleName() + " with ID " + id + " could not be found.");
  }
}
