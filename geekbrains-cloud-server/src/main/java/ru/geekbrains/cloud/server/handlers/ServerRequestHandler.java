package ru.geekbrains.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;

public interface ServerRequestHandler {

  void handle(ChannelHandlerContext ctx, Object msg);

}
