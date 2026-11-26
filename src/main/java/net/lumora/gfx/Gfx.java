package net.lumora.gfx;

import net.lumora.core.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Drawing helpers: rounded rects, circles, glows, text, aurora, cosmic scenes. */
public final class Gfx {
    private Gfx() { }

    public static TextRenderer tr() { return MinecraftClient.getInstance().textRenderer; }

    // ---------- colour ----------
    private static int ch(int a, int b, float t) { a &= 255; b &= 255; return (int) (a + (b - a) * t); }

    public static int lerp(int a, int b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        return (ch(a >>> 24, b >>> 24, t) << 24) | (ch(a >> 16, b >> 16, t) << 16)
            | (ch(a >> 8, b >> 8, t) << 8) | ch(a, b, t);
    }

    public static int shade(int argb, float k) {
        int r = Math.min(255, (int) (((argb >> 16) & 255) * k));
        int g = Math.min(255, (int) (((argb >> 8) & 255) * k));
        int b = Math.min(255, (int) ((argb & 255) * k));
        return (argb & 0xFF000000) | (r << 16) | (g << 8) | b;
    }

    // ---------- text (skips near-transparent text, which vanilla would draw opaque) ----------
    private static boolean invisible(int color) { return ((color >>> 24) & 255) < 8; }

    public static void text(DrawContext c, String s, int x, int y, int color) {
        if (invisible(color)) return;
        c.drawText(tr(), s, x, y, color, false);
    }

    public static void textCentered(DrawContext c, String s, int cx, int y, int color) {
        text(c, s, cx - tr().getWidth(s) / 2, y, color);
    }

    public static void bold(DrawContext c, String s, int x, int y, int color) {
        if (invisible(color)) return;
        c.drawText(tr(), Text.literal(s).formatted(Formatting.BOLD), x, y, color, false);
    }

    public static int boldWidth(String s) { return tr().getWidth(Text.literal(s).formatted(Formatting.BOLD)); }

    public static void scaledBold(DrawContext c, String s, int x, int y, float scale, int color, boolean centered) {
        if (invisible(color)) return;
        Text t = Text.literal(s).formatted(Formatting.BOLD);
        int w = tr().getWidth(t);
        var m = c.getMatrices();
        m.pushMatrix();
        m.translate(x, y);
        m.scale(scale, scale);
        c.drawText(tr(), t, centered ? -w / 2 : 0, 0, color, false);
        m.popMatrix();
    }

    // ---------- shapes ----------
    private static int inset(int r, int i) {
        double dy = r - i - 0.5;
        return (int) Math.round(r - Math.sqrt(Math.max(0, r * r - dy * dy)));
    }

    public static void rrect(DrawContext c, int x1, int y1, int x2, int y2, int r, int col) {
        if (((col >>> 24) & 255) == 0) return;
        r = Math.min(r, Math.min(x2 - x1, y2 - y1) / 2);
        if (r <= 0) { c.fill(x1, y1, x2, y2, col); return; }
        c.fill(x1, y1 + r, x2, y2 - r, col);
        for (int i = 0; i < r; i++) {
            int in = inset(r, i);
            c.fill(x1 + in, y1 + i, x2 - in, y1 + i + 1, col);
            c.fill(x1 + in, y2 - i - 1, x2 - in, y2 - i, col);
        }
    }

    public static void routline(DrawContext c, int x1, int y1, int x2, int y2, int r, int col) {
        if (((col >>> 24) & 255) == 0) return;
        r = Math.min(r, Math.min(x2 - x1, y2 - y1) / 2);
        if (r <= 0) {
            c.fill(x1, y1, x2, y1 + 1, col); c.fill(x1, y2 - 1, x2, y2, col);
            c.fill(x1, y1, x1 + 1, y2, col); c.fill(x2 - 1, y1, x2, y2, col);
            return;
        }
        int[] ins = new int[r];
        for (int i = 0; i < r; i++) ins[i] = inset(r, i);
        c.fill(x1 + ins[0], y1, x2 - ins[0], y1 + 1, col);
        c.fill(x1 + ins[0], y2 - 1, x2 - ins[0], y2, col);
        for (int i = 1; i < r; i++) {
            int a = ins[i], b = Math.max(ins[i - 1], a + 1);
            c.fill(x1 + a, y1 + i, x1 + b, y1 + i + 1, col);
            c.fill(x2 - b, y1 + i, x2 - a, y1 + i + 1, col);
            c.fill(x1 + a, y2 - i - 1, x1 + b, y2 - i, col);
            c.fill(x2 - b, y2 - i - 1, x2 - a, y2 - i, col);
        }
        c.fill(x1, y1 + r, x1 + 1, y2 - r, col);
        c.fill(x2 - 1, y1 + r, x2, y2 - r, col);
    }

