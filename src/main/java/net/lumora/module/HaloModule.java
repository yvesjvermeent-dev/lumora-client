package net.lumora.module;

import net.lumora.core.Category;
import net.lumora.core.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;

public class HaloModule extends Module {
    public HaloModule() { super("halo", "Halo", "A softly glowing ring above you", false, Category.PLAYER, "halo"); }

    @Override
    public void tick(MinecraftClient c) {
        if (c.player == null || c.world == null || c.isPaused() || c.player.age % 2 != 0) return;
        if (c.options.getPerspective().isFirstPerson()) return;
        double y = c.player.getY() + c.player.getHeight() + 0.3;
        for (int i = 0; i < 8; i++) {
            double a = Math.PI * 2 * i / 8;
            c.world.addParticleClient(ParticleTypes.ELECTRIC_SPARK,
                c.player.getX() + Math.cos(a) * 0.35, y, c.player.getZ() + Math.sin(a) * 0.35, 0, 0, 0);
        }
    }
}
