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

import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;

import forestry.core.config.Constants;
import forestry.core.network.IStreamable;
import forestry.core.utils.InventoryUtil;
import forestry.core.utils.NetworkUtil;

/**
 * With permission from Krapht.
 */
public class InventoryAdapter implements IInventoryAdapter, IStreamable {

	private final InventoryPlain inventory;
	private boolean allowAutomation = true;
	@Nullable
	private int[] slotMap;

	public InventoryAdapter(int size, String name) {
		this(size, name, 64);
	}

	public InventoryAdapter(int size, String name, int stackLimit) {
		this(new InventoryPlain(size, name, stackLimit));
	}

	public InventoryAdapter(InventoryPlain inventory) {
		this.inventory = inventory;
		configureSided();
	}

	public InventoryAdapter disableAutomation() {
		this.allowAutomation = false;
		return this;
	}

	/**
	 * @return Copy of this inventory. Stacks are copies.
	 */
	public InventoryAdapter copy() {
		InventoryAdapter copy = new InventoryAdapter(inventory.getContainerSize(), "TEST_TITLE_PLEASE_IGNORE", inventory.getMaxStackSize());

		for (int i = 0; i < inventory.getContainerSize(); i++) {
			if (!inventory.getItem(i).isEmpty()) {
				copy.setItem(i, inventory.getItem(i).copy());
			}
		}

		return copy;
	}

	/* IINVENTORY */
	@Override
	public boolean isEmpty() {
		return inventory.isEmpty();
	}

	@Override
	public int getContainerSize() {
		return inventory.getContainerSize();
	}

	@Override
	public ItemStack getItem(int slotId) {
		return inventory.getItem(slotId);
	}

	@Override
	public ItemStack removeItem(int slotId, int count) {
		return inventory.removeItem(slotId, count);
	}

	@Override
	public void setItem(int slotId, ItemStack itemstack) {
		inventory.setItem(slotId, itemstack);
	}

	@Override
	public int getMaxStackSize() {
		return inventory.getMaxStackSize();
	}

	@Override
	public void setChanged() {
		inventory.setChanged();
	}

	@Override
	public ItemStack removeItemNoUpdate(int slotIndex) {
		return inventory.removeItemNoUpdate(slotIndex);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public boolean canPlaceItem(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public boolean canSlotAccept(int slotIndex, ItemStack stack) {
		return true;
	}

	@Override
	public boolean isLocked(int slotIndex) {
		return false;
	}

	/* ISIDEDINVENTORY */
	@Override
	public int[] getSlotsForFace(Direction side) {
		if (allowAutomation && slotMap != null) {
			return slotMap;
		}
		return Constants.SLOTS_NONE;
	}

	private void configureSided() {
		int count = getContainerSize();
		slotMap = new int[count];
		for (int i = 0; i < count; i++) {
			slotMap[i] = i;
		}
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
		return canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
		return false;
	}

	/* SAVING & LOADING */
	@Override
	public void read(CompoundTag compoundNBT) {
		InventoryUtil.readFromNBT(this, inventory.getName(), compoundNBT);
	}

	@Override
	public CompoundTag write(CompoundTag compoundNBT) {
		InventoryUtil.writeToNBT(this, inventory.getName(), compoundNBT);
		return compoundNBT;
	}

	@Override
	public void writeData(FriendlyByteBuf data) {
		NetworkUtil.writeInventory(data, inventory);
	}

	@Override
	public void readData(FriendlyByteBuf data) {
		NetworkUtil.readInventory(data, inventory);
	}

	/* FIELDS */

	@Override
	public void clearContent() {
		inventory.clearContent();
	}
}
