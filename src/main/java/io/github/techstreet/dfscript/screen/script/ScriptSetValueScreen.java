package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptParametrizedPart;
import io.github.techstreet.dfscript.script.argument.ScriptNumberArgument;
import io.github.techstreet.dfscript.script.argument.ScriptTextArgument;
import io.github.techstreet.dfscript.script.argument.ScriptVariableArgument;
import io.github.techstreet.dfscript.script.argument.ScriptVariableScope;
import io.github.techstreet.dfscript.script.event.ScriptFunction;
import io.github.techstreet.dfscript.script.event.ScriptHeader;
import io.github.techstreet.dfscript.script.values.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ScriptSetValueScreen extends CScreen {

    private final Script script;

    private final Consumer<ScriptValue> onSave;

    private final Runnable onClose;

    private final Class<? extends ScriptValue> type;

    public ScriptSetValueScreen(Script script, Runnable onClose, Consumer<ScriptValue> onSave, Class<? extends ScriptValue> type) {
        this(script,onClose,onSave, type,null);
    }
    public ScriptSetValueScreen(Script script, Runnable onClose, Consumer<ScriptValue> onSave, Class<? extends ScriptValue> type, String overwrite) {
        super(100, 50);
        this.script = script;
        this.onSave = onSave;
        this.onClose = onClose;
        this.type = type;

        CTextField input = new CTextField("Input", 2, 2, 96, 35, true);
        if(overwrite != null) input.setText(overwrite);

        ItemStack unknownIcon = new ItemStack(Items.LIGHT_GRAY_DYE);
        unknownIcon.setCustomName(Text.literal("Unknown")
                .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack textIcon = new ItemStack(Items.BOOK);
        textIcon.setCustomName(Text.literal("Text")
            .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack numberIcon = new ItemStack(Items.SLIME_BALL);
        numberIcon.setCustomName(Text.literal("Number")
            .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack trueIcon = new ItemStack(Items.LIME_DYE);
        trueIcon.setCustomName(Text.literal("True")
                .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack falseIcon = new ItemStack(Items.RED_DYE);
        falseIcon.setCustomName(Text.literal("False")
                .fillStyle(Style.EMPTY.withItalic(false)));

        CItem addNumber;
        CItem addText;
        CItem addTrue;
        CItem addFalse;
        CItem addUnknown;

        int x = 2;

        if(type == ScriptValue.class || type == ScriptNumberValue.class) {
            addNumber = new CItem(x, 40, numberIcon);
            x += 10;

            addNumber.setClickListener((btn) -> {
                try {
                    double number = Double.parseDouble(input.getText());
                    onSave.accept(new ScriptNumberValue(number));
                    close();
                } catch (Exception err) {
                    input.textColor = 0xFF3333;
                }
            });

            widgets.add(addNumber);
        }

        if(type == ScriptValue.class || type == ScriptTextValue.class) {
            addText = new CItem(x, 40, textIcon);
            x += 10;

            addText.setClickListener((btn) -> {
                onSave.accept(new ScriptTextValue(input.getText()));
                close();
            });

            widgets.add(addText);
        }

        if(type == ScriptValue.class || type == ScriptBoolValue.class) {
            addTrue = new CItem(x, 40, trueIcon);
            x += 10;
            addFalse = new CItem(x, 40, falseIcon);
            x += 10;

            addTrue.setClickListener((btn) -> {
                onSave.accept(new ScriptBoolValue(true));
                close();
            });

            addFalse.setClickListener((btn) -> {
                onSave.accept(new ScriptBoolValue(false));
                close();
            });

            widgets.add(addTrue);
            widgets.add(addFalse);
        }

        addUnknown = new CItem(100-12, 40, unknownIcon);

        addUnknown.setClickListener((btn) -> {
            onSave.accept(new ScriptUnknownValue());
            close();
        });

        widgets.add(addUnknown);

        input.setChangedListener(() -> input.textColor = 0xFFFFFF);

        widgets.add(input);
    }

    @Override
    public void close() {
        onClose.run();
    }
}
