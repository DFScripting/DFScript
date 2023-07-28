package io.github.techstreet.dfscript.script.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.action.ScriptActionArgumentList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.lang.reflect.Type;

public class ScriptFunction extends ScriptHeader {

    public static final ItemStack functionIcon;

    static {
        functionIcon = new ItemStack(Items.LAPIS_BLOCK).setCustomName(Text.literal("Function").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));
    }
    private String name;
    private Item icon;

    private ScriptActionArgumentList argList;

    public ScriptFunction(String name, Item icon, ScriptActionArgumentList argList) {
        this.name = name;
        this.icon = icon;
        this.argList = argList;
    }

    public String getName() {
        return name;
    }

    public ItemStack getIcon() {
        ItemStack icon = new ItemStack(this.icon);

        icon.setCustomName(Text.literal(getName()).setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));

        NbtList lore = new NbtList();

        /*for (String descriptionLine: description) {
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal(descriptionLine)
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.GRAY)
                            .withItalic(false)))));
        }*/

        //lore.add(NbtString.of(Text.Serializer.toJson(Text.literal(""))));

        for (ScriptActionArgument arg : argList) {
            lore.add(NbtString.of(Text.Serializer.toJson(arg.text())));
        }

        icon.getSubNbt("display")
                .put("Lore", lore);

        return icon;
    }

    public Item getRawIcon() {
        return icon;
    }

    public void setName(String text) {
        this.name = text;
    }

    public void setIcon(Item newIcon) {
        icon = newIcon;
    }

    public static class Serializer implements JsonSerializer<ScriptFunction> {

        @Override
        public JsonElement serialize(ScriptFunction src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", "function");
            obj.addProperty("name", src.getName());
            obj.addProperty("icon", Registries.ITEM.getId(src.getRawIcon()).toString());
            obj.add("args", context.serialize(src.argList()));
            obj.add("snippet", context.serialize(src.container().getSnippet(0)));
            return obj;
        }
    }

    public int create(CScrollPanel panel, int y, int index, Script script) {
        panel.add(new CItem(5, y, functionIcon));
        panel.add(new CText(15, y + 2, Text.literal(getName())));

        return super.create(panel, y, index, script);
    }

    public ScriptActionArgumentList argList() {
        return argList;
    }
}
