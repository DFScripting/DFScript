package io.github.techstreet.dfscript.commands.arguments.serializers;

import com.google.gson.JsonObject;
import io.github.techstreet.dfscript.commands.arguments.StringFuncArgumentFunctions;
import io.github.techstreet.dfscript.commands.arguments.StringFuncArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class StringFuncArgumentSerializer implements ArgumentSerializer<StringFuncArgumentType, StringFuncArgumentSerializer.StringFuncArgumentProperties> {

    @Override
    public void writePacket(StringFuncArgumentProperties properties, PacketByteBuf buf) {
        buf.writeBoolean(properties.greedy);
        buf.writeEnumConstant(properties.func);
    }

    @Override
    public StringFuncArgumentProperties fromPacket(PacketByteBuf buf) {
        boolean greedy = buf.readBoolean();
        StringFuncArgumentFunctions func = buf.readEnumConstant(StringFuncArgumentFunctions.class);

        return new StringFuncArgumentProperties(greedy, func);
    }

    @Override
    public void writeJson(StringFuncArgumentProperties properties, JsonObject json) {
        json.addProperty("greedy", properties.greedy);

        json.addProperty("function", properties.func.name());
    }

    @Override
    public StringFuncArgumentProperties getArgumentTypeProperties(StringFuncArgumentType argumentType) {
        return new StringFuncArgumentProperties(argumentType.isGreedy(), argumentType.getFunction());
    }

    public final class StringFuncArgumentProperties implements ArgumentTypeProperties<StringFuncArgumentType> {

        boolean greedy;
        StringFuncArgumentFunctions func;

        public StringFuncArgumentProperties(boolean greedy, StringFuncArgumentFunctions func) {
            this.greedy = greedy;
            this.func = func;
        }

        @Override
        public StringFuncArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return new StringFuncArgumentType(func, greedy);
        }

        @Override
        public ArgumentSerializer<StringFuncArgumentType, ?> getSerializer() {
            return StringFuncArgumentSerializer.this;
        }
    }
}
