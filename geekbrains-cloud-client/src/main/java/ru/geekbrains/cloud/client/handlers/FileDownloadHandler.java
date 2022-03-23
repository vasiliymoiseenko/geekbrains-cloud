package ru.geekbrains.cloud.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.client.javafx.Controller;
import ru.geekbrains.cloud.common.messages.file.FileMessage;

@Log4j2
public class FileDownloadHandler implements ClientRequestHandler{

  private FileOutputStream fos;
  private Boolean append;

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg, Controller controller) {
    FileMessage fileMessage = (FileMessage) msg;

    try {
      if (fileMessage.partNumber == 1) {
        append = false;
        fos = new FileOutputStream("client_repository/" + fileMessage.filename, append);
      } else {
        append = true;
      }

      log.info(ctx.name() + "File " + fileMessage.filename + " part " + fileMessage.partNumber + " / " + fileMessage.partsCount + " received");
      fos.write(fileMessage.data);

      if (fileMessage.partNumber == fileMessage.partsCount) {
        fos.close();
        append = false;

        log.info(ctx.name() + "File " + fileMessage.filename + " is completely downloaded");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
