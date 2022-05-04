package com.acikek.displaycase.client.render;

import com.acikek.displaycase.block.entity.DisplayCaseBlockEntity;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Random;

public class DisplayCaseRenderer implements BlockEntityRenderer<DisplayCaseBlockEntity> {

	public static final Map<DyeColor, Block> ITEM_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
		map.put(DyeColor.WHITE, Blocks.WHITE_STAINED_GLASS);
		map.put(DyeColor.ORANGE, Blocks.ORANGE_STAINED_GLASS);
		map.put(DyeColor.MAGENTA, Blocks.MAGENTA_STAINED_GLASS);
		map.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_STAINED_GLASS);
		map.put(DyeColor.YELLOW, Blocks.YELLOW_STAINED_GLASS);
		map.put(DyeColor.LIME, Blocks.LIME_STAINED_GLASS);
		map.put(DyeColor.PINK, Blocks.PINK_STAINED_GLASS);
		map.put(DyeColor.GRAY, Blocks.GRAY_STAINED_GLASS);
		map.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_STAINED_GLASS);
		map.put(DyeColor.CYAN, Blocks.CYAN_STAINED_GLASS);
		map.put(DyeColor.PURPLE, Blocks.PURPLE_STAINED_GLASS);
		map.put(DyeColor.BLUE, Blocks.BLUE_STAINED_GLASS);
		map.put(DyeColor.BROWN, Blocks.BROWN_STAINED_GLASS);
		map.put(DyeColor.GREEN, Blocks.GREEN_STAINED_GLASS);
		map.put(DyeColor.RED, Blocks.RED_STAINED_GLASS);
		map.put(DyeColor.BLACK, Blocks.BLACK_STAINED_GLASS);
	});

	public final Random random = new Random();

	public DisplayCaseRenderer(BlockEntityRendererFactory.Context ctx) {
	}

	@Override
	public void render(DisplayCaseBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.push();
		matrices.push();
		matrices.scale(0.7f, 0.7f, 0.7f);
		matrices.translate(0.215, 0.18, 0.215);
		int lightAbove = entity.getWorld() != null ? WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up()) : light;
		MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(
				ITEM_BY_DYE.getOrDefault(entity.getColor(), Blocks.GLASS).getDefaultState(),
				matrices, vertexConsumers, lightAbove, overlay
		);
		matrices.pop();
		ItemStack stack = entity.stack;
		if (stack != null && !stack.isEmpty()) {
			random.setSeed(Registry.ITEM.getRawId(stack.getItem()) + stack.getDamage());
			matrices.translate(0.5d, 0.25d, 0.5d);
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getAndCacheAngle(tickDelta)));
			if (stack.getItem() instanceof BlockItem) {
				matrices.scale(1.3f, 1.3f, 1.3f);
				if (!(stack.getItem() instanceof SkullItem)) {
					matrices.translate(0.0d, -0.1d, 0.0d);
				}
			}
			MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers, 0);
		}
		matrices.pop();
	}

	public static void register() {
		BlockEntityRendererRegistry.register(DisplayCaseBlockEntity.BLOCK_ENTITY_TYPE, DisplayCaseRenderer::new);
	}
}
