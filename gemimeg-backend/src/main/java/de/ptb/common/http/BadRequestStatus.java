package de.ptb.common.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class BadRequestStatus extends HttpStatusCodeException {

  public BadRequestStatus(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }
}
