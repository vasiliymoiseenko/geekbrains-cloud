package ru.geekbrains.cloud.server.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.common.constants.Const;
import ru.geekbrains.cloud.common.messages.file.FileErrorResponse;
import ru.geekbrains.cloud.common.messages.file.FileMessage;
import ru.geekbrains.cloud.common.messages.list.FileInfo;
import ru.geekbrains.cloud.common.messages.list.ListErrorResponse;
import ru.geekbrains.cloud.common.messages.list.ListResponse;

@Log4j2
public class ClientService {

  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

  @Getter
  @Setter
  private boolean authorized = false;

  public void sendFile(Channel channel, File file, String path) {
    executorService.execute(() -> {

      int bufSize = 1024 * 1024 * 5;
      int partsCount = new Long(file.length() / bufSize).intValue();
      if (file.length() % bufSize != 0) {
        partsCount++;
      }

      FileMessage fileMessage = new FileMessage(file.getName(), path, -1, partsCount, new byte[bufSize]);

      try (FileInputStream in = new FileInputStream(file)) {
        for (int i = 0; i < partsCount; i++) {
          int readBytes = in.read(fileMessage.data);
          fileMessage.partNumber = i + 1;

          if (readBytes < bufSize) {
            fileMessage.data = Arrays.copyOfRange(fileMessage.data, 0, readBytes);
          }

          ChannelFuture f = channel.writeAndFlush(fileMessage);
          f.sync();
          
          log.info("File " + fileMessage.filename + " part " + (i + 1) + "/" + partsCount + " sent");
        }
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    });
  }

  public void sendList(ChannelHandlerContext ctx, String path) {
    Path fullPath = Paths.get(Const.SERVER_REP, path);
    try {
      List<FileInfo> list = Files.list(fullPath).map(FileInfo::new).collect(Collectors.toList());
      ctx.writeAndFlush(new ListResponse(list, path)).addListener(channelFuture -> {
        if (channelFuture.isSuccess()) {
          log.info(ctx.name() + "- List sent: " + fullPath);
        }
      });
    } catch (IOException e) {
      String reason = e.toString();
      ctx.writeAndFlush(new FileErrorResponse(reason)).addListener(channelFuture -> {
        if (channelFuture.isSuccess()) {
          log.info(ctx.name() + "- ListErrorResponse sent: " + reason);
        }
      });
    }
  }
}
