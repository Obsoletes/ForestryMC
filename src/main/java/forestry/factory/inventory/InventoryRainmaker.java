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
package forestry.factory.inventory;

import net.minecraft.world.item.ItemStack;

import forestry.api.fuels.FuelManager;
import forestry.api.fuels.RainSubstrate;
import forestry.core.inventory.InventoryAdapterTile;
import forestry.factory.tiles.TileMillRainmaker;

public class InventoryRainmaker extends InventoryAdapterTile<TileMillRainmaker> {
	private static final int SLOT_SUBSTRATE = 0;

	public InventoryRainmaker(TileMillRainmaker tile) {
		super(tile, 1, "items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack stack) {
		if (slotIndex == SLOT_SUBSTRATE) {
			if (FuelManager.rainSubstrate.containsKey(stack) && tile.charge == 0 && tile.progress == 0) {
				RainSubstrate substrate = FuelManager.rainSubstrate.get(stack);
				if (tile.getLevel().isRaining() && substrate.reverse()) {
					return true;
				} else {
					return !tile.getLevel().isRaining() && !substrate.reverse();
				}
			}
		}

		return false;
	}

	@Override
	public void setItem(int slotIndex, ItemStack itemStack) {
		if (slotIndex == SLOT_SUBSTRATE) {
			RainSubstrate substrate = FuelManager.rainSubstrate.get(itemStack);
			if (substrate != null && substrate.item().sameItem(itemStack)) {
				tile.addCharge(substrate);
			}
		}
	}
}
