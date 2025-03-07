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
package forestry.core.gui.slots;

import javax.annotation.Nullable;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import forestry.api.ForestryConstants;
import forestry.core.tiles.IFilterSlotDelegate;

/**
 * Slot which only takes specific items, specified by the IFilterSlotDelegate.
 */
public class SlotFiltered extends SlotWatched implements ISlotTextured {
	private final IFilterSlotDelegate filterSlotDelegate;
	@Nullable
	private ResourceLocation backgroundTexture = null;
	private ResourceLocation blockedTexture = ForestryConstants.forestry("slots/blocked");

	public <T extends Container & IFilterSlotDelegate> SlotFiltered(T inventory, int slotIndex, int xPos, int yPos) {
		super(inventory, slotIndex, xPos, yPos);
		this.filterSlotDelegate = inventory;
	}

	@Override
	public boolean mayPlace(ItemStack itemstack) {
		int slotIndex = getSlotIndex();
		return !filterSlotDelegate.isLocked(slotIndex) &&
				(itemstack.isEmpty() || filterSlotDelegate.canSlotAccept(slotIndex, itemstack));
	}

	public SlotFiltered setBlockedTexture(String ident) {
		blockedTexture = ForestryConstants.forestry(ident);
		return this;
	}

	public SlotFiltered setBackgroundTexture(String backgroundTexture) {
		this.backgroundTexture = ForestryConstants.forestry(backgroundTexture);
		return this;
	}

	@Nullable
	@Override
	public ResourceLocation getBackgroundTexture() {
		ItemStack stack = getItem();
		if (!mayPlace(stack)) {
			return blockedTexture;
		} else if (backgroundTexture != null) {
			return backgroundTexture;
		} else {
			return null;
		}
	}
}
