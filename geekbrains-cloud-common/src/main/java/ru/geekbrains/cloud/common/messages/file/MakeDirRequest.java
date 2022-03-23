package ru.geekbrains.cloud.common.messages.file;

import ru.geekbrains.cloud.common.messages.abs.AbstractFileMessage;

public class MakeDirRequest extends AbstractFileMessage {

  public MakeDirRequest(String fileName, String path) {
    super(fileName, path);
  }
}
