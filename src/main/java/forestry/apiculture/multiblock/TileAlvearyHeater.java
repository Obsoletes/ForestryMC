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

import forestry.apiculture.blocks.BlockAlvearyType;

public class TileAlvearyHeater extends TileAlvearyClimatiser {
	public TileAlvearyHeater(BlockPos pos, BlockState state) {
		super(BlockAlvearyType.HEATER, pos, state, (byte) 1);
	}
}
