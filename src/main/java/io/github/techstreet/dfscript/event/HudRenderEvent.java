package io.github.techstreet.dfscript.event;

import io.github.techstreet.dfscript.event.system.Event;
import net.minecraft.client.util.math.MatrixStack;

public record HudRenderEvent(MatrixStack stack) implements Event {

}
