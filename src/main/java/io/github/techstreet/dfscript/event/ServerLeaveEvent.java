package io.github.techstreet.dfscript.event;

import io.github.techstreet.dfscript.event.system.Event;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;

public class ServerLeaveEvent implements Event {
    private final DisconnectS2CPacket packet;

    public ServerLeaveEvent(DisconnectS2CPacket packet) { this.packet = packet; }

    public DisconnectS2CPacket getPacket() { return this.packet; }
}
