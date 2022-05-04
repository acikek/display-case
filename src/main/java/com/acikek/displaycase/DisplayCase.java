package com.acikek.displaycase;

import com.acikek.displaycase.advancement.ModCriteria;
import com.acikek.displaycase.block.ModBlocks;
import com.acikek.displaycase.block.entity.DisplayCaseBlockEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class DisplayCase implements ModInitializer {

    public static final String ID = "display_case";

	public static Identifier id(String key) {
		return new Identifier(ID, key);
	}

    public static final Logger LOGGER = LogManager.getLogger("displaycase");

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Displaying items...");
		ModBlocks.register();
		DisplayCaseBlockEntity.register();
		ModCriteria.register();
	}
}
