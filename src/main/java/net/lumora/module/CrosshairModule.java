package net.lumora.module;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.lumora.Lumora;
import net.lumora.core.Category;
import net.lumora.core.Module;
import net.lumora.core.Theme;
import net.lumora.gfx.Gfx;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class CrosshairModule extends Module {
    public static final String[] STYLES = {"Dot", "Gap Cross", "Square", "Diamond", "Ring", "Dot Ring"};

    public CrosshairModule() { super("crosshair", "Custom Crosshair", "Six designs in your theme colour", true, Category.INTERFACE, "ring"); }

    @Override
    public void init() {
        HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, original -> (ctx, tick) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (!enabled() || !mc.options.getPerspective().isFirstPerson()) {
                original.render(ctx, tick);
                return;
            }
            draw(ctx);
        });
    }

    private void draw(DrawContext ctx) {
        int cx = ctx.getScaledWindowWidth() / 2, cy = ctx.getScaledWindowHeight() / 2;
        int c = Theme.current().accent(), sh = 0x88000000;
        switch (Math.floorMod(Lumora.config().crosshairStyle, STYLES.length)) {
            case 0 -> { Gfx.circle(ctx, cx, cy, 2, sh); Gfx.circle(ctx, cx, cy, 1, c); }
            case 1 -> {
                int g = 3, l = 5;
                ctx.fill(cx - g - l, cy - 1, cx - g, cy + 1, c);
                ctx.fill(cx + g, cy - 1, cx + g + l, cy + 1, c);
                ctx.fill(cx - 1, cy - g - l, cx + 1, cy - g, c);
                ctx.fill(cx - 1, cy + g, cx + 1, cy + g + l, c);
            }
            case 2 -> {
                int r = 5;
                ctx.fill(cx - r, cy - r, cx + r, cy - r + 1, c);
                ctx.fill(cx - r, cy + r - 1, cx + r, cy + r, c);
                ctx.fill(cx - r, cy - r, cx - r + 1, cy + r, c);
                ctx.fill(cx + r - 1, cy - r, cx + r, cy + r, c);
            }
            case 3 -> {
                for (int i = 0; i <= 5; i++) {
                    ctx.fill(cx - i, cy - 5 + i, cx - i + 1, cy - 4 + i, c);
                    ctx.fill(cx + i, cy - 5 + i, cx + i + 1, cy - 4 + i, c);
                    ctx.fill(cx - i, cy + 5 - i, cx - i + 1, cy + 6 - i, c);
                    ctx.fill(cx + i, cy + 5 - i, cx + i + 1, cy + 6 - i, c);
                }
            }
            case 4 -> { Gfx.ring(ctx, cx, cy, 6, 1, sh); Gfx.ring(ctx, cx, cy, 5, 1, c); }
            default -> { Gfx.ring(ctx, cx, cy, 6, 1, c); Gfx.circle(ctx, cx, cy, 1, c); }
        }
    }
}
