package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.options.ScriptNamedOption;
import io.github.techstreet.dfscript.script.options.ScriptOption;
import io.github.techstreet.dfscript.script.options.ScriptOptionEnum;
import io.github.techstreet.dfscript.util.chat.ChatUtil;

import java.util.ArrayList;

public class ScriptAddSettingScreen extends CScreen {
    private static final int size;

    static {
        size = (int) (Math.ceil(Math.sqrt(ScriptOptionEnum.values().length)) * 10)+4;
    }

    private final Script script;

    public ScriptAddSettingScreen(Script script, int pos) {
        super(size, size);
        this.script = script;

        int x = 3;
        int y = 3;

        for(ScriptOptionEnum option : ScriptOptionEnum.values()) {
            CItem citem = new CItem(x, y, option.getIcon());

            if(option.getExtraTypes() == 0) {
                citem.setClickListener((a) -> {
                    try {
                        script.addOption(pos, new ScriptNamedOption(ScriptOption.instantiate(option, new ArrayList<>()), script.getUnnamedOption()));
                    } catch (Exception e) {
                        ChatUtil.error(String.valueOf(e.getCause()));
                    }

                    DFScript.MC.setScreen(new ScriptSettingsScreen(script, true));
                });
            }
            else {
                citem.setClickListener((a) -> {
                    DFScript.MC.setScreen(new ScriptAddSettingSubtypeScreen(script, option, pos));
                });
            }

            widgets.add(citem);

            x += 10;
            if (x >= size - 10) {
                x = 3;
                y += 10;
            }
        }
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptSettingsScreen(script, true));
    }
}
