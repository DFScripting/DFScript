package io.github.techstreet.dfscript.script.event;

import io.github.techstreet.dfscript.script.ScriptComment;
import io.github.techstreet.dfscript.script.action.ScriptActionCategoryExtra;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public enum ScriptHeaderCategory {

    EVENTS("Events", Items.DIAMOND)
    ;

    private final ItemStack icon;

    ScriptHeaderCategory(String name, Item icon) {
        this.icon = new ItemStack(icon);
        this.icon.setCustomName(Text.literal(name).fillStyle(Style.EMPTY.withItalic(false)));
    }

    public ItemStack getIcon() {
        return icon;
    }
}
