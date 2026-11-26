package net.lumora.core;

import net.lumora.Lumora;
import net.minecraft.client.MinecraftClient;

/** One self-contained visual feature. */
public abstract class Module {
    public final String id, name, description, icon;
    public final Category category;
    public boolean hidden = false;
    private final boolean defaultOn;

    protected Module(String id, String name, String description, boolean defaultOn, Category category, String icon) {
        this.id = id; this.name = name; this.description = description;
        this.defaultOn = defaultOn; this.category = category; this.icon = icon;
    }

    public boolean enabled() { return Lumora.config().modules.getOrDefault(id, defaultOn); }
    public void toggle() { Lumora.config().modules.put(id, !enabled()); }

    public void init() { }
    public void tick(MinecraftClient client) { }
}
