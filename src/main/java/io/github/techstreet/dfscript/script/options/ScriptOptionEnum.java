package io.github.techstreet.dfscript.script.options;

import io.github.techstreet.dfscript.script.Script;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.BiConsumer;

public enum ScriptOptionEnum {
    TEXT("Text Option", "A single option, no checks.", Items.BOOK, ScriptTextOption.class),
    INT("Integer Option", "A single option, must be an int.", Items.SLIME_BALL, ScriptIntOption.class),
    FLOAT("Floating-Point Option", "A single option, must be an int or a float.", Items.SLIME_BLOCK, ScriptFloatOption.class),
    KEY("Key Option", "A single option, acts as a key bind.", Items.STONE_BUTTON, ScriptKeyOption.class),
    BOOL("Boolean Option", "A true/false option. Returns either \"true\" or \"false\".", Items.LEVER, ScriptBoolOption.class);

    String name;
    String description;
    Item icon;
    Class<? extends ScriptOption> optionType;

    ScriptOptionEnum(String name, String description, Item icon, Class<? extends ScriptOption> optionType) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.optionType = optionType;
    }

    public ItemStack getIcon()
    {
        ItemStack item = new ItemStack(icon);

        item.setCustomName(new LiteralText(name).fillStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));

        NbtList lore = new NbtList();

        lore.add(NbtString.of(Text.Serializer.toJson(new LiteralText(description)
                .fillStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false))
        )));

        item.getSubNbt("display")
                .put("Lore", lore);

        return item;
    }

    public Class<? extends ScriptOption> getOptionType() {
        return optionType;
    }
}
