package ru.geekbrains.cloud.common.messages.del;

import lombok.Value;
import ru.geekbrains.cloud.common.messages.AbstractMessage;

@Value
public class DeleteRequest extends AbstractMessage {

  String fileName;
  String path;

}
