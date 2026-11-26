package net.lumora.module;

import net.lumora.core.Category;
import net.lumora.core.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;

public class JumpCirclesModule extends Module {
    private boolean wasOnGround = true;

    public JumpCirclesModule() { super("jump", "Jump Circles", "Rings of light where you jump and land", false, Category.PLAYER, "ring"); }

    @Override
    public void tick(MinecraftClient c) {
        if (c.player == null || c.world == null) return;
        boolean now = c.player.isOnGround();
        if (now != wasOnGround) ring(c, now ? 0.9 : 0.6);
        wasOnGround = now;
    }

    private void ring(MinecraftClient c, double r) {
        for (int i = 0; i < 20; i++) {
            double a = Math.PI * 2 * i / 20;
            c.world.addParticleClient(ParticleTypes.END_ROD,
                c.player.getX() + Math.cos(a) * r, c.player.getY() + 0.05, c.player.getZ() + Math.sin(a) * r, 0, 0, 0);
        }
    }
}
