package net.lumora.screen;

import net.lumora.gfx.Gfx;
import net.lumora.core.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

/**
 * Shared base: themed background (cosmic scene on the menu, tinted blurred world in-game),
 * a fade-in, and a render order of background -> content -> widgets -> overlay.
 */
public abstract class LumoraScreen extends Screen {
    private long opened;
    private boolean bgDone;

    protected LumoraScreen(Text title) { super(title); }

    @Override
    protected void init() { opened = Util.getMeasuringTimeMs(); }

    protected float elapsed() { return Util.getMeasuringTimeMs() - opened; }

    protected static float ease(float p) {
        p = Math.max(0f, Math.min(1f, p));
        return 1f - (1f - p) * (1f - p) * (1f - p);
    }

    protected float fade() { return ease(elapsed() / 350f); }

    protected float time() { return Util.getMeasuringTimeMs() / 1000f; }

    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        if (bgDone) return;            // vanilla blur may only be applied once per frame
        bgDone = true;
        Theme t = Theme.current();
        if (client != null && client.world != null) {
            super.renderBackground(ctx, mx, my, delta);
            ctx.fillGradient(0, 0, width, height, Theme.alpha(t.top(), 0.55f), Theme.alpha(t.bottom(), 0.78f));
        } else {
            Gfx.cosmic(ctx, 0, 0, width, height, time(), t.top(), t.bottom(), t.accent());
        }
    }

    protected void renderContent(DrawContext ctx, int mx, int my, float delta) { }
    protected void renderOverlay(DrawContext ctx, int mx, int my, float delta) { }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        bgDone = false;
        renderBackground(ctx, mx, my, delta);
        renderContent(ctx, mx, my, delta);
        super.render(ctx, mx, my, delta);
        renderOverlay(ctx, mx, my, delta);
    }
}
