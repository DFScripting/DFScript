package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.screen.widget.CWidget;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.options.ScriptOption;

import java.util.ArrayList;
import java.util.List;

public class ScriptSettingsScreen extends CScreen {
    private final Script script;

    private static int scroll = 0;

    private final CScrollPanel panel;

    private final List<CWidget> contextMenu = new ArrayList<>();

    public ScriptSettingsScreen(Script script) {
        super(125, 100);
        this.script = script;
        panel = new CScrollPanel(0, 3, 120, 94);

        widgets.add(panel);

        CTextField description = new CTextField(script.getDescription(), 3, 3, 115, 20, true);
        description.setChangedListener(() -> script.setDescription(description.getText()));
        panel.add(description);

        int y = 25;
        int index = 0;

        for(ScriptOption option : script.getOptions())
        {
            y = option.create(panel, 5, y, 105);

            index++;
        }

        CButton add = new CButton(37, y, 46, 8, "Add", () -> {
            DFScript.MC.setScreen(new ScriptAddSettingScreen(script, script.getOptions().size()));
        });

        panel.add(add);

        panel.setScroll(scroll);
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptEditScreen(script));
    }
}
