package ru.geekbrains.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.common.messages.ListResponse;
import ru.geekbrains.cloud.common.type.FileInfo;

@Log4j2
public class ListRequestHandler implements ServerRequestHandler {

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg) {
    try {
      Path path = Paths.get("server_repository");

      List<FileInfo> list = Files.list(path).map(FileInfo::new).collect(Collectors.toList());

      ctx.writeAndFlush(new ListResponse(list)).addListener(channelFuture -> {
        if (channelFuture.isSuccess()) {
          log.info("List sended to client");
        } else {
          log.error(channelFuture.cause().getMessage());
        }
      });

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
