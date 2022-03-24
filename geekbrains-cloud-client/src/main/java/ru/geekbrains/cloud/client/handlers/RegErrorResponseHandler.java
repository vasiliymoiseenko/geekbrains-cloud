package ru.geekbrains.cloud.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.client.javafx.Controller;
import ru.geekbrains.cloud.common.messages.reg.RegErrorResponse;

@Log4j2
public class RegErrorResponseHandler implements ClientRequestHandler {

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg, Controller controller) {
    String reason = ((RegErrorResponse) msg).getReason();

    log.info("Registrarion failed: " + reason);

    Platform.runLater(() -> controller.showRegMessage(reason, Color.RED));
  }
}
