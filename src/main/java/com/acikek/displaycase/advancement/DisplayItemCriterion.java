package com.acikek.displaycase.advancement;

import com.acikek.displaycase.DisplayCase;
import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class DisplayItemCriterion extends AbstractCriterion<DisplayItemCriterion.Conditions> {

	public static final Identifier ID = DisplayCase.id("display_item");

	@Override
	protected Conditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
		ItemPredicate item = ItemPredicate.fromJson(obj.get("item"));
		return new Conditions(playerPredicate, item);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack) {
		trigger(player, conditions -> conditions.item.test(stack));
	}

	public static class Conditions extends AbstractCriterionConditions {

		public ItemPredicate item;

		public Conditions(EntityPredicate.Extended extended, ItemPredicate item) {
			super(ID, extended);
			this.item = item;
		}

		@Override
		public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
			JsonObject obj = super.toJson(predicateSerializer);
			obj.add("item", item.toJson());
			return obj;
		}
	}
}
