package ru.geekbrains.cloud.common.messages.auth;

import ru.geekbrains.cloud.common.messages.ServerErrorResponse;

public class AuthErrorResponse extends ServerErrorResponse {

  public AuthErrorResponse(String reason) {
    super(reason);
  }
}