    /** Paints the area OUTSIDE the top (and optionally bottom) rounded corners, to round off square art. */
    public static void cornerMask(DrawContext c, int x1, int y1, int x2, int y2, int r, int col, boolean bottomToo) {
        for (int i = 0; i < r; i++) {
            int in = inset(r, i);
            if (in <= 0) continue;
            c.fill(x1, y1 + i, x1 + in, y1 + i + 1, col);
            c.fill(x2 - in, y1 + i, x2, y1 + i + 1, col);
            if (bottomToo) {
                c.fill(x1, y2 - i - 1, x1 + in, y2 - i, col);
                c.fill(x2 - in, y2 - i - 1, x2, y2 - i, col);
            }
        }
    }

    public static void glow(DrawContext c, int x1, int y1, int x2, int y2, int r, int col, int layers) {
        for (int k = layers; k >= 1; k--) {
            rrect(c, x1 - k, y1 - k, x2 + k, y2 + k, r + k, Theme.alpha(col, 0.22f / k));
        }
    }

    public static void circle(DrawContext c, int cx, int cy, int r, int col) {
        if (r <= 0 || ((col >>> 24) & 255) == 0) return;
        for (int dy = -r; dy <= r; dy++) {
            int dx = (int) Math.round(Math.sqrt(r * r - dy * dy + r * 0.4));
            c.fill(cx - dx, cy + dy, cx + dx + 1, cy + dy + 1, col);
        }
    }

    public static void ring(DrawContext c, int cx, int cy, int r, int th, int col) {
        if (r <= 0 || ((col >>> 24) & 255) == 0) return;
        int ri = r - th;
        for (int dy = -r; dy <= r; dy++) {
            int ox = (int) Math.round(Math.sqrt(r * r - dy * dy + r * 0.4));
            if (Math.abs(dy) < ri) {
                int ix = (int) Math.round(Math.sqrt(ri * ri - dy * dy + ri * 0.4));
                c.fill(cx - ox, cy + dy, cx - ix, cy + dy + 1, col);
                c.fill(cx + ix + 1, cy + dy, cx + ox + 1, cy + dy + 1, col);
            } else {
                c.fill(cx - ox, cy + dy, cx + ox + 1, cy + dy + 1, col);
            }
        }
    }

    public static void ellipseRing(DrawContext c, int cx, int cy, int rx, int ry, int col, int size) {
        int steps = Math.max(24, Math.max(rx, ry) * 4);
        for (int k = 0; k < steps; k++) {
            double a = Math.PI * 2 * k / steps;
            int px = (int) (cx + Math.cos(a) * rx), py = (int) (cy + Math.sin(a) * ry);
            c.fill(px, py, px + size, py + size, col);
        }
    }

    public static void pill(DrawContext c, int x, int y, int w, int h) {
        rrect(c, x, y, x + w, y + h, h / 2, 0xB0100A1C);
        routline(c, x, y, x + w, y + h, h / 2, 0x30FFFFFF);
    }

    // ---------- scenes ----------
    public static void aurora(DrawContext c, int x, int y, int w, int h, float t, int rgb) {
        int rgb2 = 0xD946EF;
        for (int i = 0; i < w; i += 3) {
            float a = 0.5f + 0.5f * (float) Math.sin(t * 0.6 + i * 0.045);
            float b = 0.5f + 0.5f * (float) Math.sin(t * 0.35 + i * 0.11 + 1.3);
            int top = y + (int) (h * (0.15f + 0.5f * (1 - a)));
            int base = ((i / 3) % 2 == 0) ? (rgb & 0xFFFFFF) : rgb2;
            int alpha = (int) (110 * (0.4f + 0.6f * b));
            c.fillGradient(x + i, top, Math.min(x + w, x + i + 3), y + h, base, (alpha << 24) | base);
        }
    }

