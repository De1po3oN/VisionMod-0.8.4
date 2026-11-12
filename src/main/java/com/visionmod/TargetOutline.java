package com.visionmod;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

/** Optional tinted overlay on the targeted block (keeps vanilla outline). */
public final class TargetOutline {
    private TargetOutline() { }
    public static void init() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (!VisionModClient.enabled || !VisionExtras.targetOutline ||
                context.consumers() == null || context.matrices() == null) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null || !(mc.hitResult instanceof BlockHitResult hit)) return;
            var pos = hit.getBlockPos();
            Vec3 camera = mc.gameRenderer.getMainCamera().position();
            VertexConsumer lines = context.consumers().getBuffer(RenderTypes.lines());
            ShapeRenderer.renderShape(context.matrices(), lines, Shapes.block(),
                pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z,
                VisionExtras.outlineColor, 1.5F);
        });
    }
}
