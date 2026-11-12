package com.visionmod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Properties;
import java.util.Random;

/** Independent, low-overhead client enhancements. No server plugin required. */
public final class VisionExtras {
    public static boolean cps = false;
    public static boolean armorHud = false;
    public static boolean hungerHud = false;
    public static boolean crosshair = false;
    public static boolean zoom = true;
    public static boolean fastScroll = false;
    public static boolean targetOutline = false;
    public static boolean snow = false;
    public static boolean hearts = false;
    public static boolean shulkerHud = false;
    public static boolean shulkerTooltip = false;
    public static boolean arrowHitSound = false;
    public static boolean smallSword = false;
    public static boolean smallShield = false;
    public static boolean smallTotem = false;
    public static boolean lowFire = false;
    public static volatile boolean zoomHeld = false;
    public static int crosshairColor = 0xFF48E7F6;
    public static int outlineColor = 0xFFFF78CB;
    public static float zoomFactor = .35f;
    // 9x9 bitmap crosshair; edited with the built-in pixel editor.
    private static final boolean[] CUSTOM_PIXELS = defaultPixels();

    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
        .resolve("visionmod/extras.properties");
    // Static array: avoid allocating an array every time the HUD renders.
    private static final EquipmentSlot[] ARMOR_SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST,
        EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    private static final Deque<Long> CLICKS = new ArrayDeque<>();
    private static final Random RANDOM = new Random();
    private static int tick;

    private VisionExtras() { }

    private static boolean[] defaultPixels() {
        boolean[] bitmap = new boolean[81];
        for (int d = 1; d <= 3; d++) {
            bitmap[4 * 9 + (4 - d)] = true;
            bitmap[4 * 9 + (4 + d)] = true;
            bitmap[(4 - d) * 9 + 4] = true;
            bitmap[(4 + d) * 9 + 4] = true;
        }
        bitmap[4 * 9 + 4] = true;
        return bitmap;
    }
    public static boolean pixel(int x, int y) {
        return x >= 0 && x < 9 && y >= 0 && y < 9 && CUSTOM_PIXELS[y * 9 + x];
    }
    public static void togglePixel(int x, int y) {
        if (x < 0 || x >= 9 || y < 0 || y >= 9) return;
        CUSTOM_PIXELS[y * 9 + x] = !CUSTOM_PIXELS[y * 9 + x];
        save();
    }
    public static void resetPixels() {
        boolean[] defaults = defaultPixels();
        System.arraycopy(defaults, 0, CUSTOM_PIXELS, 0, CUSTOM_PIXELS.length);
        save();
    }
    public static String pixels() {
        StringBuilder result = new StringBuilder(81);
        for (boolean value : CUSTOM_PIXELS) result.append(value ? '1' : '0');
        return result.toString();
    }
    public static void setPixels(String encoded) {
        if (encoded == null || !encoded.matches("[01]{81}")) throw new IllegalArgumentException("Crosshair pattern must contain 81 binary digits");
        for (int i = 0; i < CUSTOM_PIXELS.length; i++) CUSTOM_PIXELS[i] = encoded.charAt(i) == '1';
        save();
    }

