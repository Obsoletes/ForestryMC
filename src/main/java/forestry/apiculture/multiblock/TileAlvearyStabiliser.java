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
package forestry.apiculture.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutation;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.apiculture.blocks.BlockAlvearyType;

public class TileAlvearyStabiliser extends TileAlveary implements IAlvearyComponent.BeeModifier {
	private static final IBeeModifier MODIFIER = new IBeeModifier() {
		@Override
		public float modifyMutationChance(IGenome genome, IGenome mate, IMutation<IBeeSpecies> mutation, float currentChance) {
			return 0.0f;
		}
	};

	public TileAlvearyStabiliser(BlockPos pos, BlockState state) {
		super(BlockAlvearyType.STABILISER, pos, state);
	}

	@Override
	public IBeeModifier getBeeModifier() {
		return MODIFIER;
	}
}
