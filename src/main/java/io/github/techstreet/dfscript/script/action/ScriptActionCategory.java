package io.github.techstreet.dfscript.script.action;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.LiteralText;

public enum ScriptActionCategory {

    VISUALS("Visuals", Items.ENDER_EYE),
    ACTIONS("Actions", Items.PLAYER_HEAD),
    MISC("Misc", Items.COMPASS),
    VARIABLES("Variables", Items.IRON_INGOT),
    NUMBERS("Numbers", Items.SLIME_BALL),
    LISTS("Lists", Items.BOOKSHELF),
    TEXTS("Texts", Items.BOOK),
    DICTIONARIES("Dictionaries", Items.ENDER_CHEST),

    MENUS("Menus", Items.PAINTING),
    ;

    private final ItemStack icon;

    ScriptActionCategory(String name, Item icon) {
        this.icon = new ItemStack(icon);
        this.icon.setCustomName(((LiteralText) Text.of(name)).fillStyle(Style.EMPTY.withItalic(false)));
    }

    public ItemStack getIcon() {
        return icon;
    }
}
