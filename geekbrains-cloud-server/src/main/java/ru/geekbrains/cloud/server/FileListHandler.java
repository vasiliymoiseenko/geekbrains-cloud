package ru.geekbrains.cloud.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import ru.geekbrains.cloud.common.FileInfo;

public class FileListHandler extends ChannelInboundHandlerAdapter {


  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("Client connected");
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buf = (ByteBuf) msg;

    if (buf.readableBytes() > 0) {

      if (buf.readByte() != (byte) 2) {
        ctx.fireChannelRead(msg);
      } else {
        StringBuilder listFiles = new StringBuilder();
        Files.list(Paths.get("server_repository")).map(FileInfo::new).forEach(p -> listFiles.append(p + " "));
        System.out.println(listFiles);
      }
    }

  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
