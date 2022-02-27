package ru.geekbrains.cloud.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import ru.geekbrains.cloud.common.DataType;
import ru.geekbrains.cloud.common.FileInfo;

public class ServerInHandler extends ChannelInboundHandlerAdapter {

  private int state = -1;
  private int reqLen = -1;
  private DataType type = DataType.EMPTY;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("Client connected");
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
        StringBuilder listFiles = new StringBuilder();
        Files.list(Paths.get("server_repository")).map(FileInfo::new).forEach(p -> listFiles.append(p).append("::"));
        System.out.println(listFiles);

        ByteBuf outBuff = null;
        outBuff = ByteBufAllocator.DEFAULT.directBuffer(1);
        outBuff.writeByte((byte) 16);
        ctx.write(outBuff);

        byte[] fileListBytes = listFiles.toString().getBytes(StandardCharsets.UTF_8);
        outBuff = ByteBufAllocator.DEFAULT.directBuffer(4);
        outBuff.writeInt(fileListBytes.length);
        ctx.write(outBuff);

        outBuff = ByteBufAllocator.DEFAULT.directBuffer(fileListBytes.length);
        outBuff.writeBytes(fileListBytes);
        ctx.writeAndFlush(outBuff);

        state = -1;
        reqLen = 1;
        type = DataType.EMPTY;
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
