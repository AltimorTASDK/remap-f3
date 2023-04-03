package net.altimor.remapf3;

import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;

public class RemapF3 implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("remap-f3");
    public static final List<KeyBinding> BINDS = Lists.newArrayList();
    public static final Map<Integer, KeyBinding> FAKE_CODE_TO_BIND = Maps.newHashMap();

	public static final String DEBUG_PREFIX = "key.remapF3.debug.";
	public static final String DEBUG_CATEGORY = "key.categories.remapF3.debug";

	public static final int GROUP_DEFAULT     = 0;
	public static final int GROUP_DEBUG_COMBO = 1;

    private static KeyBinding debugBind;

	// Get a placeholder key code to replace a remapped hardcoded key code with
	public static int getFakeKeyCode(int code) {
		return -code;
	}

	public static int getFakeKeyCode(KeyBinding bind) {
		return getFakeKeyCode(bind.getDefaultKey().getCode());
	}

	public static int getRealKeyCode(int code) {
		return -code;
	}

	public static boolean isFakeKeyCode(int code) {
		return code < 0;
	}

	// Used to prevent bind conflicts
	public static int getGroupedKey(int code, int group) {
		return group * 1000 + code;
	}

	public static int getGroupedKeyGroup(int groupCode) {
		return groupCode / 1000;
	}

	public static int getGroupedKeyCode(int groupCode) {
		return groupCode % 1000;
	}

	public static KeyBinding getDebugBind() {
		return debugBind;
	}

	private static KeyBinding debugKeyBind(String name, int code, int group) {
		return KeyBindingHelper.registerKeyBinding(
			new KeyBinding(DEBUG_PREFIX + name, getGroupedKey(code, group), DEBUG_CATEGORY));
	}

	@Override
	public void onInitializeClient() {
		debugBind = debugKeyBind("master", GLFW.GLFW_KEY_F3, GROUP_DEFAULT);
		BINDS.add(debugBind);

		BINDS.add(debugKeyBind("reloadChunks",        GLFW.GLFW_KEY_A,  GROUP_DEBUG_COMBO));
		BINDS.add(debugKeyBind("showHitboxes",        GLFW.GLFW_KEY_B,  GROUP_DEBUG_COMBO));
		BINDS.add(debugKeyBind("clearChat",           GLFW.GLFW_KEY_D,  GROUP_DEBUG_COMBO));
		BINDS.add(debugKeyBind("showChunkBounds",     GLFW.GLFW_KEY_G,  GROUP_DEBUG_COMBO));
		BINDS.add(debugKeyBind("advancedTooltips",    GLFW.GLFW_KEY_H,  GROUP_DEBUG_COMBO));
		BINDS.add(debugKeyBind("copyBlock",           GLFW.GLFW_KEY_I,  GROUP_DEBUG_COMBO));
		BINDS.add(debugKeyBind("spectator",           GLFW.GLFW_KEY_N,  GROUP_DEBUG_COMBO));
		BINDS.add(debugKeyBind("cycleGamemode",       GLFW.GLFW_KEY_F4, GROUP_DEBUG_COMBO));
		BINDS.add(debugKeyBind("pauseOnLostFocus",    GLFW.GLFW_KEY_P,  GROUP_DEBUG_COMBO));
		BINDS.add(debugKeyBind("help",                GLFW.GLFW_KEY_Q,  GROUP_DEBUG_COMBO));
		BINDS.add(debugKeyBind("dumpDynamicTextures", GLFW.GLFW_KEY_S,  GROUP_DEBUG_COMBO));
		BINDS.add(debugKeyBind("reloadResources",     GLFW.GLFW_KEY_T,  GROUP_DEBUG_COMBO));
		BINDS.add(debugKeyBind("toggleProfiling",     GLFW.GLFW_KEY_L,  GROUP_DEBUG_COMBO));
		BINDS.add(debugKeyBind("copyLocation",        GLFW.GLFW_KEY_C,  GROUP_DEBUG_COMBO));

		for (var bind : BINDS)
			FAKE_CODE_TO_BIND.put(getFakeKeyCode(bind), bind);
	}
}
