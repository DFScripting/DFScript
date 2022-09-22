package io.github.techstreet.dfscript.script.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.event.HudRenderEvent;
import io.github.techstreet.dfscript.event.system.CancellableEvent;
import io.github.techstreet.dfscript.script.ScriptGroup;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument.ScriptActionArgumentType;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.menu.ScriptMenu;
import io.github.techstreet.dfscript.script.menu.ScriptMenuButton;
import io.github.techstreet.dfscript.script.menu.ScriptMenuItem;
import io.github.techstreet.dfscript.script.menu.ScriptMenuText;
import io.github.techstreet.dfscript.script.menu.ScriptMenuTextField;
import io.github.techstreet.dfscript.script.menu.ScriptWidget;
import io.github.techstreet.dfscript.script.util.ScriptValueItem;
import io.github.techstreet.dfscript.script.util.ScriptValueJson;
import io.github.techstreet.dfscript.script.values.*;
import io.github.techstreet.dfscript.util.ComponentUtil;
import io.github.techstreet.dfscript.util.FileUtil;
import io.github.techstreet.dfscript.util.ItemUtil;
import io.github.techstreet.dfscript.util.Scheduler;
import io.github.techstreet.dfscript.util.StringUtil;
import io.github.techstreet.dfscript.util.chat.ChatUtil;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameMode;
public enum ScriptActionType {

    DISPLAY_CHAT(builder -> builder.name("DisplayChat")
        .description("Displays a message in the chat.")
        .icon(Items.BOOK)
        .category(ScriptActionCategory.VISUALS)
        .arg("Texts", ScriptActionArgumentType.TEXT, arg -> arg.plural(true))
        .action(ctx -> {
            StringBuilder sb = new StringBuilder();
            for (ScriptValue arg : ctx.pluralValue("Texts")) {
                sb.append(arg.asText())
                    .append(" ");
            }
            sb.deleteCharAt(sb.length() - 1);
            ChatUtil.sendMessage(ComponentUtil.fromString(ComponentUtil.andsToSectionSigns(sb.toString())));
        })),

    ACTIONBAR(builder -> builder.name("ActionBar")
        .description("Displays a message in the action bar.")
        .icon(Items.SPRUCE_SIGN)
        .category(ScriptActionCategory.VISUALS)
        .arg("Texts", ScriptActionArgumentType.TEXT, arg -> arg.plural(true))
        .action(ctx -> {
            StringBuilder sb = new StringBuilder();
            for (ScriptValue arg : ctx.pluralValue("Texts")) {
                sb.append(arg.asText())
                    .append(" ");
            }
            sb.deleteCharAt(sb.length() - 1);
            ChatUtil.sendActionBar(ComponentUtil.fromString(ComponentUtil.andsToSectionSigns(sb.toString())));
        })),

    SEND_CHAT(builder -> builder.name("SendChat")
        .description("Makes the player send a chat message.")
        .icon(Items.PAPER)
        .category(ScriptActionCategory.ACTIONS)
        .arg("Texts", ScriptActionArgumentType.TEXT, arg -> arg.plural(true))
        .action(ctx -> {
            StringBuilder sb = new StringBuilder();
            for (ScriptValue arg : ctx.pluralValue("Texts")) {
                sb.append(arg.asText())
                    .append(" ");
            }

            sb.deleteCharAt(sb.length() - 1);
            io.github.techstreet.dfscript.DFScript.MC.player.sendChatMessage(sb.toString(), Text.literal(sb.toString()));
        })),

    REPEAT_MULTIPLE(builder -> builder.name("RepeatMultiple")
        .description("Repeats a specified amount of times.")
        .icon(Items.REDSTONE)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Times", ScriptActionArgumentType.NUMBER)
        .arg("Current", ScriptActionArgumentType.VARIABLE, b -> b.optional(true))
        .hasChildren(true)
        .group(ScriptGroup.REPETITION)
        .action(ctx -> {
            ctx.scheduleInner(
                    null,
                    context -> {
                        if(!context.hasScopeVariable("Counter")) {
                            context.setScopeVariable("Counter", 0);
                        }

                        int counter = (Integer)context.getScopeVariable("Counter")+1;

                        if(counter <= context.value("Times").asNumber()) {
                            context.setScopeVariable("Counter", counter);
                            if (context.argMap().containsKey("Current")) {
                                context.context().setVariable(context.variable("Current").name(), new ScriptNumberValue(counter));
                            }
                            context.setLastIfResult(true);
                        }
                    }
            );

            /*if (ctx.argMap().containsKey("Current")) {
                ctx.context().setVariable(ctx.variable("Current").name(), new ScriptNumberValue(1));
            }
            for (int i = (int) ctx.value("Times").asNumber(); i > 0; i--) {
                int current = i+1;
                ctx.scheduleInner(() -> {
                    if (ctx.argMap().containsKey("Current")) {
                        ctx.context().setVariable(ctx.variable("Current").name(), new ScriptNumberValue(current));
                    }
                });
            }*/
        })),

    CLOSE_BRACKET(builder -> builder.name("CloseBracket")
        .description("Closes the current code block.")
        .icon(Items.PISTON)
        .category(ScriptActionCategory.MISC)),

    SET_VARIABLE(builder -> builder.name("SetVariable")
        .description("Sets a variable to a value.")
        .icon(Items.IRON_INGOT)
        .category(ScriptActionCategory.VARIABLES)
        .arg("Variable", ScriptActionArgumentType.VARIABLE)
        .arg("Value", ScriptActionArgumentType.ANY)
        .action(ctx -> ctx.context().setVariable(
            ctx.variable("Variable").name(),
            ctx.value("Value")
        ))),

    INCREMENT(builder -> builder.name("Increment")
        .description("Increments a variable by a value.")
        .icon(Items.GLOWSTONE_DUST)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Variable", ScriptActionArgumentType.VARIABLE)
        .arg("Amount", ScriptActionArgumentType.NUMBER, arg -> arg.plural(true))
        .action(ctx -> {
            double value = ctx.value("Variable").asNumber();
            for (ScriptValue val : ctx.pluralValue("Amount")) {
                value += val.asNumber();
            }
            ctx.context().setVariable(
                ctx.variable("Variable").name(),
                new ScriptNumberValue(value)
            );
        })),

    DECREMENT(builder -> builder.name("Decrement")
        .description("Decrements a variable by a value.")
        .icon(Items.REDSTONE)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Variable", ScriptActionArgumentType.VARIABLE)
        .arg("Amount", ScriptActionArgumentType.NUMBER, arg -> arg.plural(true))
        .action(ctx -> {
            double value = ctx.value("Variable").asNumber();
            for (ScriptValue val : ctx.pluralValue("Amount")) {
                value -= val.asNumber();
            }
            ctx.context().setVariable(
                ctx.variable("Variable").name(),
                new ScriptNumberValue(value)
            );
        })),

