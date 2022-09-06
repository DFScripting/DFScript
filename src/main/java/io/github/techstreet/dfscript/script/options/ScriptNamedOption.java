package io.github.techstreet.dfscript.script.options;

import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class ScriptNamedOption {
    String name;
    ScriptOption option;

    public ScriptNamedOption(ScriptOption option, String name) {
        this.name = name;
        this.option = option;
    }

    public String getFullName() {
        return getName() + " (" + option.getName() + ")";
    }

    public String getName() {
        return name;
    }

    public ScriptArgument getValue() {
        return option.getValue();
    }

    public int create(CScrollPanel panel, int x, int y) {
        return option.create(panel, x, y, 105);
    }

    public void setName(String text) {
        name = text;
    }

    public ItemStack getIcon() {
        return new ItemStack(option.getIcon()).setCustomName(Text.literal(getName()).fillStyle(Style.EMPTY.withItalic(false)));
    }
}
