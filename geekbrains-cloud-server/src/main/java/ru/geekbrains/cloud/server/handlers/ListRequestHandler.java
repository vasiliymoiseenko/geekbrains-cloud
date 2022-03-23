package ru.geekbrains.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.common.messages.list.ListRequest;
import ru.geekbrains.cloud.common.service.FileService;

@Log4j2
public class ListRequestHandler implements ServerRequestHandler {

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg) {
    ListRequest listRequest = (ListRequest) msg;

    String path = listRequest.getPath();

    FileService.sendList(ctx, path);
  }
}
