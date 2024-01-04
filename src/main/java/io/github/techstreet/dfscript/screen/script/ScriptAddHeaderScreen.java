package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.event.ScriptEvent;
import io.github.techstreet.dfscript.script.event.ScriptEventType;
import io.github.techstreet.dfscript.script.event.ScriptHeaderCategory;

public class ScriptAddHeaderScreen extends CScreen {

    private final Script script;
    private final int insertIndex;

    public ScriptAddHeaderScreen(Script script, int insertIndex, ScriptHeaderCategory category) {
        super(size(category), size(category));
        int size = size(category);
        this.script = script;
        this.insertIndex = insertIndex;

        int x = 3;
        int y = 3;

        for (ScriptEventType type : ScriptEventType.values()) {
            //if (type.getCategory() != category) continue;

            CItem item = new CItem(x, y, type.getIcon());
            item.setClickListener((btn) -> {
                ScriptEvent event = new ScriptEvent(type);
                script.getHeaders().add(insertIndex, event);
                DFScript.MC.setScreen(new ScriptEditScreen(script));
            });
            widgets.add(item);
            x += 10;
            if (x >= size - 10) {
                x = 3;
                y += 10;
            }
        }
    }

    private static int size(ScriptHeaderCategory category) {
        int amount = 0;
        for (ScriptEventType type : ScriptEventType.values()) {
            //if (type.getCategory() != category) continue;

            amount++;
        }
        return (int) (Math.ceil(Math.sqrt(amount)) * 10) + 4;
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptHeaderCategoryScreen(script, insertIndex));
    }
}
