package io.github.techstreet.dfscript.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ToasterUtil {

    public static void sendToaster(String title, String description, SystemToast.Type type) {
        sendToaster(Text.literal(title), Text.literal(description), type);
    }

    public static void sendTranslateToaster(String titleIdentifier, String descIdentifier, SystemToast.Type type) {
        sendToaster(Text.translatable(titleIdentifier), Text.translatable(descIdentifier), type);
    }

    public static void sendToaster(MutableText title, MutableText description, SystemToast.Type type) {
        MinecraftClient.getInstance().getToastManager().add(new SystemToast(type, title, description));
    }

}
