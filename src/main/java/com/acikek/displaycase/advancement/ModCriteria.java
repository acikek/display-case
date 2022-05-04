package com.acikek.displaycase.advancement;

import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;

public class ModCriteria {

	public static DisplayCaseExplodeCriterion DISPLAY_CASE_EXPLODE = new DisplayCaseExplodeCriterion();
	public static DisplayItemCriterion DISPLAY_ITEM = new DisplayItemCriterion();
	public static DyeDisplayCaseCriterion DYE_DISPLAY_CASE = new DyeDisplayCaseCriterion();

	public static void register() {
		CriterionRegistry.register(DISPLAY_CASE_EXPLODE);
		CriterionRegistry.register(DISPLAY_ITEM);
		CriterionRegistry.register(DYE_DISPLAY_CASE);
	}
}
