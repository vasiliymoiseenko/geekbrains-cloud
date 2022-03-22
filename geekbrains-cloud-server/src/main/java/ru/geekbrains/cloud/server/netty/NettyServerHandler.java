package ru.geekbrains.cloud.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.server.handlers.ServerHandlerRegistry;
import ru.geekbrains.cloud.server.handlers.ServerRequestHandler;

@Log4j2
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    log.info("Client connected " + ctx.name());
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ServerRequestHandler handler = ServerHandlerRegistry.getHandler(msg.getClass());
    handler.handle(ctx, msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
