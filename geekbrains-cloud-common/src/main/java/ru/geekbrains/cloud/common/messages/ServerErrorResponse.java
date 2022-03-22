package ru.geekbrains.cloud.common.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

@AllArgsConstructor
public class ServerErrorResponse extends AbstractMessage {

  @Getter
  private String reason;
}
