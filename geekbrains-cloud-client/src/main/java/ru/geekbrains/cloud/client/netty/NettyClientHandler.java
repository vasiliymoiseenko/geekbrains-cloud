package ru.geekbrains.cloud.client.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.geekbrains.cloud.client.handlers.ClientHandlerRegistry;
import ru.geekbrains.cloud.client.handlers.ClientRequestHandler;
import ru.geekbrains.cloud.client.javafx.Controller;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

  private final Controller controller;

  public NettyClientHandler(Controller controller) {
    this.controller = controller;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ClientRequestHandler handler = ClientHandlerRegistry.getHandler(msg.getClass());
    handler.handle(ctx, msg, controller);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
