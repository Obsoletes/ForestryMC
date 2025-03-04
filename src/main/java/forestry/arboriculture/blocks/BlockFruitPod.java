/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.arboriculture.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.HitResult;

import forestry.arboriculture.tiles.TileFruitPod;
import forestry.core.tiles.TileUtil;
import forestry.core.utils.BlockUtil;
import forestry.core.utils.ItemStackUtil;

public class BlockFruitPod extends CocoaBlock implements EntityBlock {
	private final ForestryPodType podType;

	public BlockFruitPod(ForestryPodType podType) {
		super(BlockSapling.Properties.of(Material.PLANT)
				.randomTicks()
				.strength(0.2f, 3.0f)
				.sound(SoundType.WOOD));
		this.podType = podType;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		TileFruitPod tile = TileUtil.getTile(world, pos, TileFruitPod.class);
		if (tile == null) {
			return ItemStack.EMPTY;
		}
		return tile.getPickBlock();
	}

	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		if (!canSurvive(state, world, pos)) {
			dropResources(state, world, pos);
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			return;
		}

		TileFruitPod tile = TileUtil.getTile(world, pos, TileFruitPod.class);
		if (tile == null) {
			return;
		}

		tile.onBlockTick(world, pos, state, rand);
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
		if (!level.isClientSide && blockEntity instanceof TileFruitPod tile) {
			// todo replace with loot table
			for (ItemStack drop : tile.getDrops()) {
				ItemStackUtil.dropItemStackAsEntity(drop, level, pos);
			}
		}

		super.playerDestroy(level, player, pos, state, blockEntity, itemStack);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		Direction facing = state.getValue(FACING);
		return BlockUtil.isValidPodLocation(world, pos, facing, podType.getFruit().getLogTag());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileFruitPod(pos, state);
	}

	/* IGrowable */
	@Override
	public boolean isValidBonemealTarget(BlockGetter world, BlockPos pos, BlockState state, boolean isClient) {
		TileFruitPod podTile = TileUtil.getTile(world, pos, TileFruitPod.class);
		return podTile != null && podTile.canMature();
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource rand, BlockPos pos, BlockState state) {
		TileFruitPod podTile = TileUtil.getTile(world, pos, TileFruitPod.class);
		if (podTile != null) {
			podTile.addRipeness(0.5f);
		}
	}
}
