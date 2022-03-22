package ru.geekbrains.cloud.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import javafx.application.Platform;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.client.javafx.Controller;
import ru.geekbrains.cloud.common.messages.list.ListResponse;
import ru.geekbrains.cloud.common.type.FileInfo;

@Log4j2
public class ListResponseHandler implements ClientRequestHandler {

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg, Controller controller) {
    log.info("Received a list from the server");
    List<FileInfo> list = ((ListResponse) msg).getList();
    Platform.runLater(() -> controller.updateList(list));
  }
}
