package io.github.techstreet.dfscript.script.event;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public enum ScriptHeaderCategory {

    EVENTS("Events", Items.DIAMOND);

    private final ItemStack icon;

    ScriptHeaderCategory(String name, Item icon) {
        this.icon = new ItemStack(icon);
        this.icon.setCustomName(Text.literal(name).fillStyle(Style.EMPTY.withItalic(false)));
    }

    public ItemStack getIcon() {
        return icon;
    }
}
