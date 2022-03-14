package ru.geekbrains.cloud.common.messages;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import ru.geekbrains.cloud.common.type.FileInfo;

public class ListResponse extends AbstractMessage {

  @Getter
  @Setter
  private List<FileInfo> list;

  public ListResponse(List<FileInfo> list) {
    this.list = list;
  }
}
