package net.lumora.module;

import net.lumora.core.Category;
import net.lumora.core.Theme;
import net.minecraft.client.gui.DrawContext;

public class HazeModule extends OverlayModule {
    public HazeModule() { super("haze", "Haze", "A gentle wash of colour over the view", false, Category.RENDER, "sun"); }

    @Override
    protected void paint(DrawContext c, int w, int h, float t) {
        int acc = Theme.current().accent();
        c.fill(0, 0, w, h, Theme.alpha(acc, 0.06f));
        c.fillGradient(0, 0, w, h / 2, Theme.alpha(acc, 0.12f), Theme.alpha(acc, 0.0f) & 0x00FFFFFF);
    }
}
