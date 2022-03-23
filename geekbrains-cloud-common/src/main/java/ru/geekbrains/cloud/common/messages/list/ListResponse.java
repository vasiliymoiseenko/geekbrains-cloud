package ru.geekbrains.cloud.common.messages.list;

import java.util.List;
import lombok.Value;
import ru.geekbrains.cloud.common.messages.AbstractMessage;

@Value
public class ListResponse extends AbstractMessage {

  private List<FileInfo> list;
  private String path;
}
