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
package forestry.apiculture.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import forestry.core.blocks.BlockBase;

public class BlockApiculture extends BlockBase<BlockTypeApiculture> {
	public BlockApiculture(BlockTypeApiculture type) {
		super(type, Block.Properties.of(Material.WOOD).sound(SoundType.WOOD));
	}
}
