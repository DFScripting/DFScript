package io.github.techstreet.dfscript.util.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public enum ChatType {
    SUCCESS("§a§l» ", 'f', "<green><bold>» </bold></green><white>"),
    FAIL("§4§l» ", 'c', "<dark_red><bold>» </bold></dark_red><red>"),
    INFO_YELLOW("§6§l» ", 'e', "<gold><bold>» </bold></gold><yellow>"),
    INFO_BLUE("§9§l» ", 'b', "<blue><bold>» </bold></blue><aqua>");

    private final String prefix;
    private final char trailing;
    private final Component component;

    ChatType(String prefix, char trailing, String component) {
        this.prefix = prefix;
        this.trailing = trailing;
        this.component = MiniMessage.miniMessage().deserialize(component);
    }

    public String getString() {
        return this.prefix;
    }

    public char getTrailing() {
        return trailing;
    }

    public Component getComponent() {
        return component;
    }
}