    public static void cosmic(DrawContext c, int x, int y, int w, int h, float t, int top, int bottom, int accent) {
        c.fillGradient(x, y, x + w, y + h, top, bottom);
        int stars = Math.min(140, Math.max(20, w * h / 1800));
        for (int i = 0; i < stars; i++) {
            long hs = (i * 2654435761L) & 0xFFFFFFFFL;
            int sx = x + (int) (hs % Math.max(1, w));
            int sy = y + (int) ((hs >>> 5) % Math.max(1, h * 7 / 10));
            float tw = 0.3f + 0.7f * (float) Math.abs(Math.sin(t * 0.8 + i));
            c.fill(sx, sy, sx + 1, sy + 1, Theme.alpha(0xFFFFFFFF, tw * 0.8f));
        }
        aurora(c, x, y + h * 35 / 100, w, h * 50 / 100, t, accent);
        // planet with ring
        int pr = Math.max(6, Math.min(w, h) * 11 / 100);
        int px = x + w * 74 / 100, py = y + h * 30 / 100;
        int rx = (int) (pr * 2.1), ry = (int) (pr * 0.55);
        for (int k = 0; k < 90; k++) {   // back half of ring
            double a = Math.PI * 2 * k / 90;
            if (Math.sin(a) < 0) { int qx = (int) (px + Math.cos(a) * rx), qy = (int) (py + Math.sin(a) * ry); c.fill(qx, qy, qx + 2, qy + 1, Theme.alpha(accent, 0.8f)); }
        }
        circle(c, px, py, pr, 0xFF8E86AC);
        circle(c, px - pr / 4, py - pr / 4, Math.max(1, pr / 2), 0xFFD9D4EA);
        for (int k = 0; k < 90; k++) {   // front half of ring
            double a = Math.PI * 2 * k / 90;
            if (Math.sin(a) >= 0) { int qx = (int) (px + Math.cos(a) * rx), qy = (int) (py + Math.sin(a) * ry); c.fill(qx, qy, qx + 2, qy + 1, Theme.alpha(accent, 0.9f)); }
        }
        // shooting star
        float ph = (t * 0.35f) % 1f;
        if (ph < 0.25f) {
            float p = ph / 0.25f;
            int sx = x + (int) (w * (0.15f + 0.5f * p)), sy = y + (int) (h * (0.05f + 0.3f * p));
            for (int k = 0; k < 10; k++) c.fill(sx - k * 2, sy - k, sx - k * 2 + 2, sy - k + 1, Theme.alpha(0xFFFFFFFF, (1 - k / 10f) * (1 - p)));
        }
        // ridge silhouette
        for (int i = 0; i < w; i += 2) {
            int ry2 = y + h * 84 / 100 + (int) (Math.sin(i * 0.02) * 6 + Math.sin(i * 0.05) * 3);
            c.fill(x + i, ry2, Math.min(x + w, x + i + 2), y + h, 0xE00A0614);
        }
    }

    public static void emblem(DrawContext c, int cx, int cy, int r, float t, int accent, float a) {
        for (int i = 5; i >= 1; i--) circle(c, cx, cy, r + i * 3, Theme.alpha(accent, 0.035f * (6 - i) * a));
        circle(c, cx, cy, r, Theme.alpha(0xFF140A26, a));
        ring(c, cx, cy, r, 2, Theme.alpha(accent, a));
        ring(c, cx, cy, r - 5, 1, Theme.alpha(accent, 0.45f * a));
        double ang = t * 1.2;
        circle(c, (int) (cx + Math.cos(ang) * (r + 7)), (int) (cy + Math.sin(ang) * (r + 7) * 0.55), 2, Theme.alpha(0xFFFFFFFF, a));
        int s = Math.max(2, r / 5);
        Icons.draw(c, "sparkle", cx - (7 * s) / 2, cy - (7 * s) / 2, s, Theme.alpha(0xFFFFFFFF, a));
    }
}
