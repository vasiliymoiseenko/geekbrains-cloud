package ru.geekbrains.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.common.constants.Const;
import ru.geekbrains.cloud.common.messages.file.FileRequest;
import ru.geekbrains.cloud.server.service.ClientService;

@Log4j2
public class FileRequestHandler implements ServerRequestHandler {

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg, ClientService clientService) {
    if (!clientService.isAuthorized()) {
      return;
    }
    
    FileRequest fileRequest = (FileRequest) msg;

    String fileName = fileRequest.getFileName();
    String path = fileRequest.getPath();

    Path absolutePath = Paths.get(Const.SERVER_REP, path, fileName).toAbsolutePath();
    File file = new File(absolutePath.toString());

    clientService.sendFile(ctx.channel(), file, Const.CLIENT_REP);
  }
}
