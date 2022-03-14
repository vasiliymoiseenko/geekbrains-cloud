package ru.geekbrains.cloud.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import ru.geekbrains.cloud.client.javafx.Controller;

public interface ClientRequestHandler {

  void handle(ChannelHandlerContext ctx, Object msg, Controller controller);

}
