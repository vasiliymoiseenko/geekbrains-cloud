package ru.geekbrains.cloud.common.messages.reg;

import lombok.Value;
import ru.geekbrains.cloud.common.messages.AbstractMessage;

@Value
public class RegRequest extends AbstractMessage {

  private String login;
  private String password;
  private int capacity;
}
