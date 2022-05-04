package com.acikek.displaycase.client;

import com.acikek.displaycase.block.ModBlocks;
import com.acikek.displaycase.client.render.DisplayCaseItemRenderer;
import com.acikek.displaycase.client.render.DisplayCaseRenderer;
import net.minecraft.client.render.RenderLayer;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;

public class DisplayCaseClient implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
		DisplayCaseRenderer.register();
		DisplayCaseItemRenderer.register();
		BlockRenderLayerMap.put(RenderLayer.getCutout(), ModBlocks.DISPLAY_CASE);
	}
}
