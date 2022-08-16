package io.github.techstreet.dfscript.event.system;

import io.github.techstreet.dfscript.event.TickEvent;
import io.github.techstreet.dfscript.util.chat.ChatType;
import io.github.techstreet.dfscript.util.chat.ChatUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class EventManager {

    private static EventManager instance;
    private final HashMap<Class<? extends Event>, List<Consumer<Event>>> listeners = new HashMap<>();

    public EventManager() {
        instance = this;
    }

    public static EventManager getInstance() {
        if (instance == null) {
            new EventManager();
        }

        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void register(Class<T> type, Consumer<T> consumer) {
        listeners
            .computeIfAbsent(type, k -> new ArrayList<>())
            .add((Consumer<Event>) consumer);
    }

    public void dispatch(Event event) {
        for (Consumer<Event> consumer : listeners.getOrDefault(event.getClass(), new ArrayList<>())) {
            consumer.accept(event);
        }
    }
}
