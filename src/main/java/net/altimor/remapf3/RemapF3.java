package net.altimor.remapf3;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

public class RemapF3 implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("remap-f3");

	// Fake key code used to represent F3
	public static final int DEBUG_KEY_CODE = 69_420;

	public static KeyBinding debugKey;

	public static boolean debugKeyPressed;

	@Override
	public void onInitializeClient() {
		debugKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.remap-f3.debug",
			GLFW.GLFW_KEY_F3,
			KeyBinding.MISC_CATEGORY));
	}
}
