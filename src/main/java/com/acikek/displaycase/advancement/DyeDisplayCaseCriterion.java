package com.acikek.displaycase.advancement;

import com.acikek.displaycase.DisplayCase;
import com.google.gson.JsonObject;
import lib.EnumPredicate;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class DyeDisplayCaseCriterion extends AbstractCriterion<DyeDisplayCaseCriterion.Conditions> {

    public static final Identifier ID = DisplayCase.id("dye_display_case");

	@Override
	protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		EnumPredicate<DyeColor> color = EnumPredicate.fromJson(obj.get("color"), DyeColor::valueOf);
		return new Conditions(playerPredicate, color);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public void trigger(ServerPlayerEntity player, DyeColor color) {
		trigger(player, conditions -> conditions.color.test(color));
	}

	public static class Conditions extends AbstractCriterionConditions {

		public EnumPredicate<DyeColor> color;

		public Conditions(EntityPredicate.Extended extended, EnumPredicate<DyeColor> color) {
			super(ID, extended);
			this.color = color;
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject obj = super.toJson(predicateSerializer);
			obj.add("color", color.toJson());
			return obj;
		}
	}
}
