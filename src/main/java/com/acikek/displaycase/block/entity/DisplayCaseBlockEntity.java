package com.acikek.displaycase.block.entity;

import com.acikek.displaycase.DisplayCase;
import com.acikek.displaycase.advancement.DisplayCaseExplodeCriterion;
import com.acikek.displaycase.advancement.ModCriteria;
import com.acikek.displaycase.block.DisplayCaseBlock;
import com.acikek.displaycase.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class DisplayCaseBlockEntity extends BlockEntity {

	public static BlockEntityType<DisplayCaseBlockEntity> BLOCK_ENTITY_TYPE;

	public static final String DATA_KEY = "BlockEntityData";

    public ItemStack stack = ItemStack.EMPTY;
    public DyeColor color = null;
    public boolean waxed = false;
	public boolean rainbow = false;
	public int rotation = 0;
	public float cachedAngle = 0f;
	public int countdown = 0;

    public DisplayCaseBlockEntity(BlockPos pos, BlockState state) {
        super(BLOCK_ENTITY_TYPE, pos, state);
    }

	public static void tick(World world, BlockPos pos, BlockState state, DisplayCaseBlockEntity blockEntity) {
		if (!blockEntity.waxed) {
			blockEntity.rotation++;
			if (blockEntity.rotation == 360) {
				blockEntity.rotation = 0;
			}
		}
		if (blockEntity.countdown > 0) {
			blockEntity.countdown--;
		}
		if (!world.isClient() && blockEntity.countdown == 1) {
			world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 3.0f, Explosion.DestructionType.DESTROY);
			for (ServerPlayerEntity entity : world.getEntitiesByClass(ServerPlayerEntity.class, new Box(pos).expand(10, 5, 10), EntityPredicates.EXCEPT_SPECTATOR)) {
				ModCriteria.DISPLAY_CASE_EXPLODE.trigger(entity);
			}
		}
	}

    public void setWaxed(boolean waxed, World world, PlayerEntity player, BlockPos pos, ItemStack handStack) {
        this.waxed = waxed;
        markDirty();
		DisplayCaseBlock.triggerItemUsed(player, pos, handStack);
		if (waxed) {
			DisplayCaseBlock.decrementHandStack(player, handStack);
			world.syncWorldEvent(player, WorldEvents.BLOCK_WAXED, pos, 0);
		}
		else {
			world.syncWorldEvent(player, WorldEvents.BLOCK_SCRAPED, pos, 0);
			world.playSound(null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
			if (!world.isClient()) {
				stack.damage(1, world.random, (ServerPlayerEntity) player);
			}
			cachedAngle = 0f;
		}
    }

    public void setItem(ItemStack stack, World world) {
        this.stack = stack;
        this.stack.setCount(1);
		rainbow = false;
        if (DisplayCaseBlock.isDisplayCase(stack) && world != null) {
            ItemStack stack1 = stack;
            for (int i = 0; i < 10; i++) {
                ItemStack stack2 = ItemStack.fromNbt(stack1.getOrCreateNbt().getCompound(DATA_KEY).getCompound("Item"));
                if (DisplayCaseBlock.isDisplayCase(stack2)) {
                    stack1 = stack2;
                    if (i == 9) {
                        world.playSound(null, pos, SoundEvents.AMBIENT_NETHER_WASTES_MOOD, SoundCategory.BLOCKS, 4.0f, 1.0f);
                        countdown = 160;
                    }
                }
				else {
					break;
				}
            }
        }
		else if (stack.getItem() instanceof SkullItem) {
			NbtCompound nbt = stack.getOrCreateNbt().getCompound("SkullOwner");
			if (nbt.containsUuid("Id")) {
				String id = nbt.getUuid("Id").toString();
				if (id.equals("8464971c-f5ac-4eb5-87aa-511a0eec65f6")
						|| id.equals("b4c163bb-ea00-4624-8a36-b58095a70408")
						|| id.equals("1d7b97ac-e5dc-45f6-8bb2-3eb8a5ef190e")) {
					rainbow = true;
				}
			}
		}
        markDirty();
    }

    public void setColor(DyeColor color) {
        this.color = color;
        markDirty();
    }

	public float getAngle(float tickDelta) {
		return rotation + tickDelta * 4;
	}

	public float getAndCacheAngle(float tickDelta) {
		if (waxed) {
			if (cachedAngle == 0f) {
				cachedAngle = getAngle(tickDelta);
			}
			return cachedAngle;
		}
		return getAngle(tickDelta);
	}

	public DyeColor getColor() {
		return rainbow ? DyeColor.values()[MathHelper.clamp((rotation % 80) / 5, 0, 15)] : color;
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.of(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return saveNbt(new NbtCompound());
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		saveNbt(nbt);
		super.writeNbt(nbt);
	}

	public NbtCompound saveNbt(NbtCompound nbt) {
		if (!stack.isEmpty()) {
			nbt.put("Item", stack.writeNbt(new NbtCompound()));
		}
        if (color != null) {
            nbt.putString("Color", color.getName());
        }
		nbt.putBoolean("Waxed", waxed);
		nbt.putBoolean("Rainbow", rainbow);
		nbt.putInt("Rotation", rotation);
		nbt.putFloat("CachedAngle", cachedAngle);
        return nbt;
    }

	public ItemStack saveStackNbt(ItemStack stack) {
		NbtCompound nbt = saveNbt(new NbtCompound());
		if (!nbt.isEmpty()) {
			stack.getOrCreateNbt().put(DATA_KEY, nbt);
		}
		return stack;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		loadNbt(nbt, null);
	}

    public void loadNbt(NbtCompound nbt, World world) {
		setItem(ItemStack.fromNbt(nbt.getCompound("Item")), world);
		if (nbt.contains("Color")) {
			this.color = DyeColor.byName(nbt.getString("Color"), DyeColor.BLACK);
		}
		waxed = nbt.getBoolean("Waxed");
		rainbow = nbt.getBoolean("Rainbow");
        rotation = nbt.getInt("Rotation");
		cachedAngle = nbt.getFloat("CachedAngle");
    }

	public static void register() {
		BLOCK_ENTITY_TYPE = Registry.register(
				Registry.BLOCK_ENTITY_TYPE,
				DisplayCase.id("display_case_block_entity"),
				FabricBlockEntityTypeBuilder
						.create(DisplayCaseBlockEntity::new, ModBlocks.DISPLAY_CASE)
						.build(null)
		);
	}
}
