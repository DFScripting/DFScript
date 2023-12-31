package io.github.techstreet.dfscript.mixin.game;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.event.*;
import io.github.techstreet.dfscript.event.system.EventManager;
import io.github.techstreet.dfscript.util.hypercube.HypercubeRank;
import io.github.techstreet.dfscript.util.hypercube.HypercubeUtil;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonNetworkHandler.class)
public class MClientCommonNetworkHandler {
    @Inject(method = "onDisconnect", at = @At("RETURN"), cancellable = true)
    private void onDisconnect(DisconnectS2CPacket packet, CallbackInfo ci) {
        ServerLeaveEvent event = new ServerLeaveEvent(packet);
        EventManager.getInstance().dispatch(event);
    }
}
