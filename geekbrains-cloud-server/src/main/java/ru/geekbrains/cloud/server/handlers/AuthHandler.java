package ru.geekbrains.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.common.messages.auth.AuthErrorResponse;
import ru.geekbrains.cloud.common.messages.auth.AuthRequest;
import ru.geekbrains.cloud.common.messages.auth.AuthSuccessResponse;
import ru.geekbrains.cloud.common.messages.list.ListResponse;
import ru.geekbrains.cloud.common.messages.reg.RegRequest;
import ru.geekbrains.cloud.server.db.AuthService;

@Log4j2
@AllArgsConstructor
public class AuthHandler implements ServerRequestHandler {

  private AuthService authService;

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg) {
    AuthRequest authRequest = (AuthRequest) msg;

    String login = authRequest.getLogin();
    String password = authRequest.getPassword();

    if (authService.authUser(login, password)) {
      ctx.writeAndFlush(new AuthSuccessResponse(login)).addListener(channelFuture -> {
        if (channelFuture.isSuccess()) {
          log.info("AuthSuccesResponse sended: " + login);
        }
      });
    } else {
      String reason = "Invalid login/password";

      ctx.writeAndFlush(new AuthErrorResponse(reason)).addListener(channelFuture -> {
        if (channelFuture.isSuccess()) {
          log.info("AuthErrorResponse sended: " + reason);
        }
      });
    }
  }
}
