package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptParametrizedPart;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.action.ScriptAction;
import io.github.techstreet.dfscript.script.argument.ScriptClientValueArgument;
import io.github.techstreet.dfscript.script.event.ScriptHeader;

public class ScriptAddClientValueScreen extends CScreen {

    private final Script script;

    private final ScriptHeader header;
    private final ScriptParametrizedPart action;
    private final int insertIndex;
    private static final int WIDTH = 58;

    public ScriptAddClientValueScreen(ScriptParametrizedPart action, Script script, int insertIndex, ScriptHeader header, String overwrite) {
        super(WIDTH, 58);
        this.script = script;
        this.action = action;
        this.header = header;
        this.insertIndex = insertIndex;

        int x = 5;
        int y = 5;
        for (ScriptClientValueArgument arg : ScriptClientValueArgument.values()) {
            CItem item = new CItem(x, y, arg.getIcon());
            item.setClickListener((btn) -> {
                if(overwrite != null) action.getArguments().remove(insertIndex);
                action.getArguments().add(insertIndex, arg);
                DFScript.MC.setScreen(new ScriptEditPartScreen(action, script, header));
            });
            widgets.add(item);
            x += 10;
            if (x > WIDTH-10) {
                x = 5;
                y += 10;
            }
        }
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptAddArgumentScreen(script, action, insertIndex, header));
    }
}
