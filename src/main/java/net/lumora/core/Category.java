package net.lumora.core;

public enum Category {
    RENDER("Render", "Atmosphere, light and haze", "eye", false),
    PLAYER("Player", "Make yourself stand out", "person", true),
    WORLD("World", "Skies from somewhere else", "globe", true),
    INTERFACE("Interface", "Menus, HUD and crosshair", "gear", false),
    THEMES("Themes", "Pick your colours", "palette", true);

    public final String title, tagline, icon;
    public final boolean gallery;

    Category(String title, String tagline, String icon, boolean gallery) {
        this.title = title; this.tagline = tagline; this.icon = icon; this.gallery = gallery;
    }
}
