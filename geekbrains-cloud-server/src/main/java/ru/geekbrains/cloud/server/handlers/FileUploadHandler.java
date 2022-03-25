package ru.geekbrains.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.common.constants.Const;
import ru.geekbrains.cloud.common.messages.file.FileMessage;
import ru.geekbrains.cloud.server.service.ClientService;

@Log4j2
public class FileUploadHandler implements ServerRequestHandler {

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg, ClientService clientService) {
    if (!clientService.isAuthorized()) {
      return;
    }

    FileMessage fileMessage = (FileMessage) msg;

    boolean append = fileMessage.partNumber != 1;

    try (FileOutputStream fos = new FileOutputStream(Paths.get(Const.SERVER_REP, fileMessage.path, fileMessage.filename).toString(), append)) {

      log.info(ctx.name() + ": File " + fileMessage.filename + " part " + fileMessage.partNumber + " / " + fileMessage.partsCount + " received");
      fos.write(fileMessage.data);

      if (fileMessage.partNumber == fileMessage.partsCount) {
        log.info(ctx.name() + ": File " + fileMessage.filename + " is completely uploaded");
        clientService.sendList(ctx, fileMessage.path);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
