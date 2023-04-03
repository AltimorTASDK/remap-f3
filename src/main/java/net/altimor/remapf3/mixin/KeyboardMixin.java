package net.altimor.remapf3.mixin;

import net.altimor.remapf3.RemapF3;
import net.minecraft.client.Keyboard;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@ModifyConstant(method = "onKey", constant = @Constant(intValue = GLFW.GLFW_KEY_F3))
	private int injected(int value) {
		return RemapF3.DEBUG_KEY_CODE;
	}
}

