package ru.geekbrains.cloud.client.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import ru.geekbrains.cloud.common.messages.abs.AbstractMessage;
import ru.geekbrains.cloud.common.messages.auth.AuthErrorResponse;
import ru.geekbrains.cloud.common.messages.auth.AuthSuccessResponse;
import ru.geekbrains.cloud.common.messages.file.FileMessage;
import ru.geekbrains.cloud.common.messages.list.ListResponse;
import ru.geekbrains.cloud.common.messages.reg.RegErrorResponse;
import ru.geekbrains.cloud.common.messages.reg.RegSuccessResponse;

public class ClientHandlerRegistry {

  private static final Map<Class<? extends AbstractMessage>, ClientRequestHandler> REQUEST_HANDLER_MAP;

  static {
    Map<Class<? extends AbstractMessage>, ClientRequestHandler> requestHandlerMap = new HashMap<>();
    requestHandlerMap.put(ListResponse.class, new ListResponseHandler());
    requestHandlerMap.put(RegErrorResponse.class, new RegErrorResponseHandler());
    requestHandlerMap.put(RegSuccessResponse.class, new RegSuccessResponseHandler());
    requestHandlerMap.put(AuthErrorResponse.class, new AuthErrorResponseHandler());
    requestHandlerMap.put(AuthSuccessResponse.class, new AuthSuccessResponseHandler());
    requestHandlerMap.put(FileMessage.class, new FileDownloadHandler());

    REQUEST_HANDLER_MAP = Collections.unmodifiableMap(requestHandlerMap);
  }

  public static ClientRequestHandler getHandler(Class<?> messageClass) {
    return REQUEST_HANDLER_MAP.get(messageClass);
  }
}
