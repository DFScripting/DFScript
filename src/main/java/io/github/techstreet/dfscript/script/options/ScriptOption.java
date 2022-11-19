package io.github.techstreet.dfscript.script.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.util.ScriptOptionSubtypeMismatchException;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import net.minecraft.item.Item;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface ScriptOption {
    ScriptValue getValue();
    boolean convertableTo(ScriptActionArgument.ScriptActionArgumentType arg);

    default String getName() {
        String result = getType().getName();

        if(getSubtypes().size() > 0)
        {
            result += "<" + String.join(", ", getSubtypes().stream().map(ScriptOptionEnum::getName).toList()) + ">";
        }

        return result;
    }

    int create(CScrollPanel panel, int x, int y, int width); // the return value = new y

    default ScriptOptionEnum getType() {
        return ScriptOptionEnum.fromClass(getClass());
    }

    JsonElement getJsonElement();

    default List<ScriptOptionEnum> getSubtypes() {
        return new ArrayList<>();
    }

    static ScriptOption fromJson(JsonElement value, ScriptOptionEnum type, List<ScriptOptionEnum> subtypes) throws ScriptOptionSubtypeMismatchException {
        if(type.getExtraTypes() != subtypes.size()) {
            throw new ScriptOptionSubtypeMismatchException("Incorrect amount of extra types");
        }

        Class<?>[] argTypes = new Class[subtypes.size()+1];
        Object[] args = new Object[subtypes.size()+1];
        argTypes[0] = JsonElement.class;
        args[0] = value;
        int i = 1;
        for(ScriptOptionEnum subtype : subtypes) {
            argTypes[i] = ScriptOptionEnum.class;
            args[i] = subtype;

            DFScript.LOGGER.info(subtype.name());

            i++;
        }

        ScriptOption option = null;
        try {
            Constructor<? extends ScriptOption> constructor = type.getOptionType().getConstructor(argTypes);

            DFScript.LOGGER.info("Constructor: " + constructor);

            option = constructor.newInstance(args);
        } catch (Exception e) {
            throw new JsonParseException("No constructor for " + type.name() + " found!");
        }

        /*switch(type.name()) {
            case "TEXT" -> option = new ScriptTextOption(value.getAsString());
            case "INT" -> option = new ScriptIntOption(value.getAsInt());
            case "FLOAT" -> option = new ScriptFloatOption(value.getAsDouble());
            case "KEY" -> option = new ScriptKeyOption(InputUtil.fromTranslationKey(value.getAsString()));
            case "BOOL" -> option = new ScriptBoolOption(value.getAsBoolean());
            case "LIST" -> {
                List<ScriptOption> optionList = new ArrayList<>();

                String optionTypeName = value.getAsJsonObject().get("type").getAsString();
                JsonArray values = value.getAsJsonObject().get("values").getAsJsonArray();

                ScriptOptionEnum optionType = null;

                for (ScriptOptionEnum t : ScriptOptionEnum.values()) {
                    if(t.name().equals(optionTypeName)) {
                        optionType = t;
                        break;
                    }
                }

                if(optionType.getExtraTypes() != 0)
                {
                    throw new JsonParseException("Not a primitive option type (no extra types): " + type);
                }

                for(JsonElement e : values)
                {
                    optionList.add(fromJson(e, optionType, new ArrayList<>()));
                }

                List<ScriptOptionEnum> optionTypes = new ArrayList<>();
                optionTypes.add(optionType);

                option = new ScriptListOption(optionTypes, optionList);
            }
            default -> throw new JsonParseException("Unknown option type: " + type);
        }*/

        return option;
    }

    static ScriptOption instantiate(ScriptOptionEnum type, List<ScriptOptionEnum> subtypes) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(subtypes.size() == 0) return type.getOptionType().getConstructor().newInstance();

        Class<?>[] argTypes = new Class[type.getExtraTypes()];
        Arrays.fill(argTypes, ScriptOptionEnum.class);

        return type.getOptionType().getConstructor(argTypes).newInstance(subtypes.toArray());
    }
}
