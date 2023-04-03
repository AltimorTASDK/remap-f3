package net.altimor.remapf3.mixin;

import net.altimor.remapf3.RemapF3;
import net.minecraft.client.util.InputUtil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(InputUtil.class)
public class InputUtilMixin {
	@Inject(method = "isKeyPressed", at = @At("HEAD"), cancellable = true)
	private static void isKeyPressed(long handle, int code, CallbackInfoReturnable<Boolean> cir) {
		if (RemapF3.FAKE_CODE_TO_BIND.containsKey(code))
			cir.setReturnValue(RemapF3.FAKE_CODE_TO_BIND.get(code).isPressed());
	}
}
