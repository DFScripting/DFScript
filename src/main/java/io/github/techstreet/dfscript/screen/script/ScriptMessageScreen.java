package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class ScriptMessageScreen extends CScreen {

    private final Screen parent;

    public ScriptMessageScreen(Screen parent, String message) {
        super(106, 60);
        this.parent = parent;

        int counter = 0;
        int y = 20;

        StringBuilder words = new StringBuilder();
        for (Character c : message.toCharArray()) {
            counter += 1;
            words.append(c);

            if (c.toString().equals(" ") && counter >= 25) {
                int width = DFScript.MC.textRenderer.getWidth(words.toString());
                int x = (106 - width);

                CText text = new CText(10, y, Text.of(words.toString()));
                System.out.println(x + ":" + y + " == " + words);
                widgets.add(text);

                y += 6;
                words = new StringBuilder("");
                counter = 0;
            }
        }

        widgets.add(new CButton(33, 42, 40, 10, "Ok", () -> {
            DFScript.MC.setScreen(parent);
        }));
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(parent);
    }
}
