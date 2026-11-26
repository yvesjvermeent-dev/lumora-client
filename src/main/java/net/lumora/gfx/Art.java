package net.lumora.gfx;

import net.lumora.core.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Util;

/** Procedural preview art for gallery cards. Everything is drawn in code. */
public final class Art {
    private Art() { }

    public static void draw(DrawContext c, String id, int x, int y, int w, int h) {
        float t = Util.getMeasuringTimeMs() / 1000f;
        if (id.startsWith("theme")) {
            Theme th = Theme.ALL[Integer.parseInt(id.substring(5)) % Theme.ALL.length];
            c.fillGradient(x, y, x + w, y + h, th.top(), th.bottom());
            Gfx.aurora(c, x, y + h / 3, w, h * 2 / 3, t, th.accent());
            Gfx.circle(c, x + w / 2, y + h / 2, Math.max(4, h / 5), th.accent());
            Gfx.ring(c, x + w / 2, y + h / 2, Math.max(6, h / 4), 1, 0x88FFFFFF);
            return;
        }
        switch (id) {
            case "galaxy" -> Gfx.cosmic(c, x, y, w, h, t, 0xFF14083A, 0xFF4A1B8C, 0xFFB45CFF);
            case "nocturne" -> {
                c.fillGradient(x, y, x + w, y + h, 0xFF0A0612, 0xFF1E1136);
                blobs(c, x, y, w, h, t, 0xFF8C5CFF, 6, 0.16f);
                Gfx.aurora(c, x, y + h / 4, w, h * 3 / 4, t * 0.5f, 0xFF9B6BFF);
            }
            case "opaline" -> {
                c.fillGradient(x, y, x + w, y + h, 0xFF0E1A22, 0xFF22414A);
                blobs(c, x, y, w, h, t, 0xFFB9B3F5, 4, 0.14f);
                blobs(c, x, y, w, h, t + 3, 0xFF8FE3E0, 3, 0.12f);
                for (int i = 0; i < w; i += 3) {
                    int hh = 4 + (int) ((i * 7919L) % 9);
                    c.fill(x + i, y + h - hh, x + i + 3, y + h, 0xFF0F2A1C);
                }
            }
            case "halo", "aura", "trail", "jump", "shock" -> scene(c, x, y, w, h, t, id);
            default -> {
                c.fillGradient(x, y, x + w, y + h, Theme.current().top(), Theme.current().bottom());
                Icons.draw(c, "sparkle", x + w / 2 - 14, y + h / 2 - 14, 4, Theme.alpha(Theme.current().accent(), 0.9f));
            }
        }
    }

    private static void blobs(DrawContext c, int x, int y, int w, int h, float t, int col, int n, float a) {
        for (int i = 0; i < n; i++) {
            int bx = x + (int) (((i * 53 + t * (6 + i * 2)) % w + w) % w);
            int by = y + (int) (h * (0.25 + 0.4 * Math.sin(t * 0.3 + i)));
            int r = Math.max(6, h / 3);
            for (int k = 3; k >= 1; k--) Gfx.circle(c, bx, by, r * k / 3, Theme.alpha(col, a / 2));
        }
    }

    private static void scene(DrawContext c, int x, int y, int w, int h, float t, String id) {
        c.fillGradient(x, y, x + w, y + h, 0xFF3A1B78, 0xFF8A5BD0);
        int gy = y + h * 72 / 100;
        c.fill(x, gy, x + w, y + h, 0xFF4E8F3A);
        c.fill(x, gy, x + w, gy + 2, 0xFF6DB84A);
        for (int i = 0; i < 10; i++) {
            int fx = x + (i * 37 + 11) % Math.max(1, w), fy = gy + 4 + (i * 5) % 8;
            c.fill(fx, fy, fx + 2, fy + 2, 0xFFF3A6D8);
        }
        int s = Math.max(1, h / 24), px = x + w / 2, base = gy + 6;
        c.fill(px - 2 * s, base - 6 * s, px + 2 * s, base, 0xFF1B1B3F);
        c.fill(px - 2 * s, base - 12 * s, px + 2 * s, base - 6 * s, 0xFF2B2A66);
        c.fill(px - 2 * s, base - 8 * s, px + 2 * s, base - 7 * s, 0xFFC9A44A);
        c.fill(px - 3 * s, base - 12 * s, px - 2 * s, base - 7 * s, 0xFF2B2A66);
        c.fill(px + 2 * s, base - 12 * s, px + 3 * s, base - 7 * s, 0xFF2B2A66);
        c.fill(px - 2 * s, base - 16 * s, px + 2 * s, base - 12 * s, 0xFF3A3470);
        int white = 0xFFFFFFFF;
        switch (id) {
            case "halo" -> {
                Gfx.ellipseRing(c, px, base - 17 * s, 3 * s + 1, s + 1, Theme.alpha(white, 0.25f), 3);
                Gfx.ellipseRing(c, px, base - 17 * s, 3 * s, s, white, 1);
            }
            case "aura" -> {
                for (int i = 0; i < 26; i++) {
                    double a = t * 2 + i * 0.55;
                    int dx = (int) (px + Math.cos(a) * 5 * s), dy = base - i * 16 * s / 26;
                    c.fill(dx, dy, dx + 2, dy + 2, Theme.alpha(0xFFD9B8FF, 1 - i / 30f));
                }
            }
            case "trail" -> {
                for (int i = 0; i < 34; i++) {
                    double a = t * 2.2 - i * 0.12;
                    int dx = (int) (px + Math.cos(a) * 13 * s), dy = (int) (base - 2 * s + Math.sin(a) * 3 * s);
                    c.fill(dx, dy, dx + 2, dy + 2, Theme.alpha(0xFFE3C9FF, 1 - i / 36f));
                }
            }
            default -> {
                int n = id.equals("jump") ? 3 : 2;
                for (int k = 0; k < n; k++) {
                    float ph = (t * 0.8f + k / (float) n) % 1f;
                    int rx = 3 * s + (int) (ph * 11 * s);
                    Gfx.ellipseRing(c, px, base, rx, Math.max(1, rx * 3 / 10), Theme.alpha(id.equals("jump") ? white : 0xFFFF9BE8, 1 - ph), 2);
                }
            }
        }
    }
}
