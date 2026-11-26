package net.lumora.module;

import net.lumora.core.Category;
import net.lumora.core.Theme;
import net.minecraft.client.gui.DrawContext;

public class HotbarGlowModule extends OverlayModule {
    public HotbarGlowModule() { super("hotbar", "Hotbar Glow", "A soft light line above your hotbar", true, Category.INTERFACE, "star"); }

    @Override
    protected void paint(DrawContext c, int w, int h, float t) {
        int acc = Theme.current().accent();
        int x0 = w / 2 - 91, y = h - 24;
        for (int i = 0; i < 182; i++) {
            float edge = 1f - Math.abs(i - 91) / 91f;
            c.fill(x0 + i, y, x0 + i + 1, y + 1, Theme.alpha(acc, 0.9f * edge));
            c.fillGradient(x0 + i, y - 6, x0 + i + 1, y, acc & 0xFFFFFF, Theme.alpha(acc, 0.18f * edge));
        }
    }
}
