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
package forestry.core.inventory;

import java.util.Optional;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import forestry.api.genetics.capability.IIndividualHandlerItem;
import forestry.core.tiles.TileAnalyzer;
import forestry.core.utils.GeneticsUtil;
import forestry.core.utils.SlotUtil;
import forestry.core.utils.SpeciesUtil;

public class InventoryAnalyzer extends InventoryAdapterTile<TileAnalyzer> {
	public static final short SLOT_ANALYZE = 0;
	public static final short SLOT_CAN = 1;
	public static final short SLOT_INPUT_1 = 2;
	public static final short SLOT_INPUT_COUNT = 6;
	public static final short SLOT_OUTPUT_1 = 8;
	public static final short SLOT_OUTPUT_COUNT = 4;

	public InventoryAnalyzer(TileAnalyzer analyzer) {
		super(analyzer, 12, "Items");
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack stack) {
		if (SlotUtil.isSlotInRange(slotIndex, SLOT_INPUT_1, SLOT_INPUT_COUNT)) {
			return IIndividualHandlerItem.isIndividual(stack);
		} else if (slotIndex == SLOT_CAN) {
			Optional<FluidStack> fluid = FluidUtil.getFluidContained(stack);
			return fluid.map(f -> tile.getTankManager().canFillFluidType(f)).orElse(false);
		}

		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int slotIndex, ItemStack stack, Direction side) {
		return SlotUtil.isSlotInRange(slotIndex, SLOT_OUTPUT_1, SLOT_OUTPUT_COUNT);
	}

	@Override
	public void setItem(int slotId, ItemStack itemStack) {
		if (!SpeciesUtil.TREE_TYPE.get().isMember(itemStack)) {
			itemStack = GeneticsUtil.convertToGeneticEquivalent(itemStack);
		}

		super.setItem(slotId, itemStack);
	}
}
