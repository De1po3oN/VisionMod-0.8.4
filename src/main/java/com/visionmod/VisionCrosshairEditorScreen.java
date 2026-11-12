package com.visionmod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/** 9 by 9 custom reticle painter; left click toggles pixels. */
public final class VisionCrosshairEditorScreen extends Screen {
    public VisionCrosshairEditorScreen() { super(Component.literal("Vision Mod / Crosshair Painter")); }
    @Override public void renderBackground(GuiGraphics g,int mx,int my,float delta) { }
    @Override public void render(GuiGraphics g,int mx,int my,float delta) {
        super.render(g,mx,my,delta);
        VisionMenuTheme.panel(g,font,width,height,"VISION / RETICLE","Left-click cells to draw your own crosshair");
        int x=VisionMenuTheme.x(width)+57,y=VisionMenuTheme.y(height)+24;
        final int size=14, gx=x+105, gy=y+50;
        for (int row=0;row<9;row++) for(int col=0;col<9;col++) {
            int bx=gx+col*size,by=gy+row*size;
            g.fill(bx,by,bx+size-1,by+size-1,
                VisionExtras.pixel(col,row)?VisionExtras.crosshairColor:VisionMenuTheme.SURFACE);
            if(VisionMenuTheme.inside(mx,my,bx,by,size-1,size-1))
                g.fill(bx+3,by+3,bx+size-4,by+size-4,0x66FFFFFF);
        }
        VisionMenuTheme.button(g,font,x+12,y+185,153,21,"CHANGE COLOR",
            VisionMenuTheme.inside(mx,my,x+12,y+185,153,21));
        VisionMenuTheme.button(g,font,x+171,y+185,153,21,"RESET DRAWING",
            VisionMenuTheme.inside(mx,my,x+171,y+185,153,21));
        VisionMenuTheme.button(g,font,x+12,y+207,312,19,"BACK TO EXTRAS",
            VisionMenuTheme.inside(mx,my,x+12,y+207,312,19));
    }
    @Override public boolean mouseClicked(MouseButtonEvent click,boolean doubled) {
        if(click.button()!=GLFW.GLFW_MOUSE_BUTTON_LEFT) return super.mouseClicked(click,doubled);
        int x=VisionMenuTheme.x(width)+57,y=VisionMenuTheme.y(height)+24;
        int gx=x+105,gy=y+50;
        double mx=click.x(),my=click.y();
        if(mx>=gx&&my>=gy&&mx<gx+126&&my<gy+126){
            int col=(int)(mx-gx)/14,row=(int)(my-gy)/14;
            VisionExtras.togglePixel(col,row);return true;
        }
        if(VisionMenuTheme.inside(mx,my,x+12,y+185,153,21)){
            VisionExtras.cycleCrosshairColor();return true;
        }
        if(VisionMenuTheme.inside(mx,my,x+171,y+185,153,21)){
            VisionExtras.resetPixels();return true;
        }
        if(VisionMenuTheme.inside(mx,my,x+12,y+207,312,19)){
            minecraft.setScreen(new VisionScreen(VisionScreen.Page.EXTRAS));return true;
        }
        return super.mouseClicked(click,doubled);
    }
    @Override public void onClose(){minecraft.setScreen(new VisionScreen(VisionScreen.Page.EXTRAS));}
    @Override public boolean isPauseScreen(){return false;}
}
