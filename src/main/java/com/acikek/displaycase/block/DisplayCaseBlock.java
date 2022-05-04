package com.acikek.displaycase.block;

import com.acikek.displaycase.DisplayCase;
import com.acikek.displaycase.advancement.ModCriteria;
import com.acikek.displaycase.block.entity.DisplayCaseBlockEntity;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;

import java.util.List;
import java.util.stream.Stream;

public class DisplayCaseBlock extends BlockWithEntity implements Waterloggable {

	public static final Settings SETTINGS = QuiltBlockSettings.of(Material.STONE).strength(0.5f);

	public static final VoxelShape SHAPE = Stream.of(
			VoxelShapes.combine(Block.createCuboidShape(6.875, 14.024999999999988, 8, 9.125, 15.524999999999988, 8),
					VoxelShapes.combine(Block.createCuboidShape(6.5, 13.424999999999994, 6.5, 9.5, 14.17499999999999, 9.5), VoxelShapes.combine(Block.createCuboidShape(6.5625, 12.587499999999991, 6.5625, 9.4375, 13.212499999999988, 9.4375), Block.createCuboidShape(8, 14.024999999999988, 6.875, 8, 15.524999999999988, 9.125), BooleanBiFunction.AND), BooleanBiFunction.AND), BooleanBiFunction.AND),
			Block.createCuboidShape(1.875, 1.2249999999999992, 1.875, 14.125, 13.47499999999999, 14.125),
			Block.createCuboidShape(2, 4.163336342344337e-17, 2, 14, 1.5, 14)).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, BooleanBiFunction.OR)).get();

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public DisplayCaseBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(WATERLOGGED, false));
    }

	public static boolean isDisplayCase(ItemStack stack) {
		return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof DisplayCaseBlock;
	}

	public static void decrementHandStack(PlayerEntity player, ItemStack handStack) {
		if (!player.isCreative()) {
			handStack.decrement(1);
		}
	}

	public static void triggerItemUsed(PlayerEntity player, BlockPos pos, ItemStack handStack) {
		if (player instanceof ServerPlayerEntity serverPlayer) {
			Criteria.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, handStack);
		}
	}

	public static void useItem(PlayerEntity player, BlockPos pos, ItemStack handStack) {
		decrementHandStack(player, handStack);
		triggerItemUsed(player, pos, handStack);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (hand == Hand.MAIN_HAND && world.getBlockEntity(pos) instanceof DisplayCaseBlockEntity blockEntity) {
			if (blockEntity.countdown > 0) {
				return ActionResult.PASS;
			}
			ItemStack handStack = player.getStackInHand(hand);
			SoundEvent event = null;
			if (!handStack.isEmpty()) {
				if (blockEntity.stack.isEmpty()) {
					blockEntity.setItem(handStack.copy(), world);
					if (!world.isClient()) {
						ModCriteria.DISPLAY_ITEM.trigger((ServerPlayerEntity) player, handStack);
					}
					useItem(player, pos, handStack);
					event = SoundEvents.BLOCK_GLASS_STEP;
				}
				else if (handStack.getItem() instanceof DyeItem dye && blockEntity.color != dye.getColor() && !blockEntity.rainbow) {
					blockEntity.setColor(dye.getColor());
					if (!world.isClient()) {
						ModCriteria.DYE_DISPLAY_CASE.trigger((ServerPlayerEntity) player, dye.getColor());
					}
					useItem(player, pos, handStack);
					event = SoundEvents.BLOCK_BASALT_BREAK;
				}
				else if (blockEntity.color != null && handStack.isOf(Items.WATER_BUCKET) && !blockEntity.rainbow) {
					blockEntity.setColor(null);
					triggerItemUsed(player, pos, handStack);
					if (!player.isCreative()) {
						player.setStackInHand(hand, Items.BUCKET.getDefaultStack());
					}
					event = SoundEvents.ITEM_BUCKET_EMPTY;
				}
				else if (handStack.isOf(Items.HONEYCOMB) && !blockEntity.waxed) {
					blockEntity.setWaxed(true, world, player, pos, handStack);
				}
				else if (handStack.getItem() instanceof AxeItem && blockEntity.waxed) {
					blockEntity.setWaxed(false, world, player, pos, handStack);
				}
				else {
					return ActionResult.PASS;
				}
			}
			else if (!blockEntity.stack.isEmpty()) {
				if (!player.isCreative()) {
					player.getInventory().offerOrDrop(blockEntity.stack.copy());
				}
				blockEntity.setItem(ItemStack.EMPTY, world);
				event = SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM;
			}
			if (event != null) {
				world.playSound(null, pos, event, SoundCategory.BLOCKS, 1.0f, 1.0f);
			}
		}
		return ActionResult.SUCCESS;
	}

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.onPlaced(world, pos, state, entity, stack);
        if (world.getBlockEntity(pos) instanceof DisplayCaseBlockEntity blockEntity) {
            blockEntity.loadNbt(stack.getOrCreateNbt().getCompound(DisplayCaseBlockEntity.DATA_KEY), world);
        }
    }

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		if (builder.get(LootContextParameters.BLOCK_ENTITY) instanceof DisplayCaseBlockEntity blockEntity) {
			ItemStack stack = asItem().getDefaultStack();
			return List.of(blockEntity.saveStackNbt(stack));
		}
		return super.getDroppedStacks(state, builder);
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		ItemStack stack = super.getPickStack(world, pos, state);
		if (world.getBlockEntity(pos) instanceof DisplayCaseBlockEntity blockEntity) {
			return blockEntity.saveStackNbt(stack);
		}
		return stack;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}

	@Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState state = getDefaultState();
		if (state.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
			return state.with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER));
		}
		return super.getPlacementState(ctx);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new DisplayCaseBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, DisplayCaseBlockEntity.BLOCK_ENTITY_TYPE, DisplayCaseBlockEntity::tick);
	}
}
