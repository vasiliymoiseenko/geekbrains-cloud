package ru.geekbrains.cloud.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.client.javafx.Controller;
import ru.geekbrains.cloud.common.messages.auth.AuthErrorResponse;

@Log4j2
public class AuthErrorResponseHandler implements ClientRequestHandler{

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg, Controller controller) {
    String reason = ((AuthErrorResponse) msg).getReason();

    log.info("Authorization failed: " + reason);

    Platform.runLater(() -> controller.showAuthMessage(reason, Color.RED));
  }
}
