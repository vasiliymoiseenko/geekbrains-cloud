package ru.geekbrains.cloud.client.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import ru.geekbrains.cloud.common.messages.AbstractMessage;
import ru.geekbrains.cloud.common.messages.ListRequest;
import ru.geekbrains.cloud.common.messages.ListResponse;

public class ClientHandlerRegistry {

  private static final Map<Class<? extends AbstractMessage>, ClientRequestHandler> REQUEST_HANDLER_MAP;

  static {
    Map<Class<? extends AbstractMessage>, ClientRequestHandler> requestHandlerMap = new HashMap<>();
    requestHandlerMap.put(ListResponse.class, new ListResponseHandler());

    REQUEST_HANDLER_MAP = Collections.unmodifiableMap(requestHandlerMap);
  }

  public static ClientRequestHandler getHandler(Class<?> messageClass) {
    return REQUEST_HANDLER_MAP.get(messageClass);
  }
}
