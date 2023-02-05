package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptParametrizedPart;
import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.action.ScriptAction;
import io.github.techstreet.dfscript.script.argument.ScriptNumberArgument;
import io.github.techstreet.dfscript.script.argument.ScriptTextArgument;
import io.github.techstreet.dfscript.script.argument.ScriptVariableArgument;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class ScriptAddArgumentScreen extends CScreen {

    private final Script script;
    private final ScriptParametrizedPart action;

    public ScriptAddArgumentScreen(Script script, ScriptParametrizedPart action, int index) {
        this(script,action,index,null);
    }
    public ScriptAddArgumentScreen(Script script, ScriptParametrizedPart action, int index, String overwrite) {
        super(100, 50);
        this.script = script;
        this.action = action;

        CTextField input = new CTextField("Input", 2, 2, 96, 35, true);
        if(overwrite != null) input.setText(overwrite);

        ItemStack textIcon = new ItemStack(Items.BOOK);
        textIcon.setCustomName(Text.literal("Text")
            .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack numberIcon = new ItemStack(Items.SLIME_BALL);
        numberIcon.setCustomName(Text.literal("Number")
            .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack variableIcon = new ItemStack(Items.MAGMA_CREAM);
        variableIcon.setCustomName(Text.literal("Variable")
            .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack clientValueIcon = new ItemStack(Items.NAME_TAG);
        clientValueIcon.setCustomName(Text.literal("Client Value")
            .fillStyle(Style.EMPTY.withItalic(false)));

        ItemStack configValueIcon = new ItemStack(Items.INK_SAC);
        configValueIcon.setCustomName(Text.literal("Config Value")
            .fillStyle(Style.EMPTY.withItalic(false)));


        CItem addNumber = new CItem(2, 40, numberIcon);
        CItem addText = new CItem(12, 40, textIcon);
        CItem addVariable = new CItem(22, 40, variableIcon);
        CItem addClientValue = new CItem(32, 40, clientValueIcon);
        CItem addConfigValue = new CItem(42, 40, configValueIcon);

        input.setChangedListener(() -> input.textColor = 0xFFFFFF);

        addText.setClickListener((btn) -> {
            if(overwrite != null) action.getArguments().remove(index);
            action.getArguments().add(index, new ScriptTextArgument(input.getText()));
            DFScript.MC.setScreen(new ScriptEditPartScreen(action, script));
        });

        addNumber.setClickListener((btn) -> {
            try {
                double number = Double.parseDouble(input.getText());
                if(overwrite != null) action.getArguments().remove(index);
                action.getArguments().add(index, new ScriptNumberArgument(number));
                DFScript.MC.setScreen(new ScriptEditPartScreen(action, script));
            } catch (Exception err) {
                input.textColor = 0xFF3333;
            }
        });

        addVariable.setClickListener((btn) -> {
            if(overwrite != null) action.getArguments().remove(index);
            action.getArguments().add(index, new ScriptVariableArgument(input.getText()));
            DFScript.MC.setScreen(new ScriptEditPartScreen(action, script));
        });

        addClientValue.setClickListener((btn) -> {
            DFScript.MC.setScreen(new ScriptAddClientValueScreen(action, script, index, overwrite));
        });

        addConfigValue.setClickListener((btn) -> {
            DFScript.MC.setScreen(new ScriptAddConfigValueScreen(action, script, index, overwrite));
        });

        widgets.add(input);
        widgets.add(addNumber);
        widgets.add(addText);
        widgets.add(addVariable);
        widgets.add(addClientValue);
        widgets.add(addConfigValue);
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptEditPartScreen(action, script));
    }
}
