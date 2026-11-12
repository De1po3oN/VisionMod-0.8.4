package com.visionmod;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Pure 2D client-drawn menu style. No vanilla widgets or texture packs are
 * required by the main window. There is no blur/shader pass or framebuffer.
 */
public final class VisionMenuTheme {
    public static final int WIDTH = 450;
    public static final int HEIGHT = 302;
    public static final String VERSION = "0.8.3";

    public static final int CYAN = 0xFF46E4F5;
    public static final int PURPLE = 0xFFC48BFF;
    public static final int WHITE = 0xFFF3F6FF;
    public static final int MUTED = 0xFF96A9C5;
    public static final int GREEN = 0xFF63EDB0;
    public static final int RED = 0xFFF97995;
    public static final int BACK = 0xF40B1020;
    public static final int SURFACE = 0xF41D2A41;
    public static final int HOVER = 0xFF304263;

    private VisionMenuTheme() {}
    public static int x(int width) { return (width - WIDTH) / 2; }
    public static int y(int height) { return Math.max(3, (height - HEIGHT) / 2); }
    public static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    public static void panel(GuiGraphics g, Font font, int screenWidth, int screenHeight,
                             String title, String subtitle) {
        int x = x(screenWidth), y = y(screenHeight);
        // Four simple, low-cost rectangles create a soft neon frame without shaders.
        g.fill(x - 8, y + 8, x + WIDTH + 8, y + HEIGHT + 11, 0x44000000);
        g.fill(x - 3, y - 3, x + WIDTH + 3, y + HEIGHT + 3, 0x5A243859);
        g.fill(x - 1, y - 1, x + WIDTH + 1, y + HEIGHT + 1, 0xFF405A7D);
        g.fillGradient(x, y, x + WIDTH, y + HEIGHT, 0xF9141D33, 0xF2080D18);
        g.fillGradient(x, y, x + WIDTH, y + 3, CYAN, PURPLE);
        g.fill(x + 16, y + 18, x + 20, y + 41, CYAN);
        g.drawString(font, title, x + 30, y + 17, WHITE);
        g.drawString(font, subtitle, x + 30, y + 34, MUTED);
        g.fill(x + 16, y + 51, x + WIDTH - 16, y + 52, 0xFF34455F);
        g.fill(x + WIDTH - 108, y + 17, x + WIDTH - 49, y + 35, 0xFF20354D);
        g.drawCenteredString(font, "v" + VERSION, x + WIDTH - 78, y + 22, CYAN);
        footer(g, font, x, y);
    }

    private static void footer(GuiGraphics g, Font font, int x, int y) {
        int yy = y + HEIGHT - 26;
        g.fillGradient(x + 15, yy, x + WIDTH - 15, yy + 1, CYAN, PURPLE);
        g.drawString(font, "TG: @cocok_ali", x + 18, yy + 9, CYAN);
        String server = "For: mc.showtime.su";
        g.drawString(font, server, x + WIDTH - font.width(server) - 18, yy + 9, PURPLE);
    }

    public static void sidebar(GuiGraphics g, Font font, int x, int y, int active, double mx, double my) {
        String[] tabs = {"VISUALS", "ORES", "EXTRAS", "PERFORMANCE", "CONFIGS"};
        g.fillGradient(x + 12, y + 61, x + 116, y + 268, 0xEB101A2D, 0xE8141325);
        for (int i = 0; i < tabs.length; i++) {
            int bx = x + 18, by = y + 70 + i * 36;
            boolean on = i == active;
            boolean hover = inside(mx, my, bx, by, 93, 29);
            g.fill(bx, by, bx + 93, by + 29, on ? 0xFF1B4963 : hover ? HOVER : 0xFF202C43);
            if (on) {
                g.fill(bx, by, bx + 3, by + 29, CYAN);
                g.fill(bx + 5, by + 2, bx + 91, by + 3, 0xFF337D92);
            }
            g.drawString(font, tabs[i], bx + 11, by + 11, on ? WHITE : MUTED);
        }
    }

    public static void moduleRow(GuiGraphics g, Font font, int x, int y, int w, int h,
                                 String label, boolean enabled, String binding, boolean hovered) {
        g.fill(x, y, x + w, y + h, hovered ? HOVER : SURFACE);
        g.fill(x, y, x + 3, y + h, enabled ? CYAN : 0xFF586A82);
        g.drawString(font, label, x + 9, y + 7, WHITE);
        String info = binding == null ? "MMB: bind" : binding;
        g.drawString(font, info, x + 9, y + h - 13, MUTED);
        int px = x + w - 30, py = y + 12;
        g.fill(px, py, px + 24, py + 13, enabled ? 0xFF157F8B : 0xFF414E64);
        g.fill(px + (enabled ? 12 : 2), py + 2, px + (enabled ? 22 : 12), py + 11,
               enabled ? 0xFFE5FFFF : 0xFFA2ADC0);
    }

    public static void actionRow(GuiGraphics g, Font font, int x, int y, int w, int h,
                                 String label, String binding, boolean hovered) {
        g.fill(x, y, x + w, y + h, hovered ? HOVER : SURFACE);
        g.fill(x, y, x + 3, y + h, PURPLE);
        g.drawString(font, label, x + 9, y + 7, WHITE);
        g.drawString(font, binding, x + 9, y + h - 13, MUTED);
        g.drawString(font, ">", x + w - 14, y + 16, PURPLE);
    }

    public static void option(GuiGraphics g, Font font, int x, int y, int w, int h,
                              String label, boolean active, boolean hovered) {
        g.fill(x, y, x + w, y + h, active ? 0xFF1B4D62 : hovered ? HOVER : SURFACE);
        if (active) g.fill(x, y, x + w, y + 2, CYAN);
        g.drawCenteredString(font, label, x + w / 2, y + (h - 8) / 2, active ? WHITE : MUTED);
    }

    public static void button(GuiGraphics g, Font font, int x, int y, int w, int h,
                              String label, boolean hovered) {
        option(g, font, x, y, w, h, label, false, hovered);
    }
    public static void toggle(GuiGraphics g, Font font, int x, int y, int w, int h,
                              String label, boolean active, boolean hovered) {
        moduleRow(g, font, x, y, w, h, label, active, null, hovered);
    }
}
