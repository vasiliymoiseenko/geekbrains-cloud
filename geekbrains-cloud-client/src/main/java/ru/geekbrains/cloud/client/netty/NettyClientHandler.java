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
import java.util.List;
import javafx.application.Platform;
import ru.geekbrains.cloud.client.handlers.ClientHandlerRegistry;
import ru.geekbrains.cloud.client.handlers.ClientRequestHandler;
import ru.geekbrains.cloud.client.javafx.Controller;
import ru.geekbrains.cloud.common.type.FileInfo;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

  private Controller controller;

  public NettyClientHandler(Controller controller) {
    this.controller = controller;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ClientRequestHandler handler = ClientHandlerRegistry.getHandler(msg.getClass());
    handler.handle(ctx, msg, controller);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
