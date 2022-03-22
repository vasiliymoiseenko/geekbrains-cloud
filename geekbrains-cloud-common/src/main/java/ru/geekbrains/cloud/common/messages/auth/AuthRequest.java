package ru.geekbrains.cloud.common.messages.auth;

import lombok.Value;
import ru.geekbrains.cloud.common.messages.AbstractMessage;

@Value
public class AuthRequest extends AbstractMessage {

  private String login;
  private String password;
}
