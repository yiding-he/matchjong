package com.github.yidinghe.matchjong.util;

import java.util.ArrayList;
import java.util.List;

public class EventBus {

  @FunctionalInterface
  public interface Handler<T> {

    void handle(T event) throws Exception;
  }

  private record HandlerWrapper<T>(Class<T> eventType, Handler<T> eventHandler) {

  }

  private static final List<HandlerWrapper<?>> HANDLER_WRAPPERS = new ArrayList<>();

  public static <E> void on(Class<E> eventType, Handler<E> handler) {
    HANDLER_WRAPPERS.add(new HandlerWrapper<>(eventType, handler));
  }

  @SuppressWarnings("unchecked")
  public static <E> void fire(E event) {
    HANDLER_WRAPPERS.stream()
      .filter(w -> w.eventType().isAssignableFrom(event.getClass()))
      .forEach(w -> {
        try {
          ((Handler<E>)w.eventHandler()).handle(event);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
  }
}
