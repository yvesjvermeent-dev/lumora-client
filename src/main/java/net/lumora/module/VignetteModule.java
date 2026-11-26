package net.lumora.module;

import net.lumora.core.Category;
import net.lumora.core.Theme;
import net.minecraft.client.gui.DrawContext;

public class VignetteModule extends OverlayModule {
    public VignetteModule() { super("vignette", "Glow Edges", "Soft coloured light around the screen edge", false, Category.RENDER, "drop"); }

    @Override
    protected void paint(DrawContext c, int w, int h, float t) {
        int acc = Theme.current().accent();
        float pulse = 0.85f + 0.15f * (float) Math.sin(t * 1.2);
        int n = 36;
        for (int i = 0; i < n; i++) {
            int col = Theme.alpha(acc, 0.22f * pulse * (1 - i / (float) n));
            c.fill(i, 0, i + 1, h, col);
            c.fill(w - i - 1, 0, w - i, h, col);
            c.fill(0, i, w, i + 1, col);
            c.fill(0, h - i - 1, w, h - i, col);
        }
    }
}
