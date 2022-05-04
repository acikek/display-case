package com.acikek.displaycase.advancement;

import com.acikek.displaycase.DisplayCase;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class DisplayCaseExplodeCriterion extends AbstractCriterion<DisplayCaseExplodeCriterion.Conditions> {

	public static final Identifier ID = DisplayCase.id("display_case_explode");

	@Override
	protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		return new Conditions(playerPredicate);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public void trigger(ServerPlayerEntity player) {
		trigger(player, conditions -> true);
	}

	public static class Conditions extends AbstractCriterionConditions {

		public Conditions(EntityPredicate.Extended extended) {
			super(ID, extended);
		}
	}
}
