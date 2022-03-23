package ru.geekbrains.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import ru.geekbrains.cloud.common.messages.file.DeleteRequest;
import ru.geekbrains.cloud.server.service.FileService;

public class DeleteRequestHandler implements ServerRequestHandler{

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg) {
    DeleteRequest deleteRequest = (DeleteRequest) msg;

    String fileName = deleteRequest.getFileName();
    String path = deleteRequest.getPath();

    Path absolutePath = Paths.get("server_repository", path, fileName).toAbsolutePath();
    File file = new File(absolutePath.toString());

    file.delete();

    FileService.sendList(ctx, path);
  }
}
