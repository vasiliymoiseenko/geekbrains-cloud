package ru.geekbrains.cloud.server.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.server.handlers.ServerHandlerRegistry;
import ru.geekbrains.cloud.server.handlers.ServerRequestHandler;
import ru.geekbrains.cloud.server.service.ClientService;

@Log4j2
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

  private final ClientService clientService = new ClientService();

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    log.info("Client connected: " + ctx.name());
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ServerRequestHandler handler = ServerHandlerRegistry.getHandler(msg.getClass());
    handler.handle(ctx, msg, clientService);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
