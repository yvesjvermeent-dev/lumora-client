package net.lumora.module;

import net.lumora.core.Category;
import net.minecraft.client.gui.DrawContext;

public class MistModule extends OverlayModule {
    public MistModule() { super("mist", "Mist", "Soft lilac mist that hangs low", false, Category.RENDER, "cloud"); }

    @Override
    protected void paint(DrawContext c, int w, int h, float t) {
        int a = (int) (50 + 18 * Math.sin(t * 0.5));
        c.fillGradient(0, h * 45 / 100, w, h, 0x00E6D9FF, (a << 24) | 0xE6D9FF);
    }
}
