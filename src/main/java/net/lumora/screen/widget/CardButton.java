package net.lumora.screen.widget;

import net.lumora.core.Theme;
import net.lumora.gfx.Art;
import net.lumora.gfx.Gfx;
import net.lumora.gfx.Icons;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/** One custom-drawn, rounded, animated widget used for cards, tabs, buttons and orbit nodes. */
public class CardButton extends ButtonWidget {
    public enum Style { COMPACT, ART, PILL, GLASS, PRIMARY, NODE, GHOST }

    public float anim = 1f;          // entrance 0..1
    public float nodeScale = 1f;     // orbit depth scale
    public String badge = "ON";
    public boolean showSwitch = true, iconOnly = false;

    private final Style style;
    private final String title, icon, art;
    private final Supplier<String> desc;
    private final BooleanSupplier on;
    private float hover, state;

    public CardButton(Style style, int x, int y, int w, int h, String title, Supplier<String> desc,
                      String icon, String art, BooleanSupplier on, Runnable action) {
        super(x, y, w, h, Text.literal(title), b -> action.run(), DEFAULT_NARRATION_SUPPLIER);
        this.style = style; this.title = title; this.desc = desc; this.icon = icon; this.art = art; this.on = on;
    }

    @Override
    public void renderWidget(DrawContext c, int mx, int my, float delta) {
        if (style == Style.GHOST) return;
        float a = anim;
        if (a <= 0.01f) return;
        boolean hov = isHovered() && active;
        hover += ((hov ? 1f : 0f) - hover) * 0.25f;
        boolean isOn = on.getAsBoolean();
        state += ((isOn ? 1f : 0f) - state) * 0.25f;
        switch (style) {
            case COMPACT -> compact(c, a);
            case ART -> artCard(c, a);
            case PILL -> pill(c, a, isOn);
            case GLASS -> glass(c, a, false);
            case PRIMARY -> glass(c, a, true);
            case NODE -> node(c, a);
            default -> { }
        }
    }

    private String fit(String s, int maxW) {
        var tr = Gfx.tr();
        if (tr.getWidth(s) <= maxW) return s;
        return tr.trimToWidth(s, maxW - 8) + "...";
    }