    public static void init() {
        load();
        VisionShulkerTooltip.init();
        // Replace only the vanilla crosshair; default behavior is retained when disabled.
        HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, original -> (graphics, tracker) -> {
            Minecraft mc = Minecraft.getInstance();
            if (!VisionModClient.enabled || !crosshair || mc.player == null || mc.screen != null) {
                original.render(graphics, tracker);
            } else {
                drawCrosshair(graphics);
            }
        });
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("visionmod", "extra_hud"), (graphics, tracker) -> {
            Minecraft mc = Minecraft.getInstance();
            if (!VisionModClient.enabled || mc.player == null || mc.level == null || mc.screen != null) return;
            int y = 8;
            if (cps) {
                graphics.drawString(mc.font, "CPS: " + clickRate(), 8, y, 0xFFF2F7FF);
                y += 13;
            }
            if (hungerHud) {
                var food = mc.player.getFoodData();
                graphics.drawString(mc.font, "Food " + food.getFoodLevel() + "/20  Sat "
                    + Math.round(food.getSaturationLevel() * 10f) / 10f, 8, y, 0xFFFFD166);
                y += 13;
            }
            if (armorHud) {
                int pieces = 0, worn = 0, total = 0;
                for (EquipmentSlot slot : ARMOR_SLOTS) {
                    ItemStack stack = mc.player.getItemBySlot(slot);
                    if (!stack.isEmpty()) {
                        pieces++;
                        if (stack.isDamageableItem()) {
                            worn += Math.max(0, stack.getMaxDamage() - stack.getDamageValue());
                            total += stack.getMaxDamage();
                        }
                    }
                }
                String label = "Armor " + pieces + "/4";
                if (total > 0) label += "  Dur " + Math.round(100f * worn / total) + "%";
                graphics.drawString(mc.font, label, 8, y, 0xFF65F0B2);
                y += 13;
            }
            if (shulkerHud) {
                ItemStack held = mc.player.getMainHandItem();
                if (held.getItem() instanceof BlockItem block && block.getBlock() instanceof ShulkerBoxBlock) {
                    ItemContainerContents stored = held.get(DataComponents.CONTAINER);
                    int distinct = 0, totalItems = 0;
                    if (stored != null) {
                        for (ItemStack content : stored.nonEmptyItems()) {
                            distinct++;
                            totalItems += content.getCount();
                        }
                    }
                    graphics.drawString(mc.font, "Shulker: " + distinct + " stacks / " + totalItems + " items",
                        8, y, 0xFFAC79FF);
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.level == null || mc.player == null) { zoomHeld = false; tick = 0; return; }
            if (!VisionModClient.enabled || mc.screen != null || !snow && !hearts || !VisionPerformance.spawnAmbient()) return;
            if (VisionModClient.isParticlesEnabled() && VisionModClient.hideAllParticles) return;
            // 2 lightweight local effects per second at most. Never scan world blocks.
            if (++tick % 16 != 0) return;
            double x = mc.player.getX() + (RANDOM.nextDouble() - .5) * 3;
            double y = mc.player.getY() + 1.2 + RANDOM.nextDouble();
            double z = mc.player.getZ() + (RANDOM.nextDouble() - .5) * 3;
            if (snow) mc.level.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0, -.01, 0);
            if (hearts) mc.level.addParticle(ParticleTypes.HEART, x + .2, y, z, 0, .02, 0);
        });
    }

    private static void drawCrosshair(GuiGraphics g) {
        int cx = g.guiWidth()/2, cy = g.guiHeight()/2;
        int col = crosshairColor;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (pixel(x, y)) g.fill(cx + (x - 4) * 2, cy + (y - 4) * 2,
                        cx + (x - 4) * 2 + 2, cy + (y - 4) * 2 + 2, col);
            }
        }
    }

    public static void clicked() {
        if (!VisionModClient.enabled || !cps) return;
        synchronized (CLICKS) {
            long now = System.currentTimeMillis();
            CLICKS.addLast(now);
            while (CLICKS.size() > 100) CLICKS.removeFirst();
        }
    }

    private static int clickRate() {
        synchronized (CLICKS) {
            long cutoff = System.currentTimeMillis() - 1000;
            while (!CLICKS.isEmpty() && CLICKS.peekFirst() < cutoff) CLICKS.removeFirst();
            return CLICKS.size();
        }
    }

    public static void toggle(int index) {
        switch (index) {
            case 0 -> cps = !cps;
            case 1 -> armorHud = !armorHud;
            case 2 -> hungerHud = !hungerHud;
            case 3 -> crosshair = !crosshair;
            case 4 -> zoom = !zoom;
            case 5 -> fastScroll = !fastScroll;
            case 6 -> targetOutline = !targetOutline;
            case 7 -> snow = !snow;
            case 8 -> hearts = !hearts;
            case 9 -> shulkerHud = !shulkerHud;
            case 10 -> arrowHitSound = !arrowHitSound;
            case 11 -> smallSword = !smallSword;
            case 12 -> smallShield = !smallShield;
            case 13 -> smallTotem = !smallTotem;
            case 14 -> lowFire = !lowFire;
            case 15 -> shulkerTooltip = !shulkerTooltip;
            default -> { return; }
        }
        save();
        VisionPerformance.custom();
    }

    public static boolean enabled(int index) {
        return switch (index) {
            case 0 -> cps;
            case 1 -> armorHud;
            case 2 -> hungerHud;
            case 3 -> crosshair;
            case 4 -> zoom;
            case 5 -> fastScroll;
            case 6 -> targetOutline;
            case 7 -> snow;
            case 8 -> hearts;
            case 9 -> shulkerHud;
            case 10 -> arrowHitSound;
            case 11 -> smallSword;
            case 12 -> smallShield;
            case 13 -> smallTotem;
            case 14 -> lowFire;
            case 15 -> shulkerTooltip;
            default -> false;
        };
    }

    public static void cycleCrosshairColor() {
        crosshairColor = nextColor(crosshairColor);
        save();
    }
    public static void cycleOutlineColor() {
        outlineColor = nextColor(outlineColor);
        save();
    }
    private static int nextColor(int current) {
        int[] choices = {0xFF48E7F6, 0xFFFF78CB, 0xFFFFD166, 0xFF65F0B2, 0xFFFFFFFF, 0xFFAC79FF};
        for (int i = 0; i < choices.length; i++) if (choices[i] == current) return choices[(i+1)%choices.length];
        return choices[0];
    }
    public static void cycleZoom() {
        zoomFactor = zoomFactor == .35f ? .5f : zoomFactor == .5f ? .2f : .35f;
        save();
    }

    public static void load() {
        if (!Files.isRegularFile(FILE)) return;
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(FILE)) {
            p.load(in);
            cps = value(p, "cps", cps); armorHud = value(p, "armorHud", armorHud);
            hungerHud = value(p, "hungerHud", hungerHud); crosshair = value(p, "crosshair", crosshair);
            zoom = value(p, "zoom", zoom); fastScroll = value(p, "fastScroll", fastScroll);
            targetOutline = value(p, "targetOutline", targetOutline); snow = value(p, "snow", snow);
            hearts = value(p, "hearts", hearts); shulkerHud = value(p, "shulkerHud", shulkerHud);
            shulkerTooltip = value(p, "shulkerTooltip", shulkerTooltip);
            arrowHitSound = value(p, "arrowHitSound", arrowHitSound);
            smallSword = value(p, "smallSword", smallSword);
            smallShield = value(p, "smallShield", smallShield);
            smallTotem = value(p, "smallTotem", smallTotem);
            lowFire = value(p, "lowFire", lowFire);
            String savedPattern = p.getProperty("crosshairPixels");
            if (savedPattern != null && savedPattern.matches("[01]{81}")) {
                for (int i = 0; i < 81; i++) CUSTOM_PIXELS[i] = savedPattern.charAt(i) == '1';
            }
            crosshairColor = color(p, "crosshairColor", crosshairColor);
            outlineColor = color(p, "outlineColor", outlineColor);
            float z = Float.parseFloat(p.getProperty("zoomFactor", Float.toString(zoomFactor)));
            if (z == .2f || z == .35f || z == .5f) zoomFactor = z;
        } catch (Exception e) {
            System.err.println("[VisionMod] Could not load extra settings: " + e);
        }
    }
    private static boolean value(Properties p, String key, boolean def) {
        String value = p.getProperty(key);
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        return def;
    }
    private static int color(Properties p, String key, int def) {
        try { return (int) Long.parseLong(p.getProperty(key), 16) | 0xFF000000; }
        catch (RuntimeException e) { return def; }
    }
    public static void save() {
        Properties p = new Properties();
        p.setProperty("cps", Boolean.toString(cps)); p.setProperty("armorHud", Boolean.toString(armorHud));
        p.setProperty("hungerHud", Boolean.toString(hungerHud)); p.setProperty("crosshair", Boolean.toString(crosshair));
        p.setProperty("zoom", Boolean.toString(zoom)); p.setProperty("fastScroll", Boolean.toString(fastScroll));
        p.setProperty("targetOutline", Boolean.toString(targetOutline)); p.setProperty("snow", Boolean.toString(snow));
        p.setProperty("hearts", Boolean.toString(hearts)); p.setProperty("shulkerHud", Boolean.toString(shulkerHud));
        p.setProperty("shulkerTooltip", Boolean.toString(shulkerTooltip));
        p.setProperty("arrowHitSound", Boolean.toString(arrowHitSound));
        p.setProperty("smallSword", Boolean.toString(smallSword));
        p.setProperty("smallShield", Boolean.toString(smallShield));
        p.setProperty("smallTotem", Boolean.toString(smallTotem));
        p.setProperty("lowFire", Boolean.toString(lowFire));
        p.setProperty("crosshairColor", Integer.toHexString(crosshairColor));
        p.setProperty("crosshairPixels", pixels());
        p.setProperty("outlineColor", Integer.toHexString(outlineColor));
        p.setProperty("zoomFactor", Float.toString(zoomFactor));
        try {
            Files.createDirectories(FILE.getParent());
            try (OutputStream out = Files.newOutputStream(FILE)) { p.store(out, "Vision Mod optional client enhancements"); }
        } catch (Exception e) { System.err.println("[VisionMod] Could not save extra settings: " + e); }
    }
}
