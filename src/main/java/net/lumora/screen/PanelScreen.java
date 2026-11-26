package net.lumora.screen;

import net.lumora.Lumora;
import net.lumora.core.Category;
import net.lumora.core.Module;
import net.lumora.core.Theme;
import net.lumora.gfx.Gfx;
import net.lumora.gfx.Icons;
import net.lumora.module.CrosshairModule;
import net.lumora.screen.widget.CardButton;
import net.lumora.screen.widget.CardButton.Style;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/** A category page: nav pills on top, big title, scrolling grid of rounded cards. */
public class PanelScreen extends LumoraScreen {
    private final Category cat;
    private final Screen parent;
    private final List<CardButton> cards = new ArrayList<>();
    private int margin, gridTop, viewBottom, cols, colW, cardH, step, rows;
    private float scroll, scrollTarget;

    public PanelScreen(Category cat, Screen parent) {
        super(Text.literal(cat.title));
        this.cat = cat;
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        cards.clear();
        margin = Math.max(14, Math.min(48, width / 10));
        gridTop = 84;
        viewBottom = height - 20;
        buildNav();
        buildCards();
    }

    private void buildNav() {
        Category[] all = Category.values();
        int gap = 4, h = 18, y = 13, x0 = margin + 96;
        int need = 0;
        for (Category k : all) need += 8 + 7 + 4 + Gfx.tr().getWidth(k.title) + 10 + gap;
        boolean text = need <= width - margin - x0;
        if (!text) x0 = margin + 28;
        int x = x0;
        for (Category k : all) {
            int w = text ? 8 + 7 + 4 + Gfx.tr().getWidth(k.title) + 10 : 24;
            CardButton b = new CardButton(Style.PILL, x, y, w, h, k.title, () -> "", k.icon, "",
                () -> k == cat, () -> { if (k != cat) client.setScreen(new PanelScreen(k, parent)); });
            b.iconOnly = !text;
            addDrawableChild(b);
            x += w + gap;
        }
    }

    private CardButton make(String title, Supplier<String> desc, String icon, String art, BooleanSupplier on, Runnable act) {
        CardButton b = new CardButton(cat.gallery ? Style.ART : Style.COMPACT, 0, 0, colW, cardH, title, desc, icon, art, on, act);
        b.anim = 0f;
        addSelectableChild(b);
        cards.add(b);
        return b;
    }

    private void buildCards() {
        cols = width < 330 ? 2 : 3;
        int gap = 8;
        colW = (width - margin * 2 - gap * (cols - 1)) / cols;
        cardH = cat.gallery ? 98 : 54;
        step = cardH + gap;

        switch (cat) {
            case WORLD -> {
                String[] names = {"Opaline", "Nocturne", "Galaxy"};
                String[] desc = {"Iridescent veils of lavender and cyan", "Soft violet clouds with a lilac glow", "Aurora, stars and a ringed planet"};
                for (int i = 0; i < 3; i++) {
                    final int v = i + 1;
                    CardButton b = make(names[i], () -> desc[v - 1], "globe", names[i].toLowerCase(),
                        () -> Lumora.config().sky == v, () -> Lumora.config().sky = (Lumora.config().sky == v) ? 0 : v);
                    b.badge = "ACTIVE";
                }
            }
            case THEMES -> {
                for (int i = 0; i < Theme.ALL.length; i++) {
                    final int v = i;
                    CardButton b = make(Theme.ALL[i].name(), () -> "Accent colour scheme", "palette", "theme" + i,
                        () -> Math.floorMod(Lumora.config().theme, Theme.ALL.length) == v, () -> Lumora.config().theme = v);
                    b.badge = "ACTIVE";
                }
            }
            default -> {
                for (Module m : Lumora.modules().byCategory(cat)) {
                    make(m.name, () -> m.description, m.icon, m.id, m::enabled, m::toggle);
                }
                if (cat == Category.INTERFACE) {
                    CardButton b = make("Crosshair Style", () -> CrosshairModule.STYLES[Math.floorMod(Lumora.config().crosshairStyle, CrosshairModule.STYLES.length)],
                        "ring", "crosshair", () -> false, () -> Lumora.config().crosshairStyle++);
                    b.showSwitch = false;
                }
            }
        }
        rows = (cards.size() + cols - 1) / cols;
    }

    private float maxScroll() { return Math.max(0, rows * step - (viewBottom - gridTop)); }

    @Override
    protected void renderContent(DrawContext ctx, int mx, int my, float delta) {
        Theme t = Theme.current();
        float f = fade(), el = elapsed();

        // logo
        Gfx.circle(ctx, margin + 10, 22, 10, Theme.alpha(t.accent(), f));
        Icons.draw(ctx, "sparkle", margin + 6, 18, 1, Theme.alpha(0xFFFFFFFF, f));
        Gfx.bold(ctx, "LUMORA", margin + 26, 18, Theme.alpha(0xFFFFFFFF, f));

        // heading
        Gfx.scaledBold(ctx, cat.title, margin, 46 + (int) ((1 - f) * 8), 2f, Theme.alpha(0xFFFFFFFF, f), false);
        Gfx.text(ctx, cat.tagline, margin, 66, Theme.alpha(0xFF9C94B5, f));

        // grid
        scroll += (scrollTarget - scroll) * 0.25f;
        ctx.enableScissor(0, gridTop, width, viewBottom);
        for (int i = 0; i < cards.size(); i++) {
            CardButton b = cards.get(i);
            float p = ease((el - 80 - i * 45) / 320f);
            b.anim = p;
            int col = i % cols, row = i / cols;
            int y = gridTop + row * step - Math.round(scroll) + (int) ((1 - p) * 12);
            b.setX(margin + col * (colW + 8));
            b.setY(y);
            b.active = y >= gridTop - 2 && y + cardH <= viewBottom + 2;
            b.render(ctx, mx, my, delta);
        }
        ctx.disableScissor();

        // scrollbar
        float ms = maxScroll();
        if (ms > 0) {
            int trackH = viewBottom - gridTop, bx = width - margin + 8;
            int barH = Math.max(14, (int) (trackH * (trackH / (float) (rows * step))));
            int by = gridTop + (int) ((trackH - barH) * (scroll / ms));
            Gfx.rrect(ctx, bx, gridTop, bx + 3, viewBottom, 1, 0x22FFFFFF);
            Gfx.rrect(ctx, bx, by, bx + 3, by + barH, 1, Theme.alpha(t.accent(), 0.9f));
        }
    }

    @Override
    protected void renderOverlay(DrawContext ctx, int mx, int my, float delta) {
        Gfx.textCentered(ctx, "Click to toggle  -  Scroll for more  -  Esc for the orbit", width / 2, height - 12, 0x70FFFFFF);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double hAmount, double vAmount) {
        float ms = maxScroll();
        if (ms <= 0) return true;
        scrollTarget = Math.max(0, Math.min(ms, scrollTarget - (float) Math.signum(vAmount) * step));
        return true;
    }

    @Override public void close() { client.setScreen(parent); }
    @Override public void removed() { Lumora.config().save(); }
}
