package net.lumora.core;

import net.lumora.Lumora;

public record Theme(String name, int top, int bottom, int accent) {
    public static final Theme[] ALL = {
        new Theme("Nocturne", 0xFF0B0716, 0xFF2A1250, 0xFFB57BFF),
        new Theme("Opaline",  0xFF08121A, 0xFF1B3A44, 0xFF8FE3E0),
        new Theme("Ember",    0xFF140806, 0xFF4A1A10, 0xFFFF9A5C),
        new Theme("Meadow",   0xFF07130B, 0xFF174023, 0xFF8DFFA8)
    };

    public static Theme current() {
        int i = Math.floorMod(Lumora.config().theme, ALL.length);
        return ALL[i];
    }

    public static int alpha(int argb, float a) {
        int na = Math.max(0, Math.min(255, (int) ((argb >>> 24) * a)));
        return (na << 24) | (argb & 0xFFFFFF);
    }
}
