package io.github.techstreet.dfscript.mixin.game;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.event.*;
import io.github.techstreet.dfscript.event.system.EventManager;
import io.github.techstreet.dfscript.util.hypercube.HypercubeRank;
import io.github.techstreet.dfscript.util.hypercube.HypercubeUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Mixin(ClientPlayNetworkHandler.class)
public class MClientPlayNetworkHandler {

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        if (!RenderSystem.isOnRenderThread()) {
            return;
        }

        if (packet.getType() == MessageType.CHAT || packet.getType() == MessageType.SYSTEM) {
            ReceiveChatEvent event = new ReceiveChatEvent(packet.getMessage());
            EventManager.getInstance().dispatch(event);
            if (event.isCancelled()) {
                ci.cancel();
            }

            String packetString = packet.getMessage().getString();

            if (packetString.equals("» You are now in dev mode.")) {
                DevModeEvent modeEvent = new DevModeEvent();
                EventManager.getInstance().dispatch(modeEvent);
            }

            if (packetString.equals("» You are now in build mode.")) {
                BuildModeEvent modeEvent = new BuildModeEvent();
                EventManager.getInstance().dispatch(modeEvent);
            }

            if (packetString.startsWith("» Joined plot") || packetString.startsWith("» Joined game")) {
                PlayModeEvent modeEvent = new PlayModeEvent();
                EventManager.getInstance().dispatch(modeEvent);
            }
        }
    }

    @Inject(method = "onTeam", at = @At("RETURN"))
    private void handleSetPlayerTeamPacket(TeamS2CPacket packet, CallbackInfo ci) {
        if (DFScript.MC.player != null) {
            if (io.github.techstreet.dfscript.DFScript.MC.getCurrentServerEntry() != null) {
                if (io.github.techstreet.dfscript.DFScript.MC.getCurrentServerEntry().address.contains("mcdiamondfire.com")) {
                    if (packet.getPlayerNames().contains(io.github.techstreet.dfscript.DFScript.MC.player.getName().getString())) {
                        for (HypercubeRank r : HypercubeRank.values()) {
                            if (r.getTeamName() == null)
                                continue;

                            if (packet.getTeamName().endsWith(r.getTeamName())) {
                                HypercubeUtil.setRank(r);
                                return;
                            }
                        }

                        HypercubeUtil.setRank(HypercubeRank.DEFAULT);
                    }
                }
            }
        }
    }

    @Inject(method = "onGameJoin", at = @At("RETURN"), cancellable = true)
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        ClientConnection conn = io.github.techstreet.dfscript.DFScript.MC.getNetworkHandler().getConnection();
        ServerJoinEvent event = new ServerJoinEvent(packet, ((InetSocketAddress)conn.getAddress()));
        EventManager.getInstance().dispatch(event);
    }

    @Inject(method = "onDisconnect", at = @At("RETURN"), cancellable = true)
    private void onDisconnect(DisconnectS2CPacket packet, CallbackInfo ci) {
        ServerLeaveEvent event = new ServerLeaveEvent(packet);
        EventManager.getInstance().dispatch(event);
    }

    @Inject(method = "onPlaySound", at = @At("HEAD"), cancellable = true)
    private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        RecieveSoundEvent event = new RecieveSoundEvent(packet);
        EventManager.getInstance().dispatch(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
