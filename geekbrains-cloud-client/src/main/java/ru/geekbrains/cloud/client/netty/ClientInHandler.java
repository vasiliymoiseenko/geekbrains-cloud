package ru.geekbrains.cloud.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ru.geekbrains.cloud.client.javafx.Controller;
import ru.geekbrains.cloud.common.type.DataType;
import ru.geekbrains.cloud.common.type.FileInfo;

public class ClientInHandler extends ChannelInboundHandlerAdapter {

  private int state = -1;
  private int reqLen = -1;
  private DataType type = DataType.EMPTY;
  private Controller controller;

  public ClientInHandler(Controller controller) {
    this.controller = controller;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buf = (ByteBuf) msg;

    if (buf.readableBytes() > 0) {

      if (state == -1) {
        byte firstByte = buf.readByte();
        type = DataType.getDataTypeFromByte(firstByte);
        state = 0;
        reqLen = 4;
        System.out.println(type);
      }

      if (type == DataType.COMMAND) {
        if (state == 0) {
          if (buf.readableBytes() < 4) {
            return;
          }
          reqLen = buf.readInt();
          state = 1;
          System.out.println("FileList size: " + reqLen);
        }

        if (state == 1) {
          if (buf.readableBytes() < reqLen) {
            return;
          }
          byte[] data = new byte[reqLen];
          buf.readBytes(data);
          String str = new String(data);

          List<FileInfo> fileInfoList = new ArrayList<>();
          String[] fileInfos = str.split("::");
          System.out.println(Arrays.toString(fileInfos));
          for (String s: fileInfos) {
            String[] info = s.split("\\$");
            System.out.println(Arrays.toString(info));
            fileInfoList.add(new FileInfo(info[0], info[1], Long.parseLong(info[2]), LocalDateTime.parse(info[3])));
          }
          controller.getRightPC().updateList(fileInfoList);

          System.out.println(type + " " + str);
          state = -1;
          reqLen = -1;
          type = DataType.EMPTY;
        }
      }
    }

    if (buf.readableBytes() == 0) {
      buf.release();
    }
  }


  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
