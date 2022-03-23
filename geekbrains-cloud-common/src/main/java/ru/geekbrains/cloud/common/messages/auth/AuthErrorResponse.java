package ru.geekbrains.cloud.common.messages.auth;

import ru.geekbrains.cloud.common.messages.abs.ServerErrorResponse;

public class AuthErrorResponse extends ServerErrorResponse {

  public AuthErrorResponse(String reason) {
    super(reason);
  }
}
