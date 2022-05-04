package com.acikek.displaycase.client.render;

import com.acikek.displaycase.block.ModBlocks;
import com.acikek.displaycase.block.entity.DisplayCaseBlockEntity;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class DisplayCaseItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

	@Override
	public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		DisplayCaseBlockEntity blockEntity = new DisplayCaseBlockEntity(BlockPos.ORIGIN, ModBlocks.DISPLAY_CASE.getDefaultState());
		blockEntity.loadNbt(stack.getOrCreateNbt().getCompound(DisplayCaseBlockEntity.DATA_KEY), null);
		if (!blockEntity.waxed && MinecraftClient.getInstance().world != null) {
			blockEntity.rotation = (int) (MinecraftClient.getInstance().world.getTime() % 360L);
		}
		MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(blockEntity, matrices, vertexConsumers, light, overlay);
		MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(((BlockItem) stack.getItem()).getBlock().getDefaultState(), matrices, vertexConsumers, light, overlay);
	}

	public static void register() {
		BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.DISPLAY_CASE, new DisplayCaseItemRenderer());
	}
}
