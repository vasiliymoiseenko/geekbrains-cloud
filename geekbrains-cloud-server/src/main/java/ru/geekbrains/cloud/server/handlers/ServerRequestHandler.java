package ru.geekbrains.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import ru.geekbrains.cloud.server.service.ClientService;

public interface ServerRequestHandler {

  void handle(ChannelHandlerContext ctx, Object msg, ClientService clientService);

}
