package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.action.ScriptActionCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class ScriptActionCategoryScreen extends CScreen {

    private static final int size;

    static {
        size = (int) (Math.ceil(Math.sqrt(ScriptActionCategory.values().length + 1)) * 10)+4;
    }

    private final Script script;

    public ScriptActionCategoryScreen(Script script, int insertIndex) {
        super(size, size);
        this.script = script;

        ItemStack eventsItem = new ItemStack(Items.DIAMOND);
        eventsItem.setCustomName(Text.literal("Events").fillStyle(Style.EMPTY.withItalic(false)));

        int x = 3;
        int y = 3;

        CItem item = new CItem(x, y, eventsItem);
        widgets.add(item);

        item.setClickListener(btn -> io.github.techstreet.dfscript.DFScript.MC.setScreen(new ScriptAddActionScreen(script, insertIndex, null)));

        x += 10;

        for (ScriptActionCategory category : ScriptActionCategory.values()) {
            CItem actionItem = new CItem(x, y, category.getIcon());
            widgets.add(actionItem);

            actionItem.setClickListener(btn -> io.github.techstreet.dfscript.DFScript.MC.setScreen(new ScriptAddActionScreen(script, insertIndex, category)));

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
}
