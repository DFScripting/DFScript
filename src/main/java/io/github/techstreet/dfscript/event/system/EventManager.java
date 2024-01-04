package io.github.techstreet.dfscript.event.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class EventManager {

    private static EventManager instance;
    private int events = 0;
    private int eventLimit = 100;
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
            if (events == eventLimit) return;
            events++;
            consumer.accept(event);
        }
    }

    public void resetEvents() {
        this.events = 0;
    }

    public void setEventLimit(int eventLimit) {
        this.eventLimit = eventLimit;
    }
}
