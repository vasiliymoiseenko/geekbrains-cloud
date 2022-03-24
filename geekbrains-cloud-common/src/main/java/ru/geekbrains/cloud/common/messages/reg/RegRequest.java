package ru.geekbrains.cloud.common.messages.reg;

import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.geekbrains.cloud.common.messages.abs.AbstractMessage;

@Value
public class RegRequest extends AbstractMessage {

  String login;
  String password;
  int capacity;
}
