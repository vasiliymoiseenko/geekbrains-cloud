package ru.geekbrains.cloud.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import javafx.application.Platform;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.client.javafx.Controller;
import ru.geekbrains.cloud.common.messages.list.ListResponse;
import ru.geekbrains.cloud.common.messages.list.FileInfo;

@Log4j2
public class ListResponseHandler implements ClientRequestHandler {

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg, Controller controller) {
    ListResponse listResponse = (ListResponse) msg;

    List<FileInfo> list = listResponse.getList();
    String path = listResponse.getPath();

    log.info("Received a list from the server: " + path);

    Platform.runLater(() -> {
      controller.updateList(list);
      controller.updatePathField(path);
    });
  }
}
