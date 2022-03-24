package ru.geekbrains.cloud.client.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import javafx.application.Platform;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.client.javafx.Controller;
import ru.geekbrains.cloud.common.messages.file.FileMessage;

@Log4j2
public class FileService {

  public static void sendFile(Channel channel, File file, String path, Controller controller) {
    new Thread(() -> {
      try {
        int bufSize = 1024 * 1024 * 5;
        int partsCount = new Long(file.length() / bufSize).intValue();
        if (file.length() % bufSize != 0) {
          partsCount++;
        }

        FileMessage fileMessage = new FileMessage(file.getName(), path,-1, partsCount, new byte[bufSize]);
        FileInputStream in = new FileInputStream(file);
        Platform.runLater(() -> controller.setStatusProgressBar("Upload file: " + fileMessage.filename));

        for (int i = 0; i < partsCount; i++) {
          int readedBytes = in.read(fileMessage.data);
          fileMessage.partNumber = i + 1;
          if (readedBytes < bufSize) {
            fileMessage.data = Arrays.copyOfRange(fileMessage.data, 0, readedBytes);
          }
          ChannelFuture f = channel.writeAndFlush(fileMessage);
          f.sync();

          Platform.runLater(() -> controller.changeProgressBar((double) fileMessage.partNumber * ((double) 1 / fileMessage.partsCount)));

          log.info("File " + fileMessage.filename + " part " + (i + 1) + "/" + partsCount + " sent");
        }

        Platform.runLater(() -> controller.setStatusProgressBar("File " + fileMessage.filename + " is completely uploaded"));

        in.close();
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    }).start();
  }
}
