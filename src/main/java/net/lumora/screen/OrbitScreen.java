package net.lumora.screen;

import net.lumora.core.Category;
import net.lumora.core.Theme;
import net.lumora.gfx.Gfx;
import net.lumora.screen.widget.CardButton;
import net.lumora.screen.widget.CardButton.Style;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/** The round hub: categories orbit on an ellipse, the selected one sits in front. */
public class OrbitScreen extends LumoraScreen {
    private final Screen parent;
    private int sel = 0;
    private float rot = 0f;
    private final List<CardButton> nodes = new ArrayList<>();
    private CardButton center;

    public OrbitScreen(Screen parent) {
        super(Text.literal("Lumora"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        nodes.clear();
        Category[] cs = Category.values();
        center = new CardButton(Style.GHOST, 0, 0, 70, 70, "open", () -> "", "", "", () -> false,
            () -> client.setScreen(new PanelScreen(cs[sel], this)));
        addDrawableChild(center);
        for (int i = 0; i < cs.length; i++) {
            final int idx = i;
            CardButton n = new CardButton(Style.NODE, 0, 0, 36, 36, cs[i].title, () -> "", cs[i].icon, "",
                () -> sel == idx, () -> { if (sel == idx) client.setScreen(new PanelScreen(cs[idx], this)); else sel = idx; });
            addDrawableChild(n);
            nodes.add(n);
        }
    }

    @Override
    protected void renderContent(DrawContext ctx, int mx, int my, float delta) {
        Theme t = Theme.current();
        Category[] cs = Category.values();
        int n = cs.length;
        float f = fade(), tm = time();
        float d = (((sel - rot) % n) + n) % n;
        if (d > n / 2f) d -= n;
        rot += d * 0.18f;
        if (rot < 0) rot += n;
        if (rot >= n) rot -= n;

        int cx = width / 2;
        int frontY = (int) (height * 0.60f);
        int ry = (int) (height * 0.10f), rx = (int) (width * 0.30f);
        int cyE = frontY - ry;
        int emblemY = (int) (height * 0.27f) + (int) (Math.sin(tm * 1.5) * 2);

        Gfx.textCentered(ctx, "L U M O R A", cx, 10, Theme.alpha(0xFFFFFFFF, 0.45f * f));

        for (int k = 0; k < 140; k++) {                       // orbit path
            double a = Math.PI * 2 * k / 140;
            float depth = (float) (Math.sin(a) + 1) / 2;
            int px = (int) (cx + Math.cos(a) * rx), py = (int) (cyE + Math.sin(a) * ry);
            ctx.fill(px, py, px + 2, py + 1, Theme.alpha(t.accent(), (0.10f + 0.30f * depth) * f));
        }
        Gfx.emblem(ctx, cx, emblemY, 22, tm, t.accent(), f);
        center.setX(cx - 35); center.setY(emblemY - 35);

        for (int i = 0; i < n; i++) {
            double a = Math.PI / 2 + (i - rot) * Math.PI * 2 / n;
            float depth = (float) (Math.sin(a) + 1) / 2;
            CardButton node = nodes.get(i);
            node.setX((int) (cx + Math.cos(a) * rx) - 18);
            node.setY((int) (cyE + Math.sin(a) * ry) - 18);
            node.nodeScale = 0.62f + 0.38f * depth;
            node.anim = f;
        }

        Category c = cs[sel];
        Gfx.scaledBold(ctx, c.title, cx, frontY + 28, 2f, Theme.alpha(0xFFFFFFFF, f), true);
        Gfx.textCentered(ctx, c.tagline, cx, frontY + 48, Theme.alpha(0xFFB9B0D0, f));
    }

    @Override
    protected void renderOverlay(DrawContext ctx, int mx, int my, float delta) {
        String h = "Click to open  -  Scroll to rotate  -  Esc to close";
        Gfx.textCentered(ctx, h, width / 2, height - 12, 0x70FFFFFF);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmount, double vAmount) {
        int n = Category.values().length;
        sel = Math.floorMod(sel - (int) Math.signum(vAmount), n);
        return true;
    }

    @Override public void close() { client.setScreen(parent); }
    @Override public void removed() { net.lumora.Lumora.config().save(); }
}
