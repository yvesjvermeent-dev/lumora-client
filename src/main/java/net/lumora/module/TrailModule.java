package net.lumora.module;

import net.lumora.core.Category;
import net.lumora.core.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;

public class TrailModule extends Module {
    public TrailModule() { super("trail", "Trail", "A ribbon of light behind you", false, Category.PLAYER, "trail"); }

    @Override
    public void tick(MinecraftClient c) {
        if (c.player == null || c.world == null || c.isPaused()) return;
        var v = c.player.getVelocity();
        if (v.x * v.x + v.z * v.z < 0.006) return;
        c.world.addParticleClient(ParticleTypes.WITCH, c.player.getX(), c.player.getY() + 0.15, c.player.getZ(), 0, 0.01, 0);
    }
}
