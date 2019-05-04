package com.therandomlabs.randompatches;

import com.therandomlabs.randomlib.config.Config;
import com.therandomlabs.randompatches.client.WindowIconHandler;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

@Config(RandomPatches.MOD_ID)
public final class RPConfig {
	public static final class Boats {
		@Config.Property(
				"Prevents underwater boat passengers from being ejected after 60 ticks (3 seconds)."
		)
		public static boolean preventUnderwaterBoatPassengerEjection =
				RandomPatches.IS_DEOBFUSCATED;

		@Config.Property({
				"The buoyancy of boats when they are under flowing water.",
				"The vanilla default is -0.0007."
		})
		public static double underwaterBoatBuoyancy = 0.023;
	}

	public static final class Client {
		@Config.Category("Options related to the Minecraft window.")
		public static final Window window = null;

		@Config.Property(
				"Forces Minecraft to show the title screen after disconnecting rather than " +
						"the Multiplayer or Realms menu."
		)
		public static boolean forceTitleScreenOnDisconnect = RandomPatches.IS_DEOBFUSCATED;

		@Config.Property("Whether to remove the glowing effect from potions.")
		public static boolean removePotionGlint = RandomPatches.IS_DEOBFUSCATED;

		@Config.RequiresWorldReload
		@Config.Property("Enables the /rpreloadclient command.")
		public static boolean rpreloadclient = true;
	}

	public static final class Misc {
		@Config.Property("Enables the portal bucket replacement fix for Nether portals.")
		public static boolean portalBucketReplacementFixForNetherPortals;

		@Config.RequiresWorldReload
		@Config.Property("Enables the /rpreload command.")
		public static boolean rpreload = true;

		@Config.Property(
				"Whether skull stacking requires the same textures or just the same player profile."
		)
		public static boolean skullStackingRequiresSameTextures = true;
	}

	public static final class SpeedLimits {
		@Config.RangeDouble(min = 1.0)
		@Config.Property({
				"The maximum player speed.",
				"The vanilla default is 100.0."
		})
		public static float maxPlayerSpeed = 1000000.0F;

		@Config.RangeDouble(min = 1.0)
		@Config.Property({
				"The maximum player elytra speed.",
				"The vanilla default is 300.0."
		})
		public static float maxPlayerElytraSpeed = 1000000.0F;

		@Config.RangeDouble(min = 1.0)
		@Config.Property({
				"The maximum player vehicle speed.",
				"The vanilla default is 100.0."
		})
		public static double maxPlayerVehicleSpeed = 1000000.0;
	}

	public static final class Timeouts {
		@Config.RangeInt(min = 1)
		@Config.Property("The interval at which the server sends the KeepAlive packet.")
		public static int keepAlivePacketInterval = 15;

		@Config.RangeInt(min = 1)
		@Config.Property("The login timeout.")
		public static int loginTimeout = 900;

		@Config.RequiresMCRestart
		@Config.Property("Whether to apply the login timeout.")
		public static boolean patchLoginTimeout = true;

		@Config.RangeInt(min = 1)
		@Config.Property({
				"The read timeout.",
				"This is the time it takes for a player to be disconnected after not " +
						"responding to a KeepAlive packet.",
				"This value is automatically rounded up to a product of keepAlivePacketInterval.",
				"This only works on 1.12 and above."
		})
		public static int readTimeout = 90;

		public static long keepAlivePacketIntervalMillis;
		public static long keepAlivePacketIntervalLong;
		public static long readTimeoutMillis;

		public static void onReload() {
			if(readTimeout < keepAlivePacketInterval) {
				readTimeout = keepAlivePacketInterval * 2;
			} else if(readTimeout % keepAlivePacketInterval != 0) {
				readTimeout = keepAlivePacketInterval * (readTimeout / keepAlivePacketInterval + 1);
			}

			keepAlivePacketIntervalMillis = keepAlivePacketInterval * 1000L;
			keepAlivePacketIntervalLong = keepAlivePacketInterval;
			readTimeoutMillis = readTimeout * 1000L;

			System.setProperty("fml.readTimeout", Integer.toString(readTimeout));
			System.setProperty("fml.loginTimeout", Integer.toString(loginTimeout));
		}
	}

	public static final class Window {
		public static final String DEFAULT_ICON = RandomPatches.IS_DEOBFUSCATED ?
				"../src/main/resources/logo.png" : "";

		@Config.Property({
				"The path to the 16x16 Minecraft window icon.",
				"Leave this and the 32x32 icon blank to use the default icon."
		})
		public static String icon16 = DEFAULT_ICON;
		@Config.Property({
				"The path to the 32x32 Minecraft window icon.",
				"Leave this and the 16x16 icon blank to use the default icon."
		})
		public static String icon32 = DEFAULT_ICON;

		@Config.Property("The Minecraft window title.")
		public static String title = RandomPatches.IS_DEOBFUSCATED ?
				"RandomPatches" : RandomPatches.DEFAULT_WINDOW_TITLE;

		public static boolean setWindowSettings = true;

		public static void onReload() {
			if(icon16.isEmpty() && !icon32.isEmpty()) {
				icon16 = icon32;
			}

			if(icon32.isEmpty() && !icon16.isEmpty()) {
				icon32 = icon16;
			}

			if(RandomPatches.IS_CLIENT && setWindowSettings) {
				Minecraft.getInstance().addScheduledTask(Window::setWindowSettings);
			}
		}

		private static void setWindowSettings() {
			final MainWindow mainWindow = Minecraft.getInstance().mainWindow;

			if(mainWindow == null) {
				return;
			}

			if(!icon16.isEmpty()) {
				//If icon16 is empty, WindowIconHandler loads the Minecraft class too early
				WindowIconHandler.setWindowIcon();
			}

			GLFW.glfwSetWindowTitle(mainWindow.getHandle(), title);
		}
	}

	@Config.Category("Options related to boats.")
	public static final Boats boats = null;

	@Config.Category("Options related to client-sided features.")
	public static final Client client = null;

	@Config.Category("Options that don't fit into any other categories.")
	public static final Misc misc = null;

	@Config.Category("Options related to the movement speed limits.")
	public static final SpeedLimits speedLimits = null;

	@Config.Category("Options related to the disconnect timeouts.")
	public static final Timeouts timeouts = null;
}
