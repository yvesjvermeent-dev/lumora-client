package net.lumora.module;

import net.lumora.core.Category;
import net.lumora.core.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;

/** Drifting petals around the camera. Purely cosmetic and local. */
public class AmbienceModule extends Module {
    public AmbienceModule() { super("ambience", "Ambience", "Petals drifting through the air", false, Category.RENDER, "sparkle"); }

    @Override
    public void tick(MinecraftClient c) {
        if (c.player == null || c.world == null || c.isPaused() || c.player.age % 4 != 0) return;
        var r = c.world.random;
        double x = c.player.getX() + (r.nextDouble() - 0.5) * 16;
        double y = c.player.getY() + 2 + r.nextDouble() * 5;
        double z = c.player.getZ() + (r.nextDouble() - 0.5) * 16;
        c.world.addParticleClient(ParticleTypes.CHERRY_LEAVES, x, y, z, 0, 0, 0);
    }
}
