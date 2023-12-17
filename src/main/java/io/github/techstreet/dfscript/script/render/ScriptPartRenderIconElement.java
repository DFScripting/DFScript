package io.github.techstreet.dfscript.script.render;

import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.event.ScriptHeader;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ScriptPartRenderIconElement implements ScriptPartRenderElement {
    private String name;
    private ItemStack icon;

    public ScriptPartRenderIconElement(String name, ItemStack icon) {
        this.name = name;
        this.icon = icon;
    }

    @Override
    public int render(CScrollPanel panel, int y, int indent, Script script, ScriptHeader header) {
        panel.add(new CItem(5 + indent * 5, y, icon));
        panel.add(new CText(15 + indent * 5, y + 2, Text.literal(name)));

        return y + 10;
    }
}