    private void compact(DrawContext c, float a) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight(), acc = Theme.current().accent();
        float k = Math.max(hover * 0.6f, state * 0.45f);
        if (k > 0.02f) Gfx.glow(c, x, y, x + w, y + h, 10, Theme.alpha(acc, k * a), 4);
        Gfx.rrect(c, x, y, x + w, y + h, 10, Theme.alpha(Gfx.lerp(0xC0110A1F, 0xD0201438, hover), a));
        Gfx.routline(c, x, y, x + w, y + h, 10, Theme.alpha(Gfx.lerp(0x30FFFFFF, acc, Math.max(hover * 0.6f, state * 0.55f)), a));
        int is = 24, ix = x + 9, iy = y + 8;
        Gfx.circle(c, ix + is / 2, iy + is / 2, is / 2, Theme.alpha(Gfx.lerp(0xFF2A1D45, acc, state), a));
        Icons.draw(c, icon, ix + (is - 14) / 2, iy + (is - 14) / 2, 2,
            Theme.alpha(Gfx.lerp(0xFFB9A6E8, 0xFFFFFFFF, state), a));
        if (showSwitch) {
            int tx = x + w - 34, ty = y + 12;
            Gfx.rrect(c, tx, ty, tx + 24, ty + 12, 6, Theme.alpha(Gfx.lerp(0xFF3B2F55, acc, state), a));
            Gfx.circle(c, tx + 6 + (int) (state * 12), ty + 6, 4, Theme.alpha(0xFFFFFFFF, a));
            for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) {
                c.fill(x + w - 51 + i * 3, y + 13 + j * 3, x + w - 50 + i * 3, y + 14 + j * 3, Theme.alpha(0x66FFFFFF, a));
            }
        } else {
            Gfx.circle(c, x + w - 20, y + 20, 8, Theme.alpha(Gfx.lerp(0xFF2A1D45, acc, hover), a));
            Gfx.text(c, ">", x + w - 22, y + 16, Theme.alpha(0xFFFFFFFF, a));
        }
        Gfx.bold(c, title, x + 10, y + h - 22, Theme.alpha(0xFFFFFFFF, a));
        Gfx.text(c, fit(desc.get(), w - 20), x + 10, y + h - 11, Theme.alpha(0xFF9C94B5, a));
    }

    private void artCard(DrawContext c, float a) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight(), acc = Theme.current().accent();
        int artH = h - 34;
        float k = Math.max(hover * 0.6f, state * 0.5f);
        if (k > 0.02f) Gfx.glow(c, x, y, x + w, y + h, 12, Theme.alpha(acc, k * a), 4);
        Gfx.rrect(c, x, y, x + w, y + h, 12, Theme.alpha(0xE0120B20, a));
        int ax = x + 3, ay = y + 3, aw = w - 6, ah = artH - 3;
        c.enableScissor(ax, ay, ax + aw, ay + ah);
        Art.draw(c, art, ax, ay, aw, ah);
        if (hover > 0.02f) c.fill(ax, ay, ax + aw, ay + ah, Theme.alpha(0xFFFFFFFF, 0.07f * hover));
        c.disableScissor();
        Gfx.cornerMask(c, ax, ay, ax + aw, ay + ah, 9, 0xFF170F28, false);
        if (a < 0.99f) Gfx.rrect(c, x, y, x + w, y + h, 12, Theme.alpha(0xFF0B0714, (1 - a)));
        Gfx.routline(c, x, y, x + w, y + h, 12, Theme.alpha(Gfx.lerp(0x30FFFFFF, acc, Math.max(hover * 0.6f, state)), a));
        if (state > 0.3f) {
            int bw = Gfx.tr().getWidth(badge) + 12;
            int bx = x + w - bw - 8, by = y + 8;
            Gfx.rrect(c, bx, by, bx + bw, by + 12, 6, Theme.alpha(acc, a * state));
            Gfx.text(c, badge, bx + 6, by + 2, Theme.alpha(0xFFFFFFFF, a * state));
        }
        Gfx.bold(c, title, x + 10, y + artH + 6, Theme.alpha(0xFFFFFFFF, a));
        Gfx.text(c, fit(desc.get(), w - 20), x + 10, y + artH + 18, Theme.alpha(0xFF9C94B5, a));
    }

    private void pill(DrawContext c, float a, boolean isOn) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight(), acc = Theme.current().accent();
        if (state > 0.02f) Gfx.glow(c, x, y, x + w, y + h, h / 2, Theme.alpha(acc, 0.7f * state * a), 3);
        int bg = Gfx.lerp(Gfx.lerp(0x00FFFFFF, 0x26FFFFFF, hover), Gfx.shade(acc, 0.85f), state);
        Gfx.rrect(c, x, y, x + w, y + h, h / 2, Theme.alpha(bg, a));
        int col = Theme.alpha(Gfx.lerp(0xFFBDB4D6, 0xFFFFFFFF, Math.max(state, hover * 0.7f)), a);
        if (iconOnly) {
            Icons.draw(c, icon, x + (w - 7) / 2, y + (h - 7) / 2, 1, col);
        } else {
            Icons.draw(c, icon, x + 9, y + (h - 7) / 2, 1, col);
            Gfx.text(c, title, x + 9 + 7 + 4, y + (h - 8) / 2 + 1, col);
        }
    }

    private void glass(DrawContext c, float a, boolean primary) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight(), acc = Theme.current().accent();
        int bg;
        if (primary) {
            Gfx.glow(c, x, y, x + w, y + h, h / 2, Theme.alpha(acc, (0.5f + hover * 0.5f) * a), 4);
            bg = Gfx.lerp(Gfx.shade(acc, 0.62f), Gfx.shade(acc, 0.85f), hover);
        } else {
            if (hover > 0.02f) Gfx.glow(c, x, y, x + w, y + h, h / 2, Theme.alpha(acc, hover * a), 3);
            bg = Gfx.lerp(0x2AFFFFFF, Theme.alpha(acc, 0.4f), hover);
        }
        Gfx.rrect(c, x, y, x + w, y + h, h / 2, Theme.alpha(bg, a));
        Gfx.routline(c, x, y, x + w, y + h, h / 2, Theme.alpha(Gfx.lerp(0x40FFFFFF, 0xCCFFFFFF, hover), a));
        int tw = Gfx.tr().getWidth(title);
        int tx = x + (w - tw) / 2 + (icon.isEmpty() ? 0 : 5);
        if (!icon.isEmpty()) Icons.draw(c, icon, tx - 13, y + (h - 7) / 2, 1, Theme.alpha(0xFFFFFFFF, a));
        Gfx.text(c, title, tx, y + (h - 8) / 2 + 1, Theme.alpha(0xFFFFFFFF, a));
    }

    private void node(DrawContext c, float a) {
        int cx = getX() + getWidth() / 2, cy = getY() + getHeight() / 2, acc = Theme.current().accent();
        float s = nodeScale;
        int r = Math.round(15 * s);
        float al = a * Math.min(1f, (s - 0.45f) * 2f);
        if (state > 0.02f || hover > 0.02f) {
            for (int i = 4; i >= 1; i--) Gfx.circle(c, cx, cy, r + i * 3, Theme.alpha(acc, 0.05f * (5 - i) * Math.max(state, hover) * al));
        }
        Gfx.circle(c, cx, cy, r, Theme.alpha(Gfx.lerp(0xE0140C26, acc, state), al));
        Gfx.ring(c, cx, cy, r, 1, Theme.alpha(Gfx.lerp(0x66FFFFFF, 0xFFFFFFFF, Math.max(state, hover)), al));
        int sc = s > 0.85f ? 2 : 1;
        Icons.draw(c, icon, cx - (7 * sc) / 2, cy - (7 * sc) / 2, sc, Theme.alpha(0xFFFFFFFF, al));
        if (state < 0.5f) Gfx.textCentered(c, title, cx, cy + r + 4, Theme.alpha(0xFFC9C3DA, al * (1 - state)));
    }
}
