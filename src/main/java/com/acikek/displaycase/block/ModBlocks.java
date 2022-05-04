package com.acikek.displaycase.block;

import com.acikek.displaycase.DisplayCase;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

public class ModBlocks {

    public static final DisplayCaseBlock DISPLAY_CASE = new DisplayCaseBlock(DisplayCaseBlock.SETTINGS);

	public static void register() {
		Identifier id = DisplayCase.id("display_case");
		Registry.register(Registry.BLOCK, id, DISPLAY_CASE);
		Registry.register(Registry.ITEM, id, new BlockItem(DISPLAY_CASE, new QuiltItemSettings().group(ItemGroup.DECORATIONS)));
	}
}
