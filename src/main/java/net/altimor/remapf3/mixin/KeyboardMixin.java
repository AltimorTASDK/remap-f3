package net.altimor.remapf3.mixin;

import net.altimor.remapf3.RemapF3;
import net.minecraft.client.Keyboard;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@ModifyConstant(method = "onKey", constant = @Constant(intValue = GLFW.GLFW_KEY_F3))
	private int onKey(int value) {
		return RemapF3.getFakeKeyCode(value);
	}

	@ModifyArg(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Keyboard;processF3(I)Z"))
	public int processF3(int code) {
		return RemapF3.isFakeKeyCode(code) ? RemapF3.getRealKeyCode(code) : -1;
	}
}

