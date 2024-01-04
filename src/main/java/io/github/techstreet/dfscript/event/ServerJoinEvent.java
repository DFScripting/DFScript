package io.github.techstreet.dfscript.event;

import io.github.techstreet.dfscript.event.system.Event;
import java.net.InetSocketAddress;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;

public record ServerJoinEvent(GameJoinS2CPacket packet, InetSocketAddress address) implements Event {
}
