package ru.geekbrains.cloud.common.messages.list;

import ru.geekbrains.cloud.common.messages.abs.ServerErrorResponse;

public class ListErrorResponse extends ServerErrorResponse {

  public ListErrorResponse(String reason) {
    super(reason);
  }
}
