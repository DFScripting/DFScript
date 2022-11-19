package io.github.techstreet.dfscript.script.action;

import io.github.techstreet.dfscript.script.ScriptComment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.slf4j.helpers.FormattingTuple;

import java.util.ArrayList;
import java.util.List;

public enum ScriptActionCategory {

    VISUALS("Visuals", Items.ENDER_EYE),
    ACTIONS("Actions", Items.PLAYER_HEAD),
    MISC("Misc", Items.COMPASS, List.of(
            new ScriptActionCategoryExtra(new ItemStack(Items.MAP).setCustomName(Text.literal("Comment").setStyle(Style.EMPTY.withItalic(false))), (Void) -> new ScriptComment(""))
    )),
    VARIABLES("Variables", Items.IRON_INGOT),
    NUMBERS("Numbers", Items.SLIME_BALL),
    LISTS("Lists", Items.BOOKSHELF),
    TEXTS("Texts", Items.BOOK),
    DICTIONARIES("Dictionaries", Items.ENDER_CHEST),

    MENUS("Menus", Items.PAINTING),
    ;

    private final ItemStack icon;

    private List<ScriptActionCategoryExtra> extras = new ArrayList<>();

    ScriptActionCategory(String name, Item icon) {
        this.icon = new ItemStack(icon);
        this.icon.setCustomName(Text.literal(name).fillStyle(Style.EMPTY.withItalic(false)));
    }

    ScriptActionCategory(String name, Item icon, List<ScriptActionCategoryExtra> extras) {
        this.icon = new ItemStack(icon);
        this.icon.setCustomName(Text.literal(name).fillStyle(Style.EMPTY.withItalic(false)));
        this.extras = extras;
    }

    public ItemStack getIcon() {
        return icon;
    }
    public List<ScriptActionCategoryExtra> getExtras()
    {
        return extras;
    }
}
