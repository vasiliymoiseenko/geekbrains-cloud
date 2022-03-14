package ru.geekbrains.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import ru.geekbrains.cloud.common.messages.ListResponse;
import ru.geekbrains.cloud.common.type.FileInfo;

public class ListRequestHandler implements ServerRequestHandler {

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg) {
    try {
      List<FileInfo> list;

      Path path = Paths.get("server_repository");

      list = Files.list(path).map(FileInfo::new).collect(Collectors.toList());

      ctx.writeAndFlush(new ListResponse(list));

      System.out.println("FileList sended to client " + list);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
