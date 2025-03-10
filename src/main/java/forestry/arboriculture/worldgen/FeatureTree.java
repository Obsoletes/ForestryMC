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
package forestry.arboriculture.worldgen;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

import forestry.api.arboriculture.ITreeGenData;
import forestry.core.worldgen.FeatureHelper;

public abstract class FeatureTree extends FeatureArboriculture {
	private static final int minHeight = 4;
	private static final int maxHeight = 80;

	private final int baseHeight;
	private final int heightVariation;

	protected int girth;
	protected int height;

	protected FeatureTree(ITreeGenData tree, int baseHeight, int heightVariation) {
		super(tree);
		this.baseHeight = baseHeight;
		this.heightVariation = heightVariation;
	}

	@Override
	public Set<BlockPos> generateTrunk(LevelAccessor world, RandomSource rand, TreeBlockTypeLog wood, BlockPos startPos) {
		FeatureHelper.generateTreeTrunk(world, rand, wood, startPos, height, girth, 0, 0, null, 0);
		return Set.of();
	}

	@Override
	protected void generateLeaves(LevelAccessor world, RandomSource rand, TreeBlockTypeLeaf leaf, TreeContour contour, BlockPos startPos) {
		int leafHeight = height + 1;
		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.offset(0, leafHeight--, 0), girth, girth, 1, FeatureHelper.EnumReplaceMode.AIR, contour);
		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.offset(0, leafHeight--, 0), girth, 0.5f + girth, 1, FeatureHelper.EnumReplaceMode.AIR, contour);
		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.offset(0, leafHeight--, 0), girth, 1.9f + girth, 1, FeatureHelper.EnumReplaceMode.AIR, contour);
		FeatureHelper.generateCylinderFromTreeStartPos(world, leaf, startPos.offset(0, leafHeight, 0), girth, 1.9f + girth, 1, FeatureHelper.EnumReplaceMode.AIR, contour);
	}

	@Override
	protected void generateExtras(LevelAccessor world, RandomSource rand, BlockPos startPos) {
		if (hasPods()) {
			FeatureHelper.generatePods(tree, world, rand, startPos, height, minPodHeight, girth, FeatureHelper.EnumReplaceMode.AIR);
		}
	}

	@Override
	@Nullable
	public BlockPos getValidGrowthPos(LevelAccessor world, BlockPos pos) {
		return tree.getGrowthPos(tree.getDefaultGenome(), world, pos, girth, height);
	}

	@Override
	public final void preGenerate(LevelAccessor world, RandomSource rand, BlockPos startPos) {
		super.preGenerate(world, rand, startPos);
		height = determineHeight(world, rand, baseHeight, heightVariation);
		girth = tree.getGirth(tree.getDefaultGenome());
	}

	protected int modifyByHeight(LevelAccessor world, int val, int min, int max) {
		//ITreeModifier treeModifier = SpeciesUtil.TREE_TYPE.get().getTreekeepingMode(world);
		int determined = Math.round(val * tree.getHeightModifier(tree.getDefaultGenome()));/* * treeModifier.getHeightModifier(tree.getGenome(), 1f)*/
		return determined < min ? min : Math.min(determined, max);
	}

	private int determineHeight(LevelAccessor world, RandomSource rand, int required, int variation) {
		//ITreeModifier treeModifier = SpeciesUtil.TREE_TYPE.get().getTreekeepingMode(world);
		int baseHeight = required + rand.nextInt(variation);
		int height = Math.round(baseHeight * tree.getHeightModifier(tree.getDefaultGenome()));/* * treeModifier.getHeightModifier(tree.getGenome(), 1f)*/
		return height < minHeight ? minHeight : Math.min(height, maxHeight);
	}
}
