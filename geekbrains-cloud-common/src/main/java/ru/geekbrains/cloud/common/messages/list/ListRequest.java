package ru.geekbrains.cloud.common.messages.list;

import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.Value;
import ru.geekbrains.cloud.common.messages.AbstractMessage;

@Value
public class ListRequest extends AbstractMessage {

  private String path;

}
