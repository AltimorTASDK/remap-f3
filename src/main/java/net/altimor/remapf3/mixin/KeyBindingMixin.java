package net.altimor.remapf3.mixin;

import net.altimor.remapf3.RemapF3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

import java.util.Map;
import java.util.Set;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Maps;

import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {
    @Shadow @Final private static Map<String, KeyBinding> KEYS_BY_ID;
    @Shadow @Final private static Map<Key, KeyBinding> KEY_TO_BINDINGS;
    @Shadow @Final private static Set<String> KEY_CATEGORIES;

    @Shadow @Final private InputUtil.Key defaultKey;
    @Shadow @Final private String category;
    @Shadow private InputUtil.Key boundKey;

	private static final Map<Key, KeyBinding> KEY_TO_DEBUG_BINDINGS = Maps.newHashMap();

	private int group;

	private static KeyBinding getRemappedBindForKey(Key key) {
		var bind = KEY_TO_DEBUG_BINDINGS.get(key);
		if (bind == null) {
			bind = KEY_TO_BINDINGS.get(key);
			if (!RemapF3.BINDS.contains(bind))
				return null;
		}
		return bind;
	}

	private static boolean shouldSuppressBind(KeyBinding bind) {
		// Make debug combos suppress overlapping binds
		var group = ((KeyBindingMixin)(Object)bind).group;
		return group == RemapF3.GROUP_DEBUG_COMBO && RemapF3.getDebugBind().isPressed();
	}

	@Inject(method = "setKeyPressed", at = @At("HEAD"), cancellable = true)
	private static void setKeyPressed(Key key, boolean pressed, CallbackInfo ci) {
		var bind = getRemappedBindForKey(key);
		if (bind == null)
			return;

		// Send fake key code press/release
		var client = MinecraftClient.getInstance();
		var handle = client.getWindow().getHandle();
		var fakeKeyCode = RemapF3.getFakeKeyCode(bind);

		if (pressed && !bind.isPressed())
			client.keyboard.onKey(handle, fakeKeyCode, -1, 1, 0);
		else if (!pressed && bind.isPressed())
			client.keyboard.onKey(handle, fakeKeyCode, -1, 0, 0);

		if (shouldSuppressBind(bind))
			ci.cancel();
	}

	@Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
	private static void onKeyPressed(Key key, CallbackInfo ci) {
		var bind = getRemappedBindForKey(key);
		if (bind != null && shouldSuppressBind(bind))
			ci.cancel();
	}

	@Inject(method = "updateKeysByCode", at = @At("HEAD"), cancellable = true)
    private static void updateKeysByCode(CallbackInfo ci) {
		// Don't override other binds with different groups
        KEY_TO_BINDINGS.clear();
		KEY_TO_DEBUG_BINDINGS.clear();

        for (var bind : KEYS_BY_ID.values()) {
			var mixin = (KeyBindingMixin)(Object)bind;

			if (mixin.group == RemapF3.GROUP_DEBUG_COMBO)
				KEY_TO_DEBUG_BINDINGS.put(mixin.boundKey, bind);
			else if (mixin.group == RemapF3.GROUP_DEFAULT)
				KEY_TO_BINDINGS.put(mixin.boundKey, bind);
        }

		ci.cancel();
    }

	@Inject(
		method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V",
		at = @At("RETURN"))
	private void init(String translationKey, InputUtil.Type type, int code, String category, CallbackInfo ci) {
		// Don't override other binds with different groups
		group = RemapF3.getGroupedKeyGroup(code);
		boundKey = type.createFromCode(RemapF3.getGroupedKeyCode(code));

		if (group == RemapF3.GROUP_DEBUG_COMBO)
			KEY_TO_DEBUG_BINDINGS.put(boundKey, (KeyBinding)(Object)this);
	}

	@Inject(method = "compareTo", at = @At("HEAD"), cancellable = true)
    private void compareTo(KeyBinding bind, CallbackInfoReturnable<Integer> cir) {
		// Disable alphabetical sorting for remapped binds
		if (RemapF3.BINDS.contains((KeyBinding)(Object)this) && category.equals(bind.getCategory()))
			cir.setReturnValue(0);
    }

	@Inject(method = "equals", at = @At("HEAD"), cancellable = true)
    private void equals(KeyBinding other, CallbackInfoReturnable<Boolean> cir) {
		// No conflicts between binds of separate group
		if (group != ((KeyBindingMixin)(Object)other).group)
			cir.setReturnValue(false);
    }

	@Inject(method = "getDefaultKey", at = @At("HEAD"), cancellable = true)
    private void getDefaultKey(CallbackInfoReturnable<Key> cir) {
		// Remove group from default key codes
		var code = RemapF3.getGroupedKeyCode(defaultKey.getCode());
		cir.setReturnValue(defaultKey.getCategory().createFromCode(code));
    }

	@Inject(method = "isDefault", at = @At("HEAD"), cancellable = true)
    private void isDefault(CallbackInfoReturnable<Boolean> cir) {
		// Remove group from default key codes
		var defaultKey = ((KeyBinding)(Object)this).getDefaultKey();
		cir.setReturnValue(boundKey.equals(defaultKey));
    }
}
