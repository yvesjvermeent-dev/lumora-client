package net.lumora.module;

import net.lumora.Lumora;
import net.lumora.core.Category;
import net.lumora.core.Theme;
import net.lumora.gfx.Gfx;
import net.minecraft.client.gui.DrawContext;

/** Sky tint presets (Opaline, Nocturne, Galaxy). A coloured overlay on the upper view, not a real sky replacement. */
public class SkyModule extends OverlayModule {
    public SkyModule() {
        super("sky", "Sky", "Sky tint presets", true, Category.WORLD, "globe");
        hidden = true;
    }

    @Override protected boolean active() { return Lumora.config().sky > 0; }

    @Override
    protected void paint(DrawContext c, int w, int h, float t) {
        int s = Lumora.config().sky;
        int col = switch (s) { case 1 -> 0x8FE3E0; case 2 -> 0x8C5CFF; default -> 0xB45CFF; };
        c.fillGradient(0, 0, w, h * 60 / 100, Theme.alpha(0xFF000000 | col, 0.30f), col & 0xFFFFFF);
        if (s == 3) {
            Gfx.aurora(c, 0, 0, w, h * 45 / 100, t, col);
            for (int i = 0; i < 50; i++) {
                long hs = (i * 2654435761L) & 0xFFFFFFFFL;
                int sx = (int) (hs % w), sy = (int) ((hs >>> 5) % (h * 40 / 100));
                float tw = 0.3f + 0.7f * (float) Math.abs(Math.sin(t + i));
                c.fill(sx, sy, sx + 1, sy + 1, Theme.alpha(0xFFFFFFFF, tw * 0.7f));
            }
        }
    }
}
