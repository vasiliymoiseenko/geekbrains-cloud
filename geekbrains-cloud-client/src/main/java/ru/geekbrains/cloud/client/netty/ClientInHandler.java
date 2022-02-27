package ru.geekbrains.cloud.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
import ru.geekbrains.cloud.client.javafx.Controller;
import ru.geekbrains.cloud.common.type.DataType;
import ru.geekbrains.cloud.common.type.FileInfo;

public class ClientInHandler extends ChannelInboundHandlerAdapter {

  private DataType type = DataType.EMPTY;

  public enum State {
    IDLE, NAME_LENGTH, NAME, FILE_LENGTH, FILE
  }

  private State currentState = State.IDLE;
  private int nextLength;
  private long fileLength;
  private long receivedFileLength;
  private BufferedOutputStream out;

  private Controller controller;

  public ClientInHandler(Controller controller) {
    this.controller = controller;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buf = (ByteBuf) msg;

    if (buf.readableBytes() > 0) {

      if (currentState == State.IDLE) {
        byte firstByte = buf.readByte();
        type = DataType.getDataTypeFromByte(firstByte);
        currentState = State.NAME_LENGTH;
        System.out.println(type);
      }

      if (type == DataType.UPDATE_FILELIST) {
        if (currentState == State.NAME_LENGTH) {
          if (buf.readableBytes() < 4) {
            return;
          }
          nextLength = buf.readInt();
          currentState = State.NAME;
          System.out.println("FileList size: " + nextLength);
        }

        if (currentState == State.NAME) {
          if (buf.readableBytes() < nextLength) {
            return;
          }
          byte[] data = new byte[nextLength];
          buf.readBytes(data);
          String str = new String(data);

          List<FileInfo> fileInfoList = new ArrayList<>();
          String[] fileInfos = str.split("::");
          for (String s : fileInfos) {
            String[] info = s.split("\\$");
            if (info.length == 4) {
              fileInfoList.add(new FileInfo(info[0], info[1], Long.parseLong(info[2]), LocalDateTime.parse(info[3])));
            }
          }
          Platform.runLater(() -> controller.getRightPC().updateList(fileInfoList));

          System.out.println("FileList updated");

          currentState = State.IDLE;
          type = DataType.EMPTY;
        }
      } else if (type == DataType.FILE) {
        downloadFile(ctx, buf);
      }
    }

    if (buf.readableBytes() == 0) {
      buf.release();
    }
  }

  private void downloadFile(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
    if (currentState == State.NAME_LENGTH) {
      if (buf.readableBytes() >= 4) {
        System.out.println("Download file: Get filename length");
        nextLength = buf.readInt();
        currentState = State.NAME;
      }
    }

    if (currentState == State.NAME) {
      if (buf.readableBytes() >= nextLength) {
        byte[] fileName = new byte[nextLength];
        buf.readBytes(fileName);
        System.out.println("Download file: Filename received - " + new String(fileName, "UTF-8"));
        Path path = Paths.get("client_repository").resolve(new String(fileName));
        out = new BufferedOutputStream(new FileOutputStream(path.toString()));
        currentState = State.FILE_LENGTH;
      }
    }

    if (currentState == State.FILE_LENGTH) {
      if (buf.readableBytes() >= 8) {
        fileLength = buf.readLong();
        System.out.println("Download file: File length received - " + fileLength);
        currentState = State.FILE;
      }
    }

    if (currentState == State.FILE) {
      while (buf.readableBytes() > 0) {
        out.write(buf.readByte());
        receivedFileLength++;
        if (fileLength == receivedFileLength) {
          currentState = State.IDLE;
          System.out.println("File downloaded");
          out.close();
          Platform.runLater(() -> controller.getLeftPC().updateList());
          break;
        }
      }
    }
  }


  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
