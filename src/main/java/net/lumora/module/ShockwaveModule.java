package net.lumora.module;

import net.lumora.core.Category;
import net.lumora.core.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;

/** On landing, an expanding ring of light spreads over the ground. */
public class ShockwaveModule extends Module {
    private boolean wasOnGround = true;
    private int age = -1;

    public ShockwaveModule() { super("shock", "Shockwave", "Every landing sends a wave of light outward", false, Category.PLAYER, "bolt"); }

    @Override
    public void tick(MinecraftClient c) {
        if (c.player == null || c.world == null) return;
        boolean now = c.player.isOnGround();
        if (now && !wasOnGround) age = 0;
        wasOnGround = now;
        if (age < 0) return;
        double r = 0.4 + age * 0.35;
        for (int i = 0; i < 24; i++) {
            double a = Math.PI * 2 * i / 24;
            c.world.addParticleClient(ParticleTypes.END_ROD,
                c.player.getX() + Math.cos(a) * r, c.player.getY() + 0.05, c.player.getZ() + Math.sin(a) * r, 0, 0, 0);
        }
        if (++age > 9) age = -1;
    }
}
