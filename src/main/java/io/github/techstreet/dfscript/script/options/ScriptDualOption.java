package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.util.ScriptOptionSubtypeMismatchException;
import io.github.techstreet.dfscript.script.values.ScriptListValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScriptDualOption implements ScriptOption {

    ScriptOption[] values;
    ScriptOptionEnum[] valueTypes;

    public ScriptDualOption(JsonElement value, ScriptOptionEnum type1, ScriptOptionEnum type2) throws ScriptOptionSubtypeMismatchException {
        valueTypes = new ScriptOptionEnum[]{type1, type2};

        values = new ScriptOption[]
                {
                        ScriptOption.fromJson(value.getAsJsonObject().get("first"), type1, new ArrayList<>()),
                        ScriptOption.fromJson(value.getAsJsonObject().get("second"), type2, new ArrayList<>())
                };

        checkValidity();
    }

    public ScriptDualOption(ScriptOptionEnum type1, ScriptOptionEnum type2) throws ScriptOptionSubtypeMismatchException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        values = new ScriptOption[]{
                ScriptOption.instantiate(type1, new ArrayList<>()),
                ScriptOption.instantiate(type2, new ArrayList<>())
        };
        valueTypes = new ScriptOptionEnum[]{type1, type2};

        checkValidity();
    }

    private void checkValidity() throws ScriptOptionSubtypeMismatchException {
        for (int i = 0; i < 2; i++) {
            if (valueTypes[i].getExtraTypes() != 0) {
                throw new ScriptOptionSubtypeMismatchException("Incorrect amount of extra types");
            }

            if (!values[i].getClass().equals(valueTypes[i].getOptionType())) {
                throw new ScriptOptionSubtypeMismatchException("Incorrect type of a value");
            }
        }
    }

    @Override
    public ScriptValue getValue() {
        List<ScriptValue> result = new ArrayList<>();

        for (ScriptOption o : values) {
            result.add(o.getValue());
        }

        return new ScriptListValue(result);
    }

    @Override
    public boolean convertableTo(ScriptActionArgument.ScriptActionArgumentType arg) {
        return ScriptActionArgument.ScriptActionArgumentType.LIST.convertableTo(arg);
    }

    @Override
    public int create(CScrollPanel panel, int x, int y, int width) {

        int endY = y;

        for (int i = 0; i < 2; i++) {
            int curY = values[i].create(panel, x, y, width / 2 - 1);
            if (curY > endY) {
                endY = curY;
            }

            x += width / 2 + 1;
        }

        return endY;
    }

    @Override
    public JsonElement getJsonElement() {
        JsonObject obj = new JsonObject();

        obj.add("first", values[0].getJsonElement());
        obj.add("second", values[1].getJsonElement());

        return obj;
    }

    @Override
    public List<ScriptOptionEnum> getSubtypes() {
        return Arrays.stream(valueTypes).toList();
    }
}
