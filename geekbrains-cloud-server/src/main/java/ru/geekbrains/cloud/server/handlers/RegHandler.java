package ru.geekbrains.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import java.sql.SQLException;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import ru.geekbrains.cloud.common.messages.list.ListResponse;
import ru.geekbrains.cloud.common.messages.reg.RegErrorResponse;
import ru.geekbrains.cloud.common.messages.reg.RegRequest;
import ru.geekbrains.cloud.common.messages.reg.RegSuccessResponse;
import ru.geekbrains.cloud.server.db.AuthService;

@Log4j2
@AllArgsConstructor
public class RegHandler implements ServerRequestHandler {

  private AuthService authService;

  @Override
  public void handle(ChannelHandlerContext ctx, Object msg) {
    RegRequest regRequest = (RegRequest) msg;

    String login = regRequest.getLogin();
    String password = regRequest.getPassword();
    int capacity = regRequest.getCapacity();

    try {
      authService.insertUser(login, password, capacity);

      ctx.writeAndFlush(new RegSuccessResponse()).addListener(channelFuture -> {
        if (channelFuture.isSuccess()) {
          log.info("RegSuccessResponse sended: User " + regRequest.getLogin() + " registered");
        }
      });
    } catch (SQLException e) {
      String reason;

      if (e.toString().contains("UNIQUE constraint failed")) {
        reason = "Login " + regRequest.getLogin() + " is already in use";
      } else {
        reason = "Registration failed";
      }

      ctx.writeAndFlush(new RegErrorResponse(reason)).addListener(channelFuture -> {
        if (channelFuture.isSuccess()) {
          log.info("RegErrorResponse sended: " + reason);
        }
      });
    }
  }
}
