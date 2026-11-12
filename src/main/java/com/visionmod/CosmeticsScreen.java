package com.visionmod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/** Legacy cosmetics screen retained for compatibility, not linked from main menu. */
public final class CosmeticsScreen extends Screen {
    public CosmeticsScreen() { super(Component.literal("Vision Cosmetics")); }

    private static String next(java.util.List<String> items, String current) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(current)) return items.get((i + 1) % items.size());
        }
        return items.get(0);
    }

    @Override public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float delta) { }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        int x = VisionMenuTheme.x(width), y = VisionMenuTheme.y(height);
        VisionMenuTheme.panel(g, font, width, height, "VISION / COSMETICS",
            "Legacy models and cloud sync status");
        row(g, mouseX, mouseY, x, y + 52, "Head: " + VisionCosmetics.localHead);
        row(g, mouseX, mouseY, x, y + 83, "Back: " + VisionCosmetics.localBack);
        row(g, mouseX, mouseY, x, y + 114,
            "Other players: " + (VisionModClient.showOtherCosmetics ? "ON" : "OFF"));
        row(g, mouseX, mouseY, x, y + 145, "OPEN COSMETICS FOLDER");
        String status = "SYNC: " + VisionCosmetics.syncStatus;
        while (!status.isEmpty() && font.width(status) > 310)
            status = status.substring(0, status.length() - 1);
        g.drawCenteredString(font, status, x + 168, y + 184, VisionMenuTheme.MUTED);
        VisionMenuTheme.button(g, font, x + 13, y + 204, 310, 20,
            "BACK TO VISUALS", VisionMenuTheme.inside(mouseX, mouseY, x + 13, y + 204, 310, 20));
    }

    private void row(GuiGraphics g, int mx, int my, int x, int y, String title) {
        VisionMenuTheme.button(g, font, x + 13, y, 310, 25,
            title, VisionMenuTheme.inside(mx, my, x + 13, y, 310, 25));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (click.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) return super.mouseClicked(click, doubled);
        int x = VisionMenuTheme.x(width), y = VisionMenuTheme.y(height);
        double mx = click.x(), my = click.y();
        if (!VisionMenuTheme.inside(mx, my, x + 13, y + 51, 310, 175))
            return super.mouseClicked(click, doubled);
        if (VisionMenuTheme.inside(mx, my, x + 13, y + 52, 310, 25)) {
            VisionCosmetics.localHead = next(VisionCosmetics.localIds("head"), VisionCosmetics.localHead);
            VisionCosmetics.selectionChanged(); return true;
        }
        if (VisionMenuTheme.inside(mx, my, x + 13, y + 83, 310, 25)) {
            VisionCosmetics.localBack = next(VisionCosmetics.localIds("back"), VisionCosmetics.localBack);
            VisionCosmetics.selectionChanged(); return true;
        }
        if (VisionMenuTheme.inside(mx, my, x + 13, y + 114, 310, 25)) {
            VisionModClient.showOtherCosmetics = !VisionModClient.showOtherCosmetics;
            VisionPerformance.custom(); return true;
        }
        if (VisionMenuTheme.inside(mx, my, x + 13, y + 145, 310, 25)) {
            if (!CosmeticsFolder.open() && minecraft.player != null)
                minecraft.player.displayClientMessage(Component.literal("Vision Mod: Cannot open cosmetics folder"), false);
            return true;
        }
        if (VisionMenuTheme.inside(mx, my, x + 13, y + 204, 310, 20)) {
            minecraft.setScreen(new VisionScreen()); return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override public void onClose() { minecraft.setScreen(new VisionScreen()); }
    @Override public boolean isPauseScreen() { return false; }
}
