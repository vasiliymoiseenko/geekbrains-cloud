package ru.geekbrains.cloud.server.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import ru.geekbrains.cloud.common.messages.AbstractMessage;
import ru.geekbrains.cloud.common.messages.auth.AuthRequest;
import ru.geekbrains.cloud.common.messages.file.FileMessage;
import ru.geekbrains.cloud.common.messages.file.FileRequest;
import ru.geekbrains.cloud.common.messages.list.ListRequest;
import ru.geekbrains.cloud.common.messages.reg.RegRequest;
import ru.geekbrains.cloud.server.netty.NettyServer;

public class ServerHandlerRegistry {

  private static final Map<Class<? extends AbstractMessage>, ServerRequestHandler> REQUEST_HANDLER_MAP;

  static {
    Map<Class<? extends AbstractMessage>, ServerRequestHandler> requestHandlerMap = new HashMap<>();

    requestHandlerMap.put(ListRequest.class, new ListRequestHandler());
    requestHandlerMap.put(AuthRequest.class, new AuthHandler(NettyServer.getAuthService()));
    requestHandlerMap.put(RegRequest.class, new RegHandler(NettyServer.getAuthService()));
    requestHandlerMap.put(FileMessage.class, new FileUploadHandler());
    requestHandlerMap.put(FileRequest.class, new FileRequestHandler());

    REQUEST_HANDLER_MAP = Collections.unmodifiableMap(requestHandlerMap);
  }

  public static ServerRequestHandler getHandler(Class<?> messageClass) {
    return REQUEST_HANDLER_MAP.get(messageClass);
  }
}
