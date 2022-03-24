package ru.geekbrains.cloud.common.messages.file;

import ru.geekbrains.cloud.common.messages.abs.ServerErrorResponse;

public class FileErrorResponse extends ServerErrorResponse {

  public FileErrorResponse(String reason) {
    super(reason);
  }
}
