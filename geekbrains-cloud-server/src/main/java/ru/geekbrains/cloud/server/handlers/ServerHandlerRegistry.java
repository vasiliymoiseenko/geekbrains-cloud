package ru.geekbrains.cloud.server.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import ru.geekbrains.cloud.common.messages.AbstractMessage;
import ru.geekbrains.cloud.common.messages.ListRequest;

public class ServerHandlerRegistry {

  private static final Map<Class<? extends AbstractMessage>, ServerRequestHandler> REQUEST_HANDLER_MAP;

  static {
    Map<Class<? extends AbstractMessage>, ServerRequestHandler> requestHandlerMap = new HashMap<>();
    requestHandlerMap.put(ListRequest.class, new ListRequestHandler());

    REQUEST_HANDLER_MAP = Collections.unmodifiableMap(requestHandlerMap);
  }

  public static ServerRequestHandler getHandler(Class<?> messageClass) {
    return REQUEST_HANDLER_MAP.get(messageClass);
  }
}
