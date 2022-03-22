package ru.geekbrains.cloud.common.messages.reg;

import ru.geekbrains.cloud.common.messages.ServerErrorResponse;

public class RegErrorResponse extends ServerErrorResponse {

  public RegErrorResponse(String reason) {
    super(reason);
  }
}
