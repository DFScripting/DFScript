package io.github.techstreet.dfscript.script.argument;

import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.execution.ScriptContext;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.values.ScriptUnknownValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public record ScriptUnknownArgument() implements ScriptArgument {
    @Override
    public ScriptValue getValue(ScriptTask task) {
        return new ScriptUnknownValue();
    }

    @Override
    public boolean convertableTo(ScriptActionArgument.ScriptActionArgumentType type) {
        return false;
    }

    @Override
    public ItemStack getArgIcon() {
        return new ItemStack(Items.LIGHT_GRAY_DYE).setCustomName(Text.literal("Unknown").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));
    }

    @Override
    public String getArgText() {
        return "Unknown";
    }
}
