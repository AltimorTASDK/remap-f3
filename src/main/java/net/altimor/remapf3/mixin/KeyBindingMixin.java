package net.altimor.remapf3.mixin;

import net.altimor.remapf3.RemapF3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {
	@Inject(method = "setKeyPressed", at = @At("RETURN"))
	private static void setKeyPressed(Key key, boolean pressed, CallbackInfo ci) {
		// No recursion
		if (key.getCode() == RemapF3.DEBUG_KEY_CODE)
			return;

		if (!RemapF3.debugKey.isPressed() && RemapF3.debugKeyPressed) {
			// Handle release
			var client = MinecraftClient.getInstance();
			var handle = client.getWindow().getHandle();
			client.keyboard.onKey(handle, RemapF3.DEBUG_KEY_CODE, 0, 0, 0);
		}

		RemapF3.debugKeyPressed = RemapF3.debugKey.isPressed();
	}
}
