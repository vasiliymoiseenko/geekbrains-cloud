package ru.geekbrains.cloud.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import javafx.application.Platform;
import ru.geekbrains.cloud.client.javafx.Controller;
import ru.geekbrains.cloud.common.messages.ListResponse;
import ru.geekbrains.cloud.common.type.FileInfo;

public class ListResponseHandler implements ClientRequestHandler{

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg, Controller controller) {
    System.out.println("List");
    List<FileInfo> list = ((ListResponse) msg).getList();
    Platform.runLater(() -> controller.updateList(list));
  }
}
