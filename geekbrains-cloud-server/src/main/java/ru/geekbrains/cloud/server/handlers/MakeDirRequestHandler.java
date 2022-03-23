package ru.geekbrains.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.common.messages.file.MakeDirRequest;
import ru.geekbrains.cloud.common.service.FileService;

@Log4j2
public class MakeDirRequestHandler implements ServerRequestHandler{

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg) {
    MakeDirRequest makeDirRequest = (MakeDirRequest) msg;

    String fileName = makeDirRequest.getFileName();
    String path = makeDirRequest.getPath();

    Path absolutePath = Paths.get("server_repository", path, fileName).toAbsolutePath();
    File file = new File(absolutePath.toString());

    Boolean result = file.mkdir();
    log.info(ctx.name() + " Make directory " + absolutePath + " result " + result);

    FileService.sendList(ctx, path);
  }
}
