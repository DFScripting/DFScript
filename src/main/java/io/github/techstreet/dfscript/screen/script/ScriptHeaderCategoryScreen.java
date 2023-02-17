package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.action.ScriptActionCategory;
import io.github.techstreet.dfscript.script.event.ScriptFunction;
import io.github.techstreet.dfscript.script.event.ScriptHeader;
import io.github.techstreet.dfscript.script.event.ScriptHeaderCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ScriptHeaderCategoryScreen extends CScreen {

    private static final int size;
    private static final List<HeaderExtra> extra;

    static {
        extra = List.of(
            new HeaderExtra(ScriptFunction.functionIcon, (script, insertIndex) -> script.getHeaders().add(insertIndex, new ScriptFunction(script.getUnnamedFunction(), Items.LAPIS_LAZULI)))
        );
        size = (int) (Math.ceil(Math.sqrt(ScriptHeaderCategory.values().length+extra.size())) * 10)+4;
    }

    private final Script script;

    public ScriptHeaderCategoryScreen(Script script, int insertIndex) {
        super(size, size);
        this.script = script;

        int x = 3;
        int y = 3;

        for (ScriptHeaderCategory category : ScriptHeaderCategory.values()) {
            CItem actionItem = new CItem(x, y, category.getIcon());
            widgets.add(actionItem);

            actionItem.setClickListener(btn -> DFScript.MC.setScreen(new ScriptAddHeaderScreen(script, insertIndex, category)));

            x += 10;
            if (x >= size - 10) {
                x = 3;
                y += 10;
            }
        }

        for(HeaderExtra headerExtra : extra) {
            CItem item = new CItem(x, y, headerExtra.icon());
            item.setClickListener(button -> {
                headerExtra.onClick().accept(script, insertIndex);
                close();
            });

            widgets.add(item);

            x += 10;
            if (x >= size - 10) {
                x = 3;
                y += 10;
            }
        }
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptEditScreen(script));
    }

    record HeaderExtra(ItemStack icon, BiConsumer<Script, Integer> onClick) {}
}