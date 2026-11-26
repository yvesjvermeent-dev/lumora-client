package net.lumora.module;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.lumora.core.Category;
import net.lumora.core.Module;
import net.lumora.core.Theme;
import net.lumora.gfx.Gfx;
import net.lumora.gfx.Icons;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Rounded glass pills: logo + FPS, coordinates, clock. Display only. */
public class HudModule extends Module {
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm");

    public HudModule() { super("hud", "Themed HUD", "Glass pills for FPS, position and time", true, Category.INTERFACE, "gear"); }

    @Override
    public void init() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS,
            Identifier.of("lumora", "info_panel"), (ctx, tick) -> {
                if (!enabled()) return;
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.player == null || mc.options.hudHidden) return;
                draw(ctx, mc);
            });
    }

    private void draw(DrawContext c, MinecraftClient mc) {
        int acc = Theme.current().accent();
        String fps = mc.getCurrentFps() + " FPS";
        int bw = Gfx.boldWidth("LUMORA");
        int w1 = 22 + bw + 10 + mc.textRenderer.getWidth(fps) + 10;
        int x = 8, y = 8, h = 18;
        Gfx.pill(c, x, y, w1, h);
        Gfx.circle(c, x + 10, y + 9, 6, acc);
        Icons.draw(c, "sparkle", x + 7, y + 6, 1, 0xFFFFFFFF);
        Gfx.bold(c, "LUMORA", x + 20, y + 5, acc);
        c.fill(x + 20 + bw + 5, y + 5, x + 20 + bw + 6, y + 13, 0x40FFFFFF);
        Gfx.text(c, fps, x + 20 + bw + 10, y + 5, 0xFFE8E8F0);

        String xyz = mc.player.getBlockX() + "  " + mc.player.getBlockY() + "  " + mc.player.getBlockZ();
        int w2 = 10 + mc.textRenderer.getWidth("XYZ") + 8 + mc.textRenderer.getWidth(xyz) + 10;
        Gfx.pill(c, x, y + 22, w2, h);
        Gfx.text(c, "XYZ", x + 10, y + 27, acc);
        Gfx.text(c, xyz, x + 10 + mc.textRenderer.getWidth("XYZ") + 8, y + 27, 0xFFE8E8F0);

        String time = LocalTime.now().format(CLOCK);
        int w3 = mc.textRenderer.getWidth(time) + 20;
        Gfx.pill(c, c.getScaledWindowWidth() - w3 - 8, y, w3, h);
        Gfx.text(c, time, c.getScaledWindowWidth() - w3 - 8 + 10, y + 5, 0xFFE8E8F0);
    }
}
