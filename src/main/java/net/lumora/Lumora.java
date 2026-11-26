package net.lumora;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.lumora.core.Config;
import net.lumora.core.Module;
import net.lumora.core.ModuleManager;
import net.lumora.module.*;
import net.lumora.screen.LumoraScreen;
import net.lumora.screen.OrbitScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Lumora implements ClientModInitializer {
    private static Config config;
    private static ModuleManager modules;

    public static Config config() { return config; }
    public static ModuleManager modules() { return modules; }
    public static boolean isReady() { return config != null && modules != null; }

    @Override
    public void onInitializeClient() {
        config = Config.load();
        modules = new ModuleManager();
        // Interface
        modules.register(new MenuModule());
        modules.register(new HudModule());
        modules.register(new CrosshairModule());
        modules.register(new HotbarGlowModule());
        // Render
        modules.register(new AmbienceModule());
        modules.register(new MistModule());
        modules.register(new HazeModule());
        modules.register(new VignetteModule());
        // World (sky presets are chosen on the World page)
        modules.register(new SkyModule());
        // Player
        modules.register(new HaloModule());
        modules.register(new AuraModule());
        modules.register(new TrailModule());
        modules.register(new JumpCirclesModule());
        modules.register(new ShockwaveModule());
        modules.all().forEach(Module::init);

        KeyBinding open = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.lumora.settings", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, KeyBinding.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (open.wasPressed()) {
                if (!(mc.currentScreen instanceof LumoraScreen)) mc.setScreen(new OrbitScreen(mc.currentScreen));
            }
            for (Module m : modules.all()) if (m.enabled()) m.tick(mc);
        });
    }
}
