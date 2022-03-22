package ru.geekbrains.cloud.client.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.geekbrains.cloud.client.handlers.ClientHandlerRegistry;
import ru.geekbrains.cloud.client.handlers.ClientRequestHandler;
import ru.geekbrains.cloud.client.javafx.Controller;

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
