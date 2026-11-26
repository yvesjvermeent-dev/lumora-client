package net.lumora.screen;

import net.lumora.core.Theme;
import net.lumora.gfx.Gfx;
import net.lumora.screen.widget.CardButton;
import net.lumora.screen.widget.CardButton.Style;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class LumoraTitleScreen extends LumoraScreen {
    private final List<CardButton> buttons = new ArrayList<>();
    private final List<Integer> baseY = new ArrayList<>();

    public LumoraTitleScreen() { super(Text.literal("Lumora")); }

    private void add(Style st, int x, int y, int w, String label, String icon, Runnable r) {
        CardButton b = new CardButton(st, x, y, w, 24, label, () -> "", icon, "", () -> false, r);
        b.anim = 0f;
        addDrawableChild(b);
        buttons.add(b);
        baseY.add(y);
    }

    @Override
    protected void init() {
        super.init();
        buttons.clear(); baseY.clear();
        int w = 200, x = width / 2 - w / 2, y = height / 2 + 2, gap = 28;
        add(Style.GLASS, x, y, w, "Singleplayer", "person", () -> client.setScreen(new SelectWorldScreen(this)));
        add(Style.GLASS, x, y + gap, w, "Multiplayer", "globe", () -> client.setScreen(new MultiplayerScreen(this)));
        add(Style.PRIMARY, x, y + gap * 2, w, "Customise", "sparkle", () -> client.setScreen(new OrbitScreen(this)));
        add(Style.GLASS, x, y + gap * 3, w / 2 - 3, "Options", "gear", () -> client.setScreen(new OptionsScreen(this, client.options)));
        add(Style.GLASS, x + w / 2 + 3, y + gap * 3, w / 2 - 3, "Quit", "", () -> client.scheduleStop());
    }

    @Override
    protected void renderContent(DrawContext ctx, int mx, int my, float delta) {
        Theme t = Theme.current();
        float f = fade(), el = elapsed(), tm = time();
        int cx = width / 2, top = height / 2;
        Gfx.emblem(ctx, cx, top - 92 + (int) (Math.sin(tm * 1.4) * 2), 14, tm, t.accent(), f);
        Gfx.scaledBold(ctx, "L U M O R A", cx, top - 64 + (int) ((1 - f) * 10), 3f, Theme.alpha(0xFFFFFFFF, f), true);
        Gfx.textCentered(ctx, "see the world in a different light", cx, top - 34, Theme.alpha(0xFFC9C3DA, f));
        for (int i = 0; i < buttons.size(); i++) {
            float p = ease((el - 150 - i * 70) / 320f);
            buttons.get(i).anim = p;
            buttons.get(i).setY(baseY.get(i) + (int) ((1 - p) * 14));
        }
    }

    @Override
    protected void renderOverlay(DrawContext ctx, int mx, int my, float delta) {
        Gfx.text(ctx, "Right Shift in game opens the orbit", 8, height - 12, 0x70FFFFFF);
        String v = "Minecraft 1.21.11  -  Lumora 0.2.0";
        Gfx.text(ctx, v, width - Gfx.tr().getWidth(v) - 8, height - 12, 0x70FFFFFF);
    }

    @Override public boolean shouldCloseOnEsc() { return false; }
}
