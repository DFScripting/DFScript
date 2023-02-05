package io.github.techstreet.dfscript.script.repetitions;

import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.script.*;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.conditions.ScriptBranch;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;
import io.github.techstreet.dfscript.script.render.ScriptPartRenderIconElement;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.Consumer;

public abstract class ScriptRepetition extends ScriptParametrizedPart implements ScriptScopeParent {

    ScriptContainer container;

    public ScriptRepetition(List<ScriptArgument> arguments) {
        super(arguments);
        container = new ScriptContainer(1);
    }

    @Override
    public void create(ScriptPartRender render, Script script) {
        render.addElement(container.createSnippet(0));

        render.addElement(new ScriptPartRenderIconElement(ScriptBranch.closeBracketName, ScriptBranch.closeBracketIcon));
    }

    public abstract boolean checkCondition(ScriptTask task);

    @Override
    public final void run(ScriptTask task) {
        container.runSnippet(task, 0, this);
    }

    @Override
    public void forEach(Consumer<ScriptSnippet> consumer) {
        container.forEach(consumer);
    }

    @Override
    public ScriptContainer container() {
        return container;
    }
}