    JOIN_TEXT(builder -> builder.name("JoinText")
        .description("Joins multiple texts into one.")
        .icon(Items.BOOK)
        .category(ScriptActionCategory.TEXTS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Texts", ScriptActionArgumentType.TEXT, arg -> arg.plural(true))
        .action(ctx -> {
            StringBuilder sb = new StringBuilder();
            for (ScriptValue arg : ctx.pluralValue("Texts")) {
                sb.append(arg.asText());
            }
            ctx.context().setVariable(
                ctx.variable("Result").name(),
                new ScriptTextValue(sb.toString())
            );
        })),

    ADD(builder -> builder.name("Add")
        .description("Sets a variable to the sum of the number(s).")
        .icon(Items.BRICK)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Numbers", ScriptActionArgumentType.NUMBER, arg -> arg.plural(true))
        .action(ctx -> {
            double value = 0;
            for (ScriptValue val : ctx.pluralValue("Numbers")) {
                value += val.asNumber();
            }
            ctx.context().setVariable(
                ctx.variable("Result").name(),
                new ScriptNumberValue(value)
            );
        })),

    SUBTRACT(builder -> builder.name("Subtract")
        .description("Sets a variable to the difference of the number(s).")
        .icon(Items.NETHER_BRICK)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Numbers", ScriptActionArgumentType.NUMBER, arg -> arg.plural(true))
        .action(ctx -> {
            double value = ctx.value("Numbers").asNumber();
            boolean first = true;
            for (ScriptValue val : ctx.pluralValue("Numbers")) {
                if (first) {
                    first = false;
                } else {
                    value -= val.asNumber();
                }
            }
            ctx.context().setVariable(
                ctx.variable("Result").name(),
                new ScriptNumberValue(value)
            );
        })),

    MULTIPLY(builder -> builder.name("Multiply")
        .description("Sets a variable to the product of the number(s).")
        .icon(Items.BRICKS)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Numbers", ScriptActionArgumentType.NUMBER, arg -> arg.plural(true))
        .action(ctx -> {
            double value = 1;
            for (ScriptValue val : ctx.pluralValue("Numbers")) {
                value *= val.asNumber();
            }
            ctx.context().setVariable(
                ctx.variable("Result").name(),
                new ScriptNumberValue(value)
            );
        })),

    DIVIDE(builder -> builder.name("Divide")
        .description("Sets a variable to the quotient of the number(s).")
        .icon(Items.NETHER_BRICKS)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Numbers", ScriptActionArgumentType.NUMBER, arg -> arg.plural(true))
        .action(ctx -> {
            double value = ctx.value("Numbers").asNumber();
            boolean first = true;
            for (ScriptValue val : ctx.pluralValue("Numbers")) {
                if (first) {
                    first = false;
                } else {
                    value /= val.asNumber();
                }
            }
            ctx.context().setVariable(
                ctx.variable("Result").name(),
                new ScriptNumberValue(value)
            );
        })),

    MODULO(builder -> builder.name("Modulo")
        .description("Sets a variable to the remainder of the numbers.")
        .icon(Items.NETHER_WART)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Dividend", ScriptActionArgumentType.NUMBER)
        .arg("Divisor", ScriptActionArgumentType.NUMBER)
        .action(ctx -> {
            double dividend = ctx.value("Dividend").asNumber();
            double divisor = ctx.value("Divisor").asNumber();
            ctx.context().setVariable(
                ctx.variable("Result").name(),
                new ScriptNumberValue(dividend % divisor)
            );
        })),

    IF_EQUALS(builder -> builder.name("If Equals")
        .description("Checks if one value is equal to another.")
        .icon(Items.IRON_INGOT)
        .category(ScriptActionCategory.VARIABLES)
        .arg("Value", ScriptActionArgumentType.ANY)
        .arg("Other", ScriptActionArgumentType.ANY)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            if (ctx.value("Value").valueEquals(ctx.value("Other"))) {
                ctx.setLastIfResult(true);
            }
        })),

    IF_NOT_EQUALS(builder -> builder.name("If Not Equals")
        .description("Checks if one value is not equal to another.")
        .icon(Items.BARRIER)
        .category(ScriptActionCategory.VARIABLES)
        .arg("Value", ScriptActionArgumentType.ANY)
        .arg("Other", ScriptActionArgumentType.ANY)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            if (!ctx.value("Value").valueEquals(ctx.value("Other"))) {
                ctx.setLastIfResult(true);
            }
        })),

    IF_GREATER(builder -> builder.name("If Greater")
        .description("Checks if one number is greater than another.")
        .icon(Items.BRICK)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Value", ScriptActionArgumentType.NUMBER)
        .arg("Other", ScriptActionArgumentType.NUMBER)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            if (ctx.value("Value").asNumber() > ctx.value("Other").asNumber()) {
                ctx.setLastIfResult(true);
            }
        })),

    IF_GREATER_EQUALS(builder -> builder.name("If Greater Equals")
        .description("Checks if one number is greater than or equal to another.")
        .icon(Items.BRICKS)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Value", ScriptActionArgumentType.NUMBER)
        .arg("Other", ScriptActionArgumentType.NUMBER)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            if (ctx.value("Value").asNumber() >= ctx.value("Other").asNumber()) {
                ctx.setLastIfResult(true);
            }
        })),

    IF_LESS(builder -> builder.name("If Less")
        .description("Checks if one number is less than another.")
        .icon(Items.NETHER_BRICK)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Value", ScriptActionArgumentType.NUMBER)
        .arg("Other", ScriptActionArgumentType.NUMBER)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            if (ctx.value("Value").asNumber() < ctx.value("Other").asNumber()) {
                ctx.setLastIfResult(true);
            }
        })),

    IF_LESS_EQUALS(builder -> builder.name("If Less Equals")
        .description("Checks if one number is less than or equal to another.")
        .icon(Items.NETHER_BRICKS)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Value", ScriptActionArgumentType.NUMBER)
        .arg("Other", ScriptActionArgumentType.NUMBER)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            if (ctx.value("Value").asNumber() <= ctx.value("Other").asNumber()) {
                ctx.setLastIfResult(true);
            }
        })),

    CANCEL_EVENT(builder -> builder.name("Cancel Event")
        .description("Cancels the event.")
        .icon(Items.BARRIER)
        .category(ScriptActionCategory.MISC)
        .action(ctx -> {
            if (ctx.event() instanceof CancellableEvent ce) {
                ce.setCancelled(true);
            }
        })),

    UNCANCEL_EVENT(builder -> builder.name("Uncancel Event")
        .description("Uncancels the event.")
        .icon(Items.STRUCTURE_VOID)
        .category(ScriptActionCategory.MISC)
        .action(ctx -> {
            if (ctx.event() instanceof CancellableEvent ce) {
                ce.setCancelled(false);
            }
        })),

    CREATE_LIST(builder -> builder.name("Create List")
        .description("Creates a new list.")
        .icon(Items.ENDER_CHEST)
        .category(ScriptActionCategory.LISTS)
        .arg("Variable", ScriptActionArgumentType.VARIABLE)
        .arg("Values", ScriptActionArgumentType.ANY, b -> b.plural(true)
            .optional(true))
        .action(ctx -> {
            ArrayList<ScriptValue> values = new ArrayList<>();
            if (ctx.argMap().containsKey("Values")) {
                for (ScriptArgument v : ctx.argMap().get("Values")) {
                    values.add(v.getValue(ctx.event(), ctx.context()));
                }
            }
            ctx.context().setVariable(ctx.variable("Variable").name(), new ScriptListValue(values));
        })),

    APPEND_VALUE(builder -> builder.name("Append Value")
        .description("Appends values to a list.")
        .icon(Items.FURNACE)
        .category(ScriptActionCategory.LISTS)
        .arg("List", ScriptActionArgumentType.VARIABLE)
        .arg("Values", ScriptActionArgumentType.ANY, b -> b.plural(true))
        .action(ctx -> {
            List<ScriptValue> list = ctx.value("List").asList();
            for (ScriptArgument v : ctx.argMap().get("Values")) {
                list.add(v.getValue(ctx.event(), ctx.context()));
            }
            ctx.context().setVariable(ctx.variable("List").name(), new ScriptListValue(list));
        })),

    APPEND_LIST_VALUES(builder -> builder.name("Append List Values")
            .description("Appends one list's contents to another.")
            .icon(Items.BLAST_FURNACE)
            .category(ScriptActionCategory.LISTS)
            .arg("Base List", ScriptActionArgumentType.VARIABLE)
            .arg("Other List", ScriptActionArgumentType.LIST)
            .action(ctx -> {

                List<ScriptValue> receiver = ctx.value("Receiving List").asList();
                List<ScriptValue> donor = ctx.value("Other List").asList();

                receiver.addAll(donor);

                ctx.context().setVariable(ctx.variable("Receiving List").name(), new ScriptListValue(receiver));
            })),

    GET_LIST_VALUE(builder -> builder.name("Get List Value")
        .description("Gets a value from a list.")
        .icon(Items.BOOK)
        .category(ScriptActionCategory.LISTS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("List", ScriptActionArgumentType.VARIABLE)
        .arg("Index", ScriptActionArgumentType.NUMBER)
        .action(ctx -> {
            List<ScriptValue> list = ctx.value("List").asList();
         // force index consistent with diamondfire indexes
            int index = (int) ctx.value("Index").asNumber() - 1;
            if (index < 0 || index >= list.size()) {
                ctx.context().setVariable(ctx.variable("Result").name(), new ScriptUnknownValue());
            } else {
                ctx.context().setVariable(ctx.variable("Result").name(), list.get(index));
            }
        })),

    SET_LIST_VALUE(builder -> builder.name("Set List Value")
        .description("Sets a value in a list.")
        .icon(Items.WRITABLE_BOOK)
        .category(ScriptActionCategory.LISTS)
        .arg("List", ScriptActionArgumentType.VARIABLE)
        .arg("Index", ScriptActionArgumentType.NUMBER)
        .arg("Value", ScriptActionArgumentType.ANY)
        .action(ctx -> {
            List<ScriptValue> list = ctx.value("List").asList();
            // force index consistent with diamondfire indexes
            int index = (int) ctx.value("Index").asNumber() - 1;
            if (index < 0 || index >= list.size()) {
                return;
            }
            list.set(index, ctx.value("Value"));
            ctx.context().setVariable(ctx.variable("List").name(), new ScriptListValue(list));
        })),

    REMOVE_LIST_AT_INDEX_VALUE(builder -> builder.name("Remove List Value")
        .description("Removes a value from a list.")
        .icon(Items.TNT)
        .category(ScriptActionCategory.LISTS)
        .arg("List", ScriptActionArgumentType.VARIABLE)
        .arg("Index", ScriptActionArgumentType.NUMBER)
        .action(ctx -> {
            List<ScriptValue> list = ctx.value("List").asList();
            // force index consistent with diamondfire indexes
            int index = (int) ctx.value("Index").asNumber() - 1;
            if (index < 0 || index >= list.size()) {
                return;
            }
            list.remove(index);
            ctx.context().setVariable(ctx.variable("List").name(), new ScriptListValue(list));
        })),

    REMOVE_LIST_VALUE(builder -> builder.name("Remove List Value")
        .description("Removes a value from a list.")
        .icon(Items.TNT_MINECART)
        .category(ScriptActionCategory.LISTS)
        .arg("List", ScriptActionArgumentType.VARIABLE)
        .arg("Value", ScriptActionArgumentType.ANY)
        .action(ctx -> {
            List<ScriptValue> list = ctx.value("List").asList();

            list.removeIf(value -> value.valueEquals(ctx.value("Value")));

            ctx.context().setVariable(ctx.variable("List").name(), new ScriptListValue(list));
        })),

    LIST_LENGTH(builder -> builder.name("List Length")
        .description("Returns the length of a list.")
        .icon(Items.BOOKSHELF)
        .category(ScriptActionCategory.LISTS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("List", ScriptActionArgumentType.LIST)
        .action(ctx -> {
            ctx.context().setVariable(ctx.variable("Result").name(), new ScriptNumberValue(ctx.value("List").asList().size()));
        })),

    IF_LIST_CONTAINS(builder -> builder.name("If List Contains")
        .description("Checks if a list contains a value.")
        .icon(Items.BOOKSHELF)
        .category(ScriptActionCategory.LISTS)
        .arg("List", ScriptActionArgumentType.LIST)
        .arg("Value", ScriptActionArgumentType.ANY)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            List<ScriptValue> list = ctx.value("List").asList();
            if (list.stream().anyMatch(value -> value.valueEquals(ctx.value("Value")))) {
                ctx.setLastIfResult(true);
            }
        })),

    IF_TEXT_CONTAINS(builder -> builder.name("If Text Contains")
        .description("Checks if a text contains a value.")
        .icon(Items.NAME_TAG)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Subtext", ScriptActionArgumentType.TEXT)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String subtext = ctx.value("Subtext").asText();
            if (text.contains(subtext)) {
                ctx.setLastIfResult(true);
            }
        })),

    IF_MATCHES_REGEX(builder -> builder.name("If Matches Regex")
        .description("Checks if a text matches a regex.")
        .icon(Items.ANVIL)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Regex", ScriptActionArgumentType.TEXT)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String regex = ctx.value("Regex").asText();
            if (text.matches(regex)) {
                ctx.setLastIfResult(true);
            }
        })),

    IF_STARTS_WITH(builder -> builder.name("If Starts With")
        .description("Checks if a text starts with an other.")
        .icon(Items.FEATHER)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Subtext", ScriptActionArgumentType.TEXT)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String subtext = ctx.value("Subtext").asText();
            if (text.startsWith(subtext)) {
                ctx.setLastIfResult(true);
            }
        })),

    IF_LIST_DOESNT_CONTAIN(builder -> builder.name("If List Doesnt Contain")
        .description("Checks if a list doesnt contain a value.")
        .icon(Items.BOOKSHELF)
        .category(ScriptActionCategory.LISTS)
        .arg("List", ScriptActionArgumentType.LIST)
        .arg("Value", ScriptActionArgumentType.ANY)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            List<ScriptValue> list = ctx.value("List").asList();
            if (list.stream().noneMatch(value -> value.valueEquals(ctx.value("Value")))) {
                ctx.setLastIfResult(true);
            }
        })),

    IF_TEXT_DOESNT_CONTAIN(builder -> builder.name("If Text Doesnt Contain")
        .description("Checks if a text doesnt contain a value.")
        .icon(Items.NAME_TAG)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Subtext", ScriptActionArgumentType.TEXT)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String subtext = ctx.value("Subtext").asText();
            if (!text.contains(subtext)) {
                ctx.setLastIfResult(true);
            }
        })),

    IF_DOESNT_START_WITH(builder -> builder.name("If Doesnt Start With")
        .description("Checks if a text doesnt start with an other.")
        .icon(Items.FEATHER)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Subtext", ScriptActionArgumentType.TEXT)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String subtext = ctx.value("Subtext").asText();
            if (!text.startsWith(subtext)) {
                ctx.setLastIfResult(true);
            }
        })),

    IF_DOESNT_MATCH_REGEX(builder -> builder.name("If Doesnt Match Regex")
        .description("Checks if a text doesnt match a regex.")
        .icon(Items.ANVIL)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Regex", ScriptActionArgumentType.TEXT)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String regex = ctx.value("Regex").asText();
            if (!text.matches(regex)) {
                ctx.setLastIfResult(true);
            }
        })),

    WAIT(builder -> builder.name("Wait")
        .description("Waits for a given amount of time.")
        .icon(Items.CLOCK)
        .category(ScriptActionCategory.MISC)
        .arg("Ticks", ScriptActionArgumentType.NUMBER)
        .action(ctx -> {
            int n = 0;
            while(!(ctx.task().stack().peekOriginal(n) < 0)) {
                int pos = ctx.task().stack().peekOriginal(n);
                n++;
                if(pos >= 0 && ctx.script().getParts().get(pos).getGroup() == ScriptGroup.REPETITION) {
                    if(ctx.task().stack().peekElement(n).hasVariable("LagslayerCounter"))
                    {
                        ctx.task().stack().peekElement(n).setVariable("LagslayerCounter", 0);
                    }
                }
            }

            ctx.task().stop();//Stop the current thread
            ctx.task().stack().increase();//Go to the next action
            Scheduler.schedule((int) ctx.value("Ticks").asNumber(), () -> ctx.task().run());//Resume the task after the given amount of ticks
        })),

    CREATE_DICT(builder -> builder.name("Create Dictionary")
        .description("Creates a new dictionary.")
        .icon(Items.ENDER_CHEST)
        .category(ScriptActionCategory.DICTIONARIES)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Keys", ScriptActionArgumentType.LIST, b -> b.optional(true))
        .arg("Values", ScriptActionArgumentType.LIST, b -> b.optional(true))
        .action(ctx -> {

            HashMap<String, ScriptValue> dict = new HashMap<String, ScriptValue>();

                if (ctx.argMap().containsKey("Keys") && ctx.argMap().containsKey("Values")) {
                    List<ScriptValue> keys = ctx.value("Keys").asList();
                    List<ScriptValue> values = ctx.value("Values").asList();

                    // make sure we don't iterate past the end of a list
                    int lowerLength = Math.min(keys.size(), values.size());

                    for (int i = 0; i < lowerLength; i++) {
                        dict.put(keys.get(i).asText(), values.get(i));
                    }
                }

            ctx.context().setVariable(ctx.variable("Result").name(), new ScriptDictionaryValue(dict));
        })),

    PARSE_JSON(builder -> builder.name("Parse from JSON")
        .description("Creates a dict from JSON data.")
        .icon(Items.ANVIL)
        .category(ScriptActionCategory.DICTIONARIES)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("JSON", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            ScriptValue dict;

            try{
                dict = ScriptValueJson.fromJson(JsonParser.parseString(ctx.value("JSON").toString()));
            }
            catch (JsonParseException e) {dict = new ScriptUnknownValue();}


            ctx.context().setVariable(ctx.variable("Result").name(), dict);
        })),

    GET_DICT_VALUE(builder -> builder.name("Get Dictionary Value")
        .description("Gets a value from a dictionary.")
        .icon(Items.BOOK)
        .category(ScriptActionCategory.DICTIONARIES)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Dictionary", ScriptActionArgumentType.DICTIONARY)
        .arg("Key", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
            String key = ctx.value("Key").asText();
            if (dict.containsKey(key)) {
                ctx.context().setVariable(ctx.variable("Result").name(), dict.get(key));
            } else {
                ctx.context().setVariable(ctx.variable("Result").name(), new ScriptUnknownValue());
            }
        })),

    SET_DICT_VALUE(builder -> builder.name("Set Dictionary Value")
        .description("Sets a value in a dictionary.")
        .icon(Items.WRITABLE_BOOK)
        .category(ScriptActionCategory.DICTIONARIES)
        .arg("Dictionary", ScriptActionArgumentType.VARIABLE)
        .arg("Key", ScriptActionArgumentType.TEXT)
        .arg("Value", ScriptActionArgumentType.ANY)
        .action(ctx -> {
            HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
            String key = ctx.value("Key").asText();
            dict.put(key, ctx.value("Value"));
            ctx.context().setVariable(ctx.variable("Dictionary").name(), new ScriptDictionaryValue(dict));
        })),

    GET_DICT_SIZE(builder -> builder.name("Get Dictionary Size")
        .description("Gets the size of a dictionary.")
        .icon(Items.BOOKSHELF)
        .category(ScriptActionCategory.DICTIONARIES)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Dictionary", ScriptActionArgumentType.DICTIONARY)
        .action(ctx -> {
            HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
            ctx.context().setVariable(ctx.variable("Result").name(), new ScriptNumberValue(dict.size()));
        })),

    GET_DICT_KEYS(builder -> builder.name("Get Dictionary Keys")
        .description("Gets a list of the keys in a dictionary.")
        .icon(Items.FURNACE_MINECART)
        .category(ScriptActionCategory.DICTIONARIES)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Dictionary", ScriptActionArgumentType.DICTIONARY)
        .action(ctx -> {
            HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
            ctx.context().setVariable(ctx.variable("Result").name(), new ScriptListValue(dict.keySet().stream().map(x -> (ScriptValue) new ScriptTextValue(x)).toList()));
        })
    ),

    IF_DICT_KEY_EXISTS(builder -> builder.name("If Dictionary Key Exists")
        .description("Checks if a key exists in a dictionary.")
        .icon(Items.NAME_TAG)
        .category(ScriptActionCategory.DICTIONARIES)
        .arg("Dictionary", ScriptActionArgumentType.DICTIONARY)
        .arg("Key", ScriptActionArgumentType.TEXT)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
            String key = ctx.value("Key").asText();
            if (dict.containsKey(key)) {
                ctx.setLastIfResult(true);
            }
        })),

    IF_DICT_KEY_DOESNT_EXIST(builder -> builder.name("If Dictionary Key Doesnt Exist")
        .description("Checks if a key doesnt exist in a dictionary.")
        .icon(Items.NAME_TAG)
        .category(ScriptActionCategory.DICTIONARIES)
        .arg("Dictionary", ScriptActionArgumentType.DICTIONARY)
        .arg("Key", ScriptActionArgumentType.TEXT)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
            String key = ctx.value("Key").asText();
            if (!dict.containsKey(key)) {
                ctx.setLastIfResult(true);
            }
        })),

    REMOVE_DICT_ENTRY(builder -> builder.name("Remove Dictionary Entry")
        .description("Removes a key from a dictionary.")
        .icon(Items.TNT_MINECART)
        .category(ScriptActionCategory.DICTIONARIES)
        .arg("Dictionary", ScriptActionArgumentType.VARIABLE)
        .arg("Key", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
            String key = ctx.value("Key").asText();
            dict.remove(key);
            ctx.context().setVariable(ctx.variable("Dictionary").name(), new ScriptDictionaryValue(dict));
        })),


    FOR_EACH_IN_LIST(builder -> builder.name("For Each In List")
        .description("Iterates over a list.")
        .icon(Items.BOOKSHELF)
        .category(ScriptActionCategory.LISTS)
        .arg("Variable", ScriptActionArgumentType.VARIABLE)
        .arg("List", ScriptActionArgumentType.LIST)
        .hasChildren(true)
        .group(ScriptGroup.REPETITION)
        .action(ctx -> {
            ctx.scheduleInner(
                    null,
                    context -> {
                        if(!context.hasScopeVariable("Counter")) {
                            context.setScopeVariable("Counter", 0);
                        }

                        int counter = (Integer)context.getScopeVariable("Counter")+1;
                        List<ScriptValue> list = context.value("List").asList();

                        if(counter <= list.size()) {
                            context.setScopeVariable("Counter", counter);
                            context.context().setVariable(context.variable("Variable").name(), list.get(counter-1));
                            context.setLastIfResult(true);
                        }
                    }
            );

            /*List<ScriptValue> list = ctx.value("List").asList();
            if (!list.isEmpty()) {
                ctx.context().setVariable(ctx.variable("Variable").name(), list.get(0));
            }
            Lists.reverse(list);
            for (ScriptValue item : list) {
                ctx.scheduleInner(() -> {
                    ctx.context().setVariable(ctx.variable("Variable").name(), item);
                });
            }*/
        })),

    DICT_FOR_EACH(builder -> builder.name("For Each In Dictionary")
        .description("Iterates over a dictionary.")
        .icon(Items.BOOKSHELF)
        .category(ScriptActionCategory.DICTIONARIES)
        .arg("Key", ScriptActionArgumentType.VARIABLE)
        .arg("Value", ScriptActionArgumentType.VARIABLE)
        .arg("Dictionary", ScriptActionArgumentType.DICTIONARY)
        .hasChildren(true)
        .group(ScriptGroup.REPETITION)
        .action(ctx -> {
            ctx.scheduleInner(
                    null,
                    context -> {
                        HashMap<String, ScriptValue> dict = context.value("Dictionary").asDictionary();

                        if(!context.hasScopeVariable("Iterator")) {
                            context.setScopeVariable("Iterator", dict.entrySet().iterator());
                        }

                        Iterator<Map.Entry<String, ScriptValue>> iterator = (Iterator<Map.Entry<String, ScriptValue>>) context.getScopeVariable("Iterator");

                        if(iterator.hasNext()) {
                            Map.Entry<String, ScriptValue> entry = iterator.next();
                            context.setScopeVariable("Iterator", iterator);
                            context.context().setVariable(context.variable("Key").name(), new ScriptTextValue(entry.getKey()));
                            context.context().setVariable(context.variable("Value").name(), entry.getValue());
                            context.setLastIfResult(true);
                        }
                    }
            );

            /*HashMap<String, ScriptValue> dict = ctx.value("Dictionary").asDictionary();
            for (Map.Entry<String, ScriptValue> entry : dict.entrySet()) {
                ctx.scheduleInner(() -> {
                    ctx.context().setVariable(ctx.variable("Key").name(), new ScriptTextValue(entry.getKey()));
                    ctx.context().setVariable(ctx.variable("Value").name(), entry.getValue());
                });
            }*/
        })),

    ROUND_NUM(builder -> builder.name("Round Number")
        .description("Rounds a number.")
        .icon(Items.QUARTZ_STAIRS)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Number", ScriptActionArgumentType.NUMBER)
        .action(ctx -> {
            double number = ctx.value("Number").asNumber();
            ctx.context().setVariable(ctx.variable("Result").name(), new ScriptNumberValue(Math.round(number)));
        })),

    FLOOR_NUM(builder -> builder.name("Floor Number")
        .description("Rounds a number down.")
        .icon(Items.OAK_STAIRS)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Number", ScriptActionArgumentType.NUMBER)
        .action(ctx -> {
            double number = ctx.value("Number").asNumber();
            ctx.context().setVariable(ctx.variable("Result").name(), new ScriptNumberValue(Math.floor(number)));
        })),

    CEIL_NUM(builder -> builder.name("Ceil Number")
        .description("Rounds a number up.")
        .icon(Items.DARK_OAK_STAIRS)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Number", ScriptActionArgumentType.NUMBER)
        .action(ctx -> {
            double number = ctx.value("Number").asNumber();
            ctx.context().setVariable(ctx.variable("Result").name(), new ScriptNumberValue(Math.ceil(number)));
        })),

    REGISTER_CMD(builder -> builder.name("Register Command")
        .description("Registers a /cmd completion.")
        .icon(Items.COMMAND_BLOCK)
        .category(ScriptActionCategory.MISC)
        .arg("Commands", ScriptActionArgumentType.TEXT, b -> b.plural(true))
        .action(ctx -> {
            for (ScriptValue cmd : ctx.pluralValue("Commands")) {
                String[] args = cmd.asText().split(" ", -1);
                ArgumentBuilder<FabricClientCommandSource, ?> ab = RequiredArgumentBuilder.argument("args", StringArgumentType.greedyString());

                ab.executes(ctx2 -> 0);

                for (int i = args.length - 1; i >= 0; i--) {
                    LiteralArgumentBuilder<FabricClientCommandSource> l = LiteralArgumentBuilder.literal(args[i]);
                    l.then(ab);
                    ab = l;
                }

                if (ab instanceof LiteralArgumentBuilder lab) {
                    if (ClientCommandManager.getActiveDispatcher() == null) {
                        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(lab));
                    } else {
                        ClientCommandManager.getActiveDispatcher().register(lab);
                    }
                }

                ClientPlayNetworkHandler nh = DFScript.MC.getNetworkHandler();
                if (nh != null) {
                    nh.onCommandTree(new CommandTreeS2CPacket(nh.getCommandDispatcher().getRoot()));
                }
            }
        })),

    IF_GUI_OPEN(builder -> builder.name("If GUI Open")
        .description("Executes if a gui is open.")
        .icon(Items.BOOK)
        .hasChildren(true)
        .category(ScriptActionCategory.MISC)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            if (io.github.techstreet.dfscript.DFScript.MC.currentScreen != null) {
                ctx.setLastIfResult(true);
            }
        })),

    IF_GUI_CLOSED(builder -> builder.name("If GUI Not Open")
        .description("Executes if no gui is open.")
        .icon(Items.BOOK)
        .hasChildren(true)
        .category(ScriptActionCategory.MISC)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            if (io.github.techstreet.dfscript.DFScript.MC.currentScreen == null) {
                ctx.setLastIfResult(true);
            }
        })),

    COPY_TEXT(builder -> builder.name("Copy Text")
        .description("Copies the text to the clipboard.")
        .icon(Items.PAPER)
        .category(ScriptActionCategory.TEXTS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            io.github.techstreet.dfscript.DFScript.MC.keyboard.setClipboard(ctx.value("Text").asText());
        })),

    SPLIT_TEXT(builder -> builder.name("Split Text")
        .description("Splits a text into a list of texts.")
        .icon(Items.SHEARS)
        .category(ScriptActionCategory.TEXTS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Separator", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            String separator = ctx.value("Separator").asText();
            List<ScriptValue> split = new ArrayList<>();

            for (String s : text.split(Pattern.quote(separator))) {
                split.add(new ScriptTextValue(s));
            }

            ctx.context().setVariable(ctx.variable("Result").name(), new ScriptListValue(split));
        })),

    REGEX_SPLIT_TEXT(builder -> builder.name("Split Text by Regex")
            .description("Splits a text into a list of texts\nusing a regex as a separator.")
            .icon(Items.SHEARS, true)
            .category(ScriptActionCategory.TEXTS)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Text", ScriptActionArgumentType.TEXT)
            .arg("Separator (Regex)", ScriptActionArgumentType.TEXT)
            .action(ctx -> {
                String text = ctx.value("Text").asText();
                String separator = ctx.value("Separator (Regex)").asText();
                List<ScriptValue> split = new ArrayList<>();

                for (String s : text.split(separator)) {
                    split.add(new ScriptTextValue(s));
                }

                ctx.context().setVariable(ctx.variable("Result").name(), new ScriptListValue(split));
            })),

    STOP(builder -> builder.name("Stop Codeline")
        .description("Stops the current codeline.")
        .icon(Items.BARRIER)
        .category(ScriptActionCategory.MISC)
        .action(ctx -> {
            ctx.task().stop();
        })),

    SKIP_ITERATION(builder -> builder.name("Skip Iteration")
        .description("Skips the current iteration of the latest loop.")
        .icon(Items.ENDER_PEARL)
        .category(ScriptActionCategory.MISC)
        .action(ctx -> {
            int n = 0;
            while(!(ctx.task().stack().peekOriginal(n) < 0)) {
                int pos = ctx.task().stack().peekOriginal(n);
                n++;
                ctx.context().forceEndScope();
                if(pos < 0 || ctx.script().getParts().get(pos).getGroup() == ScriptGroup.REPETITION) {
                    break;
                }
            }
        })),

    STOP_REPETITION(builder -> builder.name("Stop Repetition")
        .description("Stops the latest loop.")
        .icon(Items.PRISMARINE_SHARD)
        .category(ScriptActionCategory.MISC)
        .action(ctx -> {
            ctx.context().breakLoop();
            int n = 0;
            while(!(ctx.task().stack().peekOriginal(n) < 0)) {
                int pos = ctx.task().stack().peekOriginal(n);
                n++;
                ctx.context().forceEndScope();
                if(pos < 0 || ctx.script().getParts().get(pos).getGroup() == ScriptGroup.REPETITION) {
                    break;
                }
            }
        })),

    PLAY_SOUND(builder -> builder.name("Play Sound")
        .description("Plays a sound.")
        .icon(Items.NAUTILUS_SHELL)
        .category(ScriptActionCategory.VISUALS)
        .arg("Sound", ScriptActionArgumentType.TEXT)
        .arg("Volume", ScriptActionArgumentType.NUMBER, b -> b.optional(true))
        .arg("Pitch", ScriptActionArgumentType.NUMBER, b -> b.optional(true))
        .action(ctx -> {
            String sound = ctx.value("Sound").asText();
            double volume = 1;
            double pitch = 1;

            if (ctx.argMap().containsKey("Volume")) {
                volume = ctx.value("Volume").asNumber();
            }

            if (ctx.argMap().containsKey("Pitch")) {
                pitch = ctx.value("Pitch").asNumber();
            }

            SoundEvent snd = null;

            try {
                snd = Registry.SOUND_EVENT.get(new Identifier(sound));
            } catch (Exception err) {
                err.printStackTrace();
            }

            String jname = sound.toUpperCase().replaceAll("\\.", "_").replaceAll(" ", "_").toUpperCase();
            if (snd == null) {
                try {
                    Class<SoundEvents> clazz = SoundEvents.class;
                    Field field = clazz.getField(jname);
                    snd = (SoundEvent) field.get(null);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }

            if (snd != null) {
                io.github.techstreet.dfscript.DFScript.MC.getSoundManager().play(PositionedSoundInstance.master(snd, (float) volume, (float) pitch));
            } else {
                ChatUtil.error("Unknown sound: " + sound);

                try {
                    Class<SoundEvents> clazz = SoundEvents.class;

                    List<String> similiar = new ArrayList<>();

                    int counter = 0;
                    for (Field field : clazz.getFields()) {
                        String name = field.getName();
                        if (name.contains(jname)) {
                            similiar.add(StringUtil.toTitleCase(name.replaceAll("_", " ")));
                            counter++;
                            if (counter > 5) {
                                break;
                            }
                        }
                    }

                    if (similiar.size() > 0) {
                        ChatUtil.error("Did you mean: " + String.join(", ", similiar));
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        })),

    DISPLAY_TITLE(builder -> builder.name("Display Title")
        .description("Displays a title.")
        .icon(Items.WARPED_SIGN)
        .category(ScriptActionCategory.VISUALS)
        .arg("Title", ScriptActionArgumentType.TEXT)
        .arg("Subtitle", ScriptActionArgumentType.TEXT, b -> b.optional(true))
        .arg("Fade In", ScriptActionArgumentType.NUMBER, b -> b.optional(true))
        .arg("Stay", ScriptActionArgumentType.NUMBER, b -> b.optional(true))
        .arg("Fade Out", ScriptActionArgumentType.NUMBER, b -> b.optional(true))
        .action(ctx -> {
            String title = ctx.value("Title").asText();
            String subtitle = "";
            int fadeIn = 20;
            int stay = 60;
            int fadeOut = 20;

            if (ctx.argMap().containsKey("Subtitle")) {
                subtitle = ctx.value("Subtitle").asText();
            }

            if (ctx.argMap().containsKey("Fade In")) {
                fadeIn = (int) ctx.value("Fade In").asNumber();
            }

            if (ctx.argMap().containsKey("Stay")) {
                stay = (int) ctx.value("Stay").asNumber();
            }

            if (ctx.argMap().containsKey("Fade Out")) {
                fadeOut = (int) ctx.value("Fade Out").asNumber();
            }

            io.github.techstreet.dfscript.DFScript.MC.inGameHud.setTitle(ComponentUtil.fromString(ComponentUtil.andsToSectionSigns(title)));
            io.github.techstreet.dfscript.DFScript.MC.inGameHud.setSubtitle(ComponentUtil.fromString(ComponentUtil.andsToSectionSigns(subtitle)));
            io.github.techstreet.dfscript.DFScript.MC.inGameHud.setTitleTicks(fadeIn, stay, fadeOut);
        })),

    JOIN_LIST_TO_TEXT(builder -> builder.name("Join List to Text")
        .description("Joins a list into a single text.")
        .icon(Items.SLIME_BALL)
        .category(ScriptActionCategory.LISTS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("List", ScriptActionArgumentType.LIST)
        .arg("Separator", ScriptActionArgumentType.TEXT, b -> b.optional(true))
        .action(ctx -> {
            String separator = ", ";

            if (ctx.argMap().containsKey("Separator")) {
                separator = ctx.value("Separator").asText();
            }

            String result = ctx.value("List")
                .asList().stream()
                .map(ScriptValue::asText)
                .collect(Collectors.joining(separator));

            ctx.context().setVariable(ctx.variable("Result").name(), new ScriptTextValue(result));
        })),

    TEXT_INDEX_OF(builder -> builder.name("Index Of Text")
        .description("Gets the index of the first occurrence of a text within another text.")
        .icon(Items.FLINT)
        .category(ScriptActionCategory.TEXTS)
        .arg("Result",ScriptActionArgumentType.VARIABLE)
        .arg("Text",ScriptActionArgumentType.TEXT)
        .arg("Subtext",ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            int result = ctx.value("Text").asText().indexOf(ctx.value("Subtext").asText()) + 1;
            ctx.context().setVariable(ctx.variable("Result").name(), new ScriptNumberValue(result));
        })),

    TEXT_SUBTEXT(builder -> builder.name("Get Subtext")
        .description("Gets a piece of text within another text.")
        .icon(Items.KNOWLEDGE_BOOK)
        .category(ScriptActionCategory.TEXTS)
        .arg("Result",ScriptActionArgumentType.VARIABLE)
        .arg("Text",ScriptActionArgumentType.TEXT)
        .arg("First Index",ScriptActionArgumentType.NUMBER)
        .arg("Last Index",ScriptActionArgumentType.NUMBER)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            int start = (int)ctx.value("First Index").asNumber()-1;
            int end = (int)ctx.value("Last Index").asNumber();
            String result = text.substring(start, end);
            ctx.context().setVariable(ctx.variable("Result").name(), new ScriptTextValue(result));
        })),

    TEXT_SUBTEXT_V1(builder -> builder.name("Get Subtext OLD")
            .description("Gets a piece of text within another text.")
            .icon(Items.KNOWLEDGE_BOOK)
            .category(ScriptActionCategory.TEXTS)
            .arg("Result",ScriptActionArgumentType.VARIABLE)
            .arg("Text",ScriptActionArgumentType.TEXT)
            .arg("First Index",ScriptActionArgumentType.NUMBER)
            .arg("Last Index",ScriptActionArgumentType.NUMBER)
            .deprecate(TEXT_SUBTEXT)
            .action(ctx -> {
                String text = ctx.value("Text").asText();
                int start = (int)ctx.value("First Index").asNumber()+1;
                int end = (int)ctx.value("Last Index").asNumber();
                String result = text.substring(start, end);
                ctx.context().setVariable(ctx.variable("Result").name(), new ScriptTextValue(result));
            })),

    TEXT_LENGTH(builder -> builder.name("Get Text Length")
        .description("Get the length of a text value.")
        .icon(Items.BOOKSHELF)
        .category(ScriptActionCategory.TEXTS)
        .arg("Result",ScriptActionArgumentType.VARIABLE)
        .arg("Text",ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            ctx.context().setVariable(ctx.variable("Result").name(), new ScriptNumberValue(text.length()));
        })),
          
    READ_FILE(builder -> builder.name("Read File")
        .description("Reads a file from the scripts folder.")
        .icon(Items.WRITTEN_BOOK)
        .category(ScriptActionCategory.MISC)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Filename", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String filename = ctx.value("Filename").asText();

            if (filename.matches("^[a-zA-Z\\d_\\-\\. ]+$")) {
                Path f = FileUtil.folder("Scripts").resolve(ctx.script().getFile().getName()+"-files").resolve(filename);
                if (Files.exists(f)) {
                    try {
                        String content = FileUtil.readFile(f);
                        JsonElement json = JsonParser.parseString(content);
                        ScriptValue value = ScriptValueJson.fromJson(json);
                        ctx.context().setVariable(ctx.variable("Result").name(), value);
                    } catch (IOException e) {
                        e.printStackTrace();
                        ChatUtil.error("Internal error while reading file.");
                    }
                }
            } else {
                ChatUtil.error("Illegal filename: " + filename);
            }
        })),

    WRITE_FILE(builder -> builder.name("Write File")
        .description("Writes a file to the scripts folder.")
        .icon(Items.WRITABLE_BOOK)
        .category(ScriptActionCategory.MISC)
        .arg("Filename", ScriptActionArgumentType.TEXT)
        .arg("Content", ScriptActionArgumentType.ANY)
        .action(ctx -> {
            String filename = ctx.value("Filename").asText();
            ScriptValue value = ctx.value("Content");

            if (filename.matches("^[a-zA-Z\\d_\\-\\. ]+$")) {
                Path f = FileUtil.folder("Scripts").resolve(ctx.script().getFile().getName()+"-files").resolve(filename);
                try {
                    f.toFile().getParentFile().mkdirs();
                    FileUtil.writeFile(f, ScriptValueJson.toJson(value).toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    ChatUtil.error("Internal error while writing file.");
                }
            } else {
                ChatUtil.error("Illegal filename: " + filename);
            }
        })),

    IF_FILE_EXISTS(builder -> builder.name("If File Exists")
        .description("Executes if the specified file exists.")
        .icon(Items.BOOK)
        .category(ScriptActionCategory.MISC)
        .arg("Filename", ScriptActionArgumentType.TEXT)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            String filename = ctx.value("Filename").asText();
            if (filename.matches("^[a-zA-Z\\d_\\-\\. ]+$")) {
                Path f = FileUtil.folder("Scripts").resolve(ctx.script().getFile().getName()+"-files").resolve(filename);
                if (Files.exists(f)) {
                    ctx.setLastIfResult(true);
                }
            } else {
                ChatUtil.error("Illegal filename: " + filename);
            }
        })),

    IF_FILE_DOESNT_EXIST(builder -> builder.name("If File Doesnt Exist")
        .description("Executes if the specified file doesnt exist.")
        .icon(Items.BOOK)
        .category(ScriptActionCategory.MISC)
        .arg("Filename", ScriptActionArgumentType.TEXT)
        .hasChildren(true)
        .group(ScriptGroup.CONDITION)
        .action(ctx -> {
            String filename = ctx.value("Filename").asText();
            if (filename.matches("^[a-zA-Z\\d_\\-\\. ]+$")) {
                Path f = FileUtil.folder("Scripts").resolve(ctx.script().getFile().getName()+"-files").resolve(filename);
                if (!Files.exists(f)) {
                    ctx.setLastIfResult(true);
                }
            } else {
                ChatUtil.error("Illegal filename: " + filename);
            }
        })),

    PARSE_NUMBER(builder -> builder.name("Parse Number")
        .description("Parses a number from a text.")
        .icon(Items.ANVIL)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            try {
                ctx.context().setVariable(ctx.variable("Result").name(), new ScriptNumberValue(Double.parseDouble(text)));
            } catch (NumberFormatException e) {
                ctx.context().setVariable(ctx.variable("Result").name(), new ScriptUnknownValue());
            }
        })),

    SET_HOTBAR_ITEM(builder -> builder.name("Set Hotbar Item")
        .description("Sets a hotbar item. (Requires Creative)")
        .icon(Items.IRON_AXE)
        .category(ScriptActionCategory.ACTIONS)
        .arg("Slot", ScriptActionArgumentType.NUMBER)
        .arg("Item", ScriptActionArgumentType.DICTIONARY)
        .action(ctx -> {
            int slot = (int) ctx.value("Slot").asNumber();
            ItemStack item = ScriptValueItem.itemFromValue(ctx.value("Item"));

            if (io.github.techstreet.dfscript.DFScript.MC.interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
                io.github.techstreet.dfscript.DFScript.MC.interactionManager.clickCreativeStack(item, slot + 36);
                io.github.techstreet.dfscript.DFScript.MC.player.getInventory().setStack(slot, item);
            } else {
                ChatUtil.error("Unable to set hotbar item! (Not in creative mode)");
            }
        })),

    GIVE_ITEM(builder -> builder.name("Give Item")
        .description("Gives the player an item. (Requires Creative)")
        .icon(Items.CHEST)
        .category(ScriptActionCategory.ACTIONS)
        .arg("Item", ScriptActionArgumentType.DICTIONARY)
        .action(ctx -> {
            ItemStack item = ScriptValueItem.itemFromValue(ctx.value("Item"));

            if (io.github.techstreet.dfscript.DFScript.MC.interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
                ItemUtil.giveCreativeItem(item,true);
            } else {
                ChatUtil.error("Unable to set hotbar item! (Not in creative mode)");
            }
        })),

    DRAW_TEXT(builder -> builder.name("Draw Text")
        .description("Draws text on the screen.")
        .icon(Items.NAME_TAG)
        .category(ScriptActionCategory.VISUALS)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("X", ScriptActionArgumentType.NUMBER)
        .arg("Y", ScriptActionArgumentType.NUMBER)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            int x = (int) ctx.value("X").asNumber();
            int y = (int) ctx.value("Y").asNumber();

            if (ctx.event() instanceof HudRenderEvent event) {
                Text t = ComponentUtil.fromString(ComponentUtil.andsToSectionSigns(text));
                io.github.techstreet.dfscript.DFScript.MC.textRenderer.drawWithShadow(event.stack(),t,x,y, 0xFFFFFF);
            }
        })),

    MEASURE_TEXT(builder -> builder.name("Measure Text")
        .description("Measures the width of a text in pixels.")
        .icon(Items.STICK)
        .category(ScriptActionCategory.TEXTS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String text = ctx.value("Text").asText();
            Text t = ComponentUtil.fromString(ComponentUtil.andsToSectionSigns(text));
            int width = DFScript.MC.textRenderer.getWidth(t);
            ctx.context().setVariable(ctx.variable("Result").name(), new ScriptNumberValue(width));
        })),

    OPEN_MENU(builder -> builder.name("Open Menu")
        .description("Opens a custom empty menu.")
        .icon(Items.PAINTING)
        .category(ScriptActionCategory.MENUS)
        .arg("Width", ScriptActionArgumentType.NUMBER)
        .arg("Height", ScriptActionArgumentType.NUMBER)
        .action(ctx -> {
            int width = (int) ctx.value("Width").asNumber();
            int height = (int) ctx.value("Height").asNumber();

            io.github.techstreet.dfscript.DFScript.MC.setScreen(new ScriptMenu(width,height,ctx.script()));
        })),

    ADD_MENU_BUTTON(builder -> builder.name("Add Menu Button")
        .description("Adds a button to an open custom menu.")
        .icon(Items.CHISELED_STONE_BRICKS)
        .category(ScriptActionCategory.MENUS)
        .arg("X", ScriptActionArgumentType.NUMBER)
        .arg("Y", ScriptActionArgumentType.NUMBER)
        .arg("Width", ScriptActionArgumentType.NUMBER)
        .arg("Height", ScriptActionArgumentType.NUMBER)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Identifier", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            int x = (int) ctx.value("X").asNumber();
            int y = (int) ctx.value("Y").asNumber();
            int width = (int) ctx.value("Width").asNumber();
            int height = (int) ctx.value("Height").asNumber();
            String text = ctx.value("Text").asText();
            String identifier = ctx.value("Identifier").asText();

            if (DFScript.MC.currentScreen instanceof ScriptMenu menu) {
                if (menu.ownedBy(ctx.script())) {
                    menu.widgets.add(new ScriptMenuButton(x,y,width,height,text,identifier,ctx.script()));
                } else {
                    ChatUtil.error("Unable to add button to menu! (Not owned by script)");
                }
            } else {
                ChatUtil.error("Unable to add button to menu! (Unknown menu type)");
            }
        })),

    ADD_MENU_ITEM(builder -> builder.name("Add Menu Item")
        .description("Adds an item to an open custom menu.")
        .icon(Items.ITEM_FRAME)
        .category(ScriptActionCategory.MENUS)
        .arg("X", ScriptActionArgumentType.NUMBER)
        .arg("Y", ScriptActionArgumentType.NUMBER)
        .arg("Item", ScriptActionArgumentType.DICTIONARY)
        .arg("Identifier", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            int x = (int) ctx.value("X").asNumber();
            int y = (int) ctx.value("Y").asNumber();
            ItemStack item = ScriptValueItem.itemFromValue(ctx.value("Item"));
            String identifier = ctx.value("Identifier").asText();

            if (io.github.techstreet.dfscript.DFScript.MC.currentScreen instanceof ScriptMenu menu) {
                if (menu.ownedBy(ctx.script())) {
                    menu.widgets.add(new ScriptMenuItem(x,y,item,identifier));
                } else {
                    ChatUtil.error("Unable to add item to menu! (Not owned by script)");
                }
            } else {
                ChatUtil.error("Unable to add item to menu! (Unknown menu type)");
            }
        })),

    ADD_MENU_TEXT(builder -> builder.name("Add Menu Text")
        .description("Adds text to an open custom menu.")
        .icon(Items.WRITTEN_BOOK)
        .category(ScriptActionCategory.MENUS)
        .arg("X", ScriptActionArgumentType.NUMBER)
        .arg("Y", ScriptActionArgumentType.NUMBER)
        .arg("Text", ScriptActionArgumentType.TEXT)
        .arg("Identifier", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            int x = (int) ctx.value("X").asNumber();
            int y = (int) ctx.value("Y").asNumber();
            String rawText = ctx.value("Text").asText();
            String identifier = ctx.value("Identifier").asText();
            Text text = ComponentUtil.fromString(ComponentUtil.andsToSectionSigns(rawText));

            if (io.github.techstreet.dfscript.DFScript.MC.currentScreen instanceof ScriptMenu menu) {
                if (menu.ownedBy(ctx.script())) {
                    menu.widgets.add(new ScriptMenuText(x,y,text,0x333333, 1, false, false,identifier));
                } else {
                    ChatUtil.error("Unable to add text to menu! (Not owned by script)");
                }
            } else {
                ChatUtil.error("Unable to add text to menu! (Unknown menu type)");
            }
        })),

    ADD_MENU_TEXT_FIELD(builder -> builder.name("Add Menu Text Field")
        .description("Adds a text field to an open custom menu.")
        .icon(Items.WRITABLE_BOOK)
        .category(ScriptActionCategory.MENUS)
        .arg("X", ScriptActionArgumentType.NUMBER)
        .arg("Y", ScriptActionArgumentType.NUMBER)
        .arg("Width", ScriptActionArgumentType.NUMBER)
        .arg("Height", ScriptActionArgumentType.NUMBER)
        .arg("Identifier", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            int x = (int) ctx.value("X").asNumber();
            int y = (int) ctx.value("Y").asNumber();
            int width = (int) ctx.value("Width").asNumber();
            int height = (int) ctx.value("Height").asNumber();
            String identifier = ctx.value("Identifier").asText();

            if (io.github.techstreet.dfscript.DFScript.MC.currentScreen instanceof ScriptMenu menu) {
                if (menu.ownedBy(ctx.script())) {
                    menu.widgets.add(new ScriptMenuTextField("",x,y,width,height,true,identifier));
                } else {
                    ChatUtil.error("Unable to add text field to menu! (Not owned by script)");
                }
            } else {
                ChatUtil.error("Unable to add text field to menu! (Unknown menu type)");
            }
        })),

    REMOVE_MENU_ELEMENT(builder -> builder.name("Remove Menu Element")
        .description("Removes an element from an open custom menu.")
        .icon(Items.TNT_MINECART)
        .category(ScriptActionCategory.MENUS)
        .arg("Identifier", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String identifier = ctx.value("Identifier").asText();
            if (io.github.techstreet.dfscript.DFScript.MC.currentScreen instanceof ScriptMenu menu) {
                if (menu.ownedBy(ctx.script())) {
                    menu.removeChild(identifier);
                } else {
                    ChatUtil.error("Unable to remove element from menu! (Not owned by script)");
                }
            } else {
                ChatUtil.error("Unable to remove element from menu! (Unknown menu type)");
            }
        })),

    GET_MENU_TEXT_FIELD_VALUE(builder -> builder.name("Get Menu Text Field Value")
        .description("Gets the text inside a text field in an open custom menu.")
        .icon(Items.BOOKSHELF)
        .category(ScriptActionCategory.MENUS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Identifier", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String identifier = ctx.value("Identifier").asText();
            if (io.github.techstreet.dfscript.DFScript.MC.currentScreen instanceof ScriptMenu menu) {
                if (menu.ownedBy(ctx.script())) {
                    ScriptWidget w = menu.getWidget(identifier);

                    if (w instanceof ScriptMenuTextField field) {
                        ctx.context().setVariable(
                            ctx.variable("Result").name(),
                            new ScriptTextValue(field.getText())
                        );
                    } else {
                        ChatUtil.error("Unable to get text field value! (Unknown widget type)");
                    }
                } else {
                    ChatUtil.error("Unable to get text field value! (Not owned by script)");
                }
            } else {
                ChatUtil.error("Unable to get text field value! (Unknown menu type)");
            }
        })),

    SET_MENU_TEXT_FIELD_VALUE(builder -> builder.name("Set Menu Text Field Value")
        .description("Sets the text inside a text field in an open custom menu.")
        .icon(Items.KNOWLEDGE_BOOK)
        .category(ScriptActionCategory.MENUS)
        .arg("Identifier", ScriptActionArgumentType.TEXT)
        .arg("Value", ScriptActionArgumentType.TEXT)
        .action(ctx -> {
            String identifier = ctx.value("Identifier").asText();

            if (io.github.techstreet.dfscript.DFScript.MC.currentScreen instanceof ScriptMenu menu) {
                if (menu.ownedBy(ctx.script())) {
                    ScriptWidget w = menu.getWidget(identifier);
                    if (w instanceof ScriptMenuTextField field) {
                        field.setText(ctx.value("Value").asText());
                    } else {
                        ChatUtil.error("Unable to set text field value! (Unknown widget type)");
                    }
                } else {
                    ChatUtil.error("Unable to set text field value! (Not owned by script)");
                }
            } else {
                ChatUtil.error("Unable to set text field value! (Unknown menu type)");
            }
        })),
    
        RANDOM_INT(builder -> builder.name("Random Int")
        .description("Generates a random whole number between two other numbers.")
        .icon(Items.HOPPER)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Min", ScriptActionArgumentType.NUMBER)
        .arg("Max", ScriptActionArgumentType.NUMBER)
        .action(ctx -> {
            int min = (int) ctx.value("Min").asNumber();
            int max = (int) ctx.value("Max").asNumber();
            Random random = new Random();
            int result = random.nextInt(max + 1 - min) + min;
            ctx.context().setVariable(
                ctx.variable("Result").name(),
                new ScriptNumberValue(result)
            );
        })),

    RANDOM_DOUBLE(builder -> builder.name("Random Double")
        .description("Generates a random floating point number between two other numbers.")
        .icon(Items.HOPPER)
        .category(ScriptActionCategory.NUMBERS)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Min", ScriptActionArgumentType.NUMBER)
        .arg("Max", ScriptActionArgumentType.NUMBER)
        .action(ctx -> {
            double min = ctx.value("Min").asNumber();
            double max = ctx.value("Max").asNumber();
            double result = Math.random() * (max - min) + min;
            ctx.context().setVariable(
                ctx.variable("Result").name(),
                new ScriptNumberValue(result)
            );
        })),
        
    RANDOM_NUMBER(builder -> builder.name("Random Number")
        .description("Generates a random number between two other numbers.")
        .icon(Items.HOPPER)
        .category(ScriptActionCategory.NUMBERS)
        .deprecate(RANDOM_DOUBLE)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("Min", ScriptActionArgumentType.NUMBER)
        .arg("Max", ScriptActionArgumentType.NUMBER)
        .action(ctx -> {
            double min = ctx.value("Min").asNumber();
            double max = ctx.value("Max").asNumber();
            double result = Math.random() * (max - min) + min;
            ctx.context().setVariable(
                ctx.variable("Result").name(),
                new ScriptNumberValue(result)
            );
        })),

    REPEAT_FOREVER(builder -> builder.name("RepeatForever")
            .description("Repeats for eternity.\nMake sure to have a Stop Repetition, Stop Codeline or Wait somewhere in the code!\nThere's a lagslayer for the repetition actions.\nIt activates after 100000 iterations with no Wait.")
            .icon(Items.GOLD_INGOT)
            .category(ScriptActionCategory.MISC)
            .hasChildren(true)
            .group(ScriptGroup.REPETITION)
            .action(ctx -> {
                ctx.scheduleInner(null, context -> context.setLastIfResult(true));
            })),
    ELSE(builder -> builder.name("Else")
        .description("Executes if the last IF condition failed.\nAnd ELSE also works as a valid IF condition for ELSE.")
        .icon(Items.END_STONE)
        .category(ScriptActionCategory.MISC)
        .group(ScriptGroup.CONDITION)
        .hasChildren(true)
        .action(ctx -> {
            ctx.setLastIfResult(!ctx.lastIfResult());
    })),

    SORT_LIST(builder -> builder.name("Sort List")
        .description("Sorts a list in ascending order.")
        .icon(Items.REPEATING_COMMAND_BLOCK)
        .arg("Result", ScriptActionArgumentType.VARIABLE)
        .arg("List", ScriptActionArgumentType.LIST, b -> b.optional(true))
        .category(ScriptActionCategory.LISTS)
        .action(ctx -> {
            List<ScriptValue> list = null;

            if(ctx.argMap().containsKey("List"))
            {
                list = ctx.value("List").asList();
            }
            else
            {
                list = ctx.value("Result").asList();
            }

            list.sort(new ScriptValueComparator());

            ctx.context().setVariable(ctx.variable("Result").name(), new ScriptListValue(list));
    })),

    REPLACE_TEXT(builder -> builder.name("Replace Text")
            .description("Searches for part of a text and replaces it.")
            .icon(Items.LEAD)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Text to change", ScriptActionArgumentType.TEXT)
            .arg("Text part to replace", ScriptActionArgumentType.TEXT)
            .arg("Replacement", ScriptActionArgumentType.TEXT)
            .category(ScriptActionCategory.TEXTS)
            .action(ctx -> {
                String result = ctx.value("Text to change").asText();

                result = result.replace(ctx.value("Text part to replace").asText(), ctx.value("Replacement").asText());

                ctx.context().setVariable(ctx.variable("Result").name(), new ScriptTextValue(result));
    })),

    REGEX_REPLACE_TEXT(builder -> builder.name("Replace Text using Regex")
            .description("Searches for part of a text\nusing a regex and replaces it.")
            .icon(Items.LEAD, true)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Text to change", ScriptActionArgumentType.TEXT)
            .arg("Regex", ScriptActionArgumentType.TEXT)
            .arg("Replacement", ScriptActionArgumentType.TEXT)
            .category(ScriptActionCategory.TEXTS)
            .action(ctx -> {
                String result = ctx.value("Text to change").asText();

                result = result.replaceAll(ctx.value("Regex").asText(), ctx.value("Replacement").asText());

                ctx.context().setVariable(ctx.variable("Result").name(), new ScriptTextValue(result));
    })),

    REMOVE_TEXT(builder -> builder.name("Remove Text")
            .description("Searches for part of a text and removes it.")
            .icon(Items.WRITABLE_BOOK)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Text to change", ScriptActionArgumentType.TEXT)
            .arg("Text to remove", ScriptActionArgumentType.TEXT, b -> b.plural(true))
            .category(ScriptActionCategory.TEXTS)
            .action(ctx -> {
                String result = ctx.value("Text to change").asText();

                List<ScriptValue> textsToRemove = ctx.pluralValue("Text to remove");

                for(int i = 0; i < textsToRemove.size(); i++) {
                    result = result.replace(textsToRemove.get(i).asText(), "");
                }

                ctx.context().setVariable(ctx.variable("Result").name(), new ScriptTextValue(result));
    })),

    STRIP_COLOR(builder -> builder.name("Strip Color from Text")
            .description("Searches for color codes in a text and removes them.")
            .icon(Items.CYAN_DYE)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Text", ScriptActionArgumentType.TEXT, b -> b.optional(true))
            .category(ScriptActionCategory.TEXTS)
            .action(ctx -> {
                String result = null;

                if (ctx.argMap().containsKey("Text")) {
                    result = ctx.value("Text").asText();
                } else {
                    result = ctx.value("Result").asText();
                }

                result = result.replaceAll("&x(&[0-9a-fA-F]){6}", "");
                result = result.replaceAll("&[0-9a-fA-FlonmkrLONMKR]", "");

                ctx.context().setVariable(ctx.variable("Result").name(), new ScriptTextValue(result));
    })),

    REPEAT_TEXT(builder -> builder.name("Repeat Text")
            .description("Repeats a text the given number of times.")
            .icon(Items.REPEATING_COMMAND_BLOCK)
            .arg("Result", ScriptActionArgumentType.VARIABLE)
            .arg("Text to repeat", ScriptActionArgumentType.TEXT)
            .arg("Times to repeat", ScriptActionArgumentType.NUMBER)
            .category(ScriptActionCategory.TEXTS)
            .action(ctx -> {
                String result = "";
                String input = ctx.value("Text to repeat").asText();
                int times = (int) ctx.value("Times to repeat").asNumber();

                for(int i = 0; i < times; i++)
                {
                    result += input;
                }

                ctx.context().setVariable(ctx.variable("Result").name(), new ScriptTextValue(result));
    }));

    private Consumer<ScriptActionContext> action = (ctx) -> {
    };

    private boolean glow = false;
    private Item icon = Items.STONE;
    private String name = "Unnamed Action";
    private boolean hasChildren = false;
    private ScriptActionCategory category = ScriptActionCategory.MISC;
    private List<String> description = new ArrayList();
    private ScriptGroup group = ScriptGroup.ACTION;

    private ScriptActionType deprecated = null; //if deprecated == null, the action is not deprecated
    private final List<ScriptActionArgument> arguments = new ArrayList<>();
    ScriptActionType(Consumer<ScriptActionType> builder) {
        description.add("No description provided.");
        builder.accept(this);
    }
    public ItemStack getIcon() {
        ItemStack item = new ItemStack(icon);

        item.setCustomName(Text.literal(name)
            .fillStyle(Style.EMPTY
                .withColor(Formatting.WHITE)
                .withItalic(false)));

        NbtList lore = new NbtList();

        if(isDeprecated())
        {
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal("This action is deprecated!")
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.RED)
                            .withItalic(false)))));
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal("Use '" + deprecated.getName() + "'")
                    .fillStyle(Style.EMPTY
                            .withColor(Formatting.RED)
                            .withItalic(false)))));
        }

        for (String descriptionLine: description) {
            lore.add(NbtString.of(Text.Serializer.toJson(Text.literal(descriptionLine)
                .fillStyle(Style.EMPTY
                      .withColor(Formatting.GRAY)
                      .withItalic(false)))));
        }

        lore.add(NbtString.of(Text.Serializer.toJson(Text.literal(""))));

        for (ScriptActionArgument arg : arguments) {
            lore.add(NbtString.of(Text.Serializer.toJson(arg.text())));
        }

        item.getSubNbt("display")
            .put("Lore", lore);

        if(glow)
        {
            item.addEnchantment(Enchantments.UNBREAKING, 1);
            item.addHideFlag(ItemStack.TooltipSection.ENCHANTMENTS);
        }

        return item;
    }
    public String getName() {
        return name;
    }

    public boolean isDeprecated() {
        return deprecated != null;
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public ScriptActionCategory getCategory() {
        return category;
    }

    private ScriptActionType action(Consumer<ScriptActionContext> action) {
        this.action = action;
        return this;
    }

    private ScriptActionType icon(Item icon, boolean glow) {
        this.icon = icon;
        this.glow = glow;
        return this;
    }

    private ScriptActionType icon(Item icon) {
        icon(icon, false);
        return this;
    }

    private ScriptActionType name(String name) {
        this.name = name;
        return this;
    }

    private ScriptActionType hasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
        return this;
    }

    private ScriptActionType category(ScriptActionCategory category) {
        this.category = category;
        return this;
    }

    private ScriptActionType description(String description) {
        this.description.clear();
        this.description.addAll(Arrays.asList(description.split("\n", -1)));
        return this;
    }

    private ScriptActionType group(ScriptGroup group) {
        this.group = group;
        return this;
    }

    public ScriptGroup getGroup() {
        return group;
    }

    public ScriptActionType arg(String name, ScriptActionArgumentType type, Consumer<ScriptActionArgument> builder) {
        ScriptActionArgument arg = new ScriptActionArgument(name, type);
        builder.accept(arg);
        arguments.add(arg);
        return this;
    }

    public ScriptActionType arg(String name, ScriptActionArgumentType type) {
        return arg(name, type, (arg) -> {
        });
    }

    public ScriptActionType deprecate(ScriptActionType newScriptActionType) {
        deprecated = newScriptActionType;

        return this;
    }

    public void run(ScriptActionContext ctx) {
        List<List<ScriptActionArgument>> possibilities = new ArrayList<>();

        generatePossibilities(possibilities, new ArrayList<>(), arguments, 0);

        search:
        for (List<ScriptActionArgument> possibility : possibilities) {
            int pos = 0;
            ctx.argMap().clear();
            for (ScriptActionArgument arg : possibility) {
                List<ScriptArgument> args = new ArrayList<>();
                if (pos >= ctx.arguments().size()) {
                    continue search;
                }
                if (ctx.arguments().get(pos).convertableTo(arg.type())) {
                    args.add(ctx.arguments().get(pos));
                    pos++;
                }
                if (arg.plural()) {
                    while (pos < ctx.arguments().size()) {
                        if (ctx.arguments().get(pos).convertableTo(arg.type())) {
                            args.add(ctx.arguments().get(pos));
                            pos++;
                        } else {
                            break;
                        }
                    }
                }
                ctx.setArg(arg.name(), args);
            }
            if (pos == ctx.arguments().size()) {
                action.accept(ctx);
                return;
            }
        }

        ChatUtil.error("Invalid arguments for " + name + ".");
    }

    private void generatePossibilities(List<List<ScriptActionArgument>> possibilities, ArrayList<ScriptActionArgument> current, List<ScriptActionArgument> arguments, int pos) {
        if (pos >= arguments.size()) {
            possibilities.add(new ArrayList<>(current));
            return;
        }

        ScriptActionArgument arg = arguments.get(pos);
        if (arg.optional()) {
            generatePossibilities(possibilities, new ArrayList<>(current), arguments, pos + 1);
        }
        current.add(arg);
        generatePossibilities(possibilities, current, arguments, pos + 1);
    }
}
