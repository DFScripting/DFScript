package io.github.techstreet.dfscript.util.chat;

import io.github.techstreet.dfscript.DFScript;
import java.awt.Color;
import java.util.Objects;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.title.Title;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.*;
import org.jetbrains.annotations.NotNull;

public class ChatUtil {
    public static class PlayerAudience implements Audience {
        final private ClientPlayerEntity player = DFScript.MC.player;

        private boolean assertPlayer() {
            return player != null;
        }

        @Override
        public void sendMessage(final @NotNull Component message) {
            if (assertPlayer()) {
                player.sendMessage(message);
            }
        }

        @Override
        public void sendActionBar(final @NotNull Component message) {
            if (assertPlayer()) {
                player.sendActionBar(message);
            }
        }

        public void sendTitle(final @NotNull Component title, final @NotNull Component subtitle) {
            if (assertPlayer()) {
                player.showTitle(Title.title(title, subtitle));
            }
        }
    }
    private static final PlayerAudience audience = new PlayerAudience();

    public static void playSound(SoundEvent sound) {
        playSound(sound, 1F);
    }

    public static void playSound(SoundEvent sound, float pitch) {
        playSound(sound, 2F, pitch);
    }

    public static void playSound(SoundEvent sound, float pitch, float volume) {
        if (sound != null && DFScript.MC.player != null) {
            DFScript.MC.player.playSound(sound, volume, pitch);
        }
    }

    public static void chat(String message) {
        Objects.requireNonNull(DFScript.MC.getNetworkHandler()).sendChatMessage(message);
    }

    public static void executeCommand(String command) {
        chat("/" + command.replaceFirst("^/", ""));
    }

    public static void executeCommandSilently(String command, int messageAmount) {
        executeCommand(command);
        MessageGrabber.hide(messageAmount);
    }

    public static void executeCommandSilently(String command) {
        executeCommandSilently(command, 1);
    }

    public static void sendMessage(String text) {
        sendMessage(Text.literal(text), null);
    }

    public static void sendMessage(Text text) {
        sendMessage(text, null);
    }

    public static void sendMessage(Component component) {
        audience.sendMessage(component);
    }

    public static void sendMessage(String text, ChatType prefixType) {
        sendMessage(Text.literal(text), prefixType);
    }

    public static void sendMessage(Text text, ChatType prefixType) {
        if (DFScript.MC.player == null) return;
        String prefix = "";
        if (prefixType != null) {
            prefix = prefixType.getString();
        }
        DFScript.MC.player.sendMessage(Text.literal(prefix).append(text), false);
    }

    public static void sendMessage(Component component, ChatType prefixType) {
        if (DFScript.MC.player == null) return;
        Component prefix = Component.empty();
        if (prefixType != null) {
            prefix = prefixType.getComponent();
        }
        audience.sendMessage(prefix.append(component));
    }

    public static MutableText setColor(MutableText component, Color color) {
        Style colorStyle = component.getStyle().withColor(TextColor.fromRgb(color.getRGB()));
        component.setStyle(colorStyle);
        return component;
    }

    public static void sendActionBar(Text msg) {
        if (DFScript.MC.player == null) return;
        DFScript.MC.player.sendMessage(msg, true);
    }

    public static void sendActionBar(Component component) {
        if (DFScript.MC.player == null) return;
        audience.sendActionBar(component);
    }

    public static void sendTitle(Component title, Component subtitle) {
        if (DFScript.MC.player == null) return;
        audience.sendTitle(title, subtitle);
    }

    public static void error(String s) {
        sendMessage(s, ChatType.FAIL);
    }

    public static void info(String s) {
        sendMessage(s, ChatType.INFO_BLUE);
    }
}
