package io.github.techstreet.dfscript.script.action;

import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptSnippet;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;

public class ScriptActionCategoryExtra {
    private final ItemStack icon;
    private final TriConsumer<Script, ScriptSnippet, Integer> onClick;

    public ScriptActionCategoryExtra(ItemStack icon, TriConsumer<Script, ScriptSnippet, Integer> onClick) {
        this.icon = icon;
        this.onClick = onClick;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public ItemStack icon() {
        return icon;
    }

    public void click(Script script, ScriptSnippet snippet, int insertIndex) {
        onClick.accept(script, snippet, insertIndex);
    }
}
