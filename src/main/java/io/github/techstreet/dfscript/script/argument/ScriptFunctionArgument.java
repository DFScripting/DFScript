package io.github.techstreet.dfscript.script.argument;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.event.ScriptFunction;
import io.github.techstreet.dfscript.script.event.ScriptHeader;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Type;

public final class ScriptFunctionArgument implements ScriptArgument {

    private String functionArg;

    private transient ScriptHeader header;

    public ScriptFunctionArgument(String functionArg, ScriptHeader header) {
        this.functionArg = functionArg;
        this.header = header;
    }

    @Override
    public ScriptValue getValue(ScriptTask task) {
        return task.stack().getFunctionElement().getFunctionArgument(functionArg);
    }

    @Override
    public boolean convertableTo(ScriptActionArgument.ScriptActionArgumentType type) {
        if (getFunctionArg() != null) {
            return getFunctionArg().type().convertableTo(type);
        }
        return false;
    }

    public ScriptActionArgument getFunctionArg() {
        if(header instanceof ScriptFunction f) {
            return f.argList().getByName(functionArg);
        }
        return null;
    }
    public String getName() {
        return functionArg;
    }

    public void setFunctionArg(String newArg) {
        functionArg = newArg;
    }

    public void setHeader(ScriptHeader header) {
        this.header = header;
    }

    public static class Serializer implements JsonSerializer<ScriptFunctionArgument> {

        @Override
        public JsonElement serialize(ScriptFunctionArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("type", "FUNCTION_ARGUMENT");
            object.addProperty("value", src.getName());
            return object;
        }
    }

    @Override
    public ItemStack getArgIcon() {
        return new ItemStack(Items.BLUE_DYE).setCustomName(Text.literal("Function Argument").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false)));
    }

    @Override
    public String getArgText() {
        if(getFunctionArg() == null) {
            return "Invalid Function Argument";
        }

        return getFunctionArg().name();
    }
}