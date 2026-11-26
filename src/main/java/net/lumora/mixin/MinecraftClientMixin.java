package net.lumora.mixin;

import net.lumora.Lumora;
import net.lumora.screen.LumoraTitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    /** Swaps the vanilla title screen for the custom one when the module is on. */
    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen lumora$replaceTitle(Screen screen) {
        if (screen instanceof TitleScreen && Lumora.isReady() && Lumora.modules().isOn("menu")) {
            return new LumoraTitleScreen();
        }
        return screen;
    }
}
