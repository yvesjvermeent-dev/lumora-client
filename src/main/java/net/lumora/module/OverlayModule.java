package net.lumora.module;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.lumora.core.Category;
import net.lumora.core.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/** Base for modules that paint a translucent layer over the view, under the rest of the HUD. */
public abstract class OverlayModule extends Module {
    protected OverlayModule(String id, String name, String desc, boolean on, Category cat, String icon) {
        super(id, name, desc, on, cat, icon);
    }

    protected abstract void paint(DrawContext c, int w, int h, float t);
    protected boolean active() { return enabled(); }

    @Override
    public void init() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS,
            Identifier.of("lumora", "overlay_" + id), (ctx, tick) -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (!active() || mc.player == null || mc.options.hudHidden) return;
                paint(ctx, ctx.getScaledWindowWidth(), ctx.getScaledWindowHeight(), System.currentTimeMillis() / 1000f);
            });
    }
}
