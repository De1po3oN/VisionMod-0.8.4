package com.visionmod;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Local adaptive budgets for Vision Mod only. Never modifies server TPS,
 * Minecraft's global render distance or another mod's graphics options.
 */
public final class VisionPerformance {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
        .resolve("visionmod/performance.properties");

    public static int oreRadius = 6;
    public static int checksPerTick = 80;
    public static int outlineLimit = 48;
    public static int cosmeticsRange = 12;
    public static String preset = "MAX_FPS";

    public static boolean adaptive = true;
    public static boolean hideOtherCosmeticsOnBusySpawn = true;
    public static boolean reduceAmbientOnBusySpawn = true;
    public static int busyPlayerThreshold = 16;

    private static int nearbyPlayers = 0;
    private static int scanTicks = 0;
    private static long lastFrameNanos = 0;
    private static double frameTimeMillis = 16.7;

    private VisionPerformance() {}

    public static void tick(Minecraft client) {
        if (client.level == null || client.player == null) {
            nearbyPlayers = 0;
            scanTicks = 0;
            lastFrameNanos = 0;
            return;
        }
        if (++scanTicks >= 40) {
            scanTicks = 0;
            // Loaded players, not a server-wide player count.
            nearbyPlayers = client.level.players().size();
        }
    }

    public static void observeFrame() {
        long now = System.nanoTime();
        if (lastFrameNanos != 0) {
            double elapsed = (now - lastFrameNanos) / 1_000_000.0;
            if (elapsed > 1 && elapsed < 250) frameTimeMillis = frameTimeMillis * .92 + elapsed * .08;
        }
        lastFrameNanos = now;
    }

    public static int loadedPlayers() { return nearbyPlayers; }
    public static int estimatedFps() { return (int) Math.round(1000.0 / Math.max(1, frameTimeMillis)); }
    public static boolean busy() {
        return adaptive && (nearbyPlayers >= busyPlayerThreshold || frameTimeMillis >= 40.0);
    }
    public static int effectiveOreRadius() { return busy() ? Math.max(4, oreRadius / 2) : oreRadius; }
    public static int effectiveChecksPerTick() {
        return busy() ? Math.max(24, checksPerTick / (frameTimeMillis > 50 ? 4 : 2)) : checksPerTick;
    }
    public static int effectiveOutlineLimit() { return busy() ? Math.max(16, outlineLimit / 2) : outlineLimit; }
    public static int effectiveCosmeticsRange() { return busy() ? Math.min(12, cosmeticsRange) : cosmeticsRange; }
    public static boolean drawRemoteCosmetics() {
        return !busy() || !hideOtherCosmeticsOnBusySpawn;
    }
    public static boolean drawHearts() { return !busy() || !reduceAmbientOnBusySpawn; }
    public static boolean spawnAmbient() { return !busy() || !reduceAmbientOnBusySpawn; }

