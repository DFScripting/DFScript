package io.github.techstreet.dfscript.event;

import io.github.techstreet.dfscript.event.system.Event;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;

public record ServerLeaveEvent(DisconnectS2CPacket packet) implements Event {
}
