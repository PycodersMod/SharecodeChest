package com.pycoder.sharecodechest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.pycoder.sharecodechest.block.SpecialLootBlock;
import com.pycoder.sharecodechest.block.SpecialLootBlockEntity;
import com.pycoder.sharecodechest.registry.ModRegistry;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;

public class SpecialLootBlockEntityRenderer implements BlockEntityRenderer<SpecialLootBlockEntity> {
    private final ModelPart lid;
    private final ModelPart lock;
    private final ModelPart bottom;

    public SpecialLootBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart chest = context.bakeLayer(ModelLayers.CHEST);
        bottom = chest.getChild("bottom");
        lid = chest.getChild("lid");
        lock = chest.getChild("lock");
    }

    @Override
    public void render(SpecialLootBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (blockEntity.getBlockState().is(ModRegistry.LOOT_BARREL.get())) {
            return;
        }

        Direction facing = blockEntity.getBlockState().getValue(SpecialLootBlock.FACING);
        Material material = blockEntity.getBlockState().is(ModRegistry.LOOT_TRAPPED_CHEST.get()) ? Sheets.CHEST_TRAP_LOCATION : Sheets.CHEST_LOCATION;
        VertexConsumer buffer = material.buffer(bufferSource, RenderType::entityCutout);

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        lid.xRot = 0.0F;
        lock.xRot = 0.0F;
        lid.render(poseStack, buffer, packedLight, packedOverlay);
        lock.render(poseStack, buffer, packedLight, packedOverlay);
        bottom.render(poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