    public static void load() {
        if (!Files.isRegularFile(FILE)) { maxFps(); return; }
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(FILE)) {
            p.load(in);
            oreRadius = choice(number(p, "oreRadius", 6), new int[] {4,6,8,12,16}, 6);
            checksPerTick = choice(number(p, "checksPerTick", 80), new int[] {40,80,160,320}, 80);
            outlineLimit = choice(number(p, "outlineLimit", 48), new int[] {32,48,96,160,240}, 48);
            cosmeticsRange = choice(number(p, "cosmeticsRange", 12), new int[] {12,24,48}, 12);
            busyPlayerThreshold = choice(number(p, "busyPlayerThreshold", 16), new int[] {8,12,16,24,32}, 16);
            adaptive = flag(p, "adaptive", true);
            hideOtherCosmeticsOnBusySpawn = flag(p, "hideRemoteOnBusy", true);
            reduceAmbientOnBusySpawn = flag(p, "reduceAmbientOnBusy", true);
            String value = p.getProperty("preset", "CUSTOM");
            preset = value.matches("MAX_FPS|BALANCED|VISUAL|CUSTOM") ? value : "CUSTOM";
            VisionModClient.oreHighlight = flag(p, "oreHighlight", false);
            VisionModClient.playerHearts = flag(p, "playerHearts", false);
            VisionModClient.cosmetics = flag(p, "cosmetics", false);
            VisionModClient.showOtherCosmetics = flag(p, "otherCosmetics", false);
            VisionModClient.particles = flag(p, "particleFilter", true);
            VisionModClient.hideAllParticles = flag(p, "hideAllParticles", true);
        } catch (Exception e) {
            System.err.println("[VisionMod] Invalid performance configuration: " + e);
            maxFps();
        }
    }

    private static int choice(int value, int[] options, int fallback) {
        for (int option : options) if (option == value) return value;
        return fallback;
    }
    private static int number(Properties p, String name, int fallback) {
        try { return Integer.parseInt(p.getProperty(name, Integer.toString(fallback))); }
        catch (NumberFormatException ignored) { return fallback; }
    }
    private static boolean flag(Properties p, String key, boolean fallback) {
        return "true".equalsIgnoreCase(p.getProperty(key)) ? true :
            "false".equalsIgnoreCase(p.getProperty(key)) ? false : fallback;
    }

    public static void maxFps() {
        preset = "MAX_FPS";
        oreRadius = 6; checksPerTick = 80; outlineLimit = 48; cosmeticsRange = 12;
        adaptive = true;
        hideOtherCosmeticsOnBusySpawn = true;
        reduceAmbientOnBusySpawn = true;
        busyPlayerThreshold = 12;
        VisionModClient.oreHighlight = false;
        VisionModClient.playerHearts = false;
        VisionModClient.cosmetics = false;
        VisionModClient.showOtherCosmetics = false;
        VisionModClient.particles = true;
        VisionModClient.hideAllParticles = true;
        VisionExtras.snow = false;
        VisionExtras.hearts = false;
        VisionExtras.targetOutline = false;
        save();
    }
    public static void balanced() {
        preset = "BALANCED";
        oreRadius = 8; checksPerTick = 160; outlineLimit = 96; cosmeticsRange = 24;
        adaptive = true;
        hideOtherCosmeticsOnBusySpawn = true;
        reduceAmbientOnBusySpawn = true;
        busyPlayerThreshold = 16;
        VisionModClient.oreHighlight = true;
        VisionModClient.playerHearts = false;
        VisionModClient.cosmetics = false;
        VisionModClient.showOtherCosmetics = false;
        VisionModClient.particles = true;
        VisionModClient.hideAllParticles = false;
        save();
    }
    public static void visual() {
        preset = "VISUAL";
        oreRadius = 16; checksPerTick = 320; outlineLimit = 240; cosmeticsRange = 48;
        adaptive = false;
        hideOtherCosmeticsOnBusySpawn = false;
        reduceAmbientOnBusySpawn = false;
        busyPlayerThreshold = 32;
        VisionModClient.oreHighlight = true;
        VisionModClient.playerHearts = true;
        VisionModClient.cosmetics = true;
        VisionModClient.showOtherCosmetics = true;
        VisionModClient.particles = false;
        VisionModClient.hideAllParticles = false;
        save();
    }
    public static void custom() { preset = "CUSTOM"; save(); }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Properties p = new Properties();
            p.setProperty("preset", preset);
            p.setProperty("oreRadius", Integer.toString(oreRadius));
            p.setProperty("checksPerTick", Integer.toString(checksPerTick));
            p.setProperty("outlineLimit", Integer.toString(outlineLimit));
            p.setProperty("cosmeticsRange", Integer.toString(cosmeticsRange));
            p.setProperty("adaptive", Boolean.toString(adaptive));
            p.setProperty("hideRemoteOnBusy", Boolean.toString(hideOtherCosmeticsOnBusySpawn));
            p.setProperty("reduceAmbientOnBusy", Boolean.toString(reduceAmbientOnBusySpawn));
            p.setProperty("busyPlayerThreshold", Integer.toString(busyPlayerThreshold));
            p.setProperty("oreHighlight", Boolean.toString(VisionModClient.oreHighlight));
            p.setProperty("playerHearts", Boolean.toString(VisionModClient.playerHearts));
            p.setProperty("cosmetics", Boolean.toString(VisionModClient.cosmetics));
            p.setProperty("otherCosmetics", Boolean.toString(VisionModClient.showOtherCosmetics));
            p.setProperty("particleFilter", Boolean.toString(VisionModClient.particles));
            p.setProperty("hideAllParticles", Boolean.toString(VisionModClient.hideAllParticles));
            try (OutputStream out = Files.newOutputStream(FILE)) {
                p.store(out, "Vision Mod local performance settings - 0.8.3");
            }
        } catch (Exception e) {
            System.err.println("[VisionMod] Cannot save performance settings: " + e);
        }
    }
}
