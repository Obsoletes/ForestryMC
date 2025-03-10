/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import java.util.List;

import net.minecraft.world.item.ItemStack;

/**
 * Can be implemented by tile entities which can bear fruit.
 *
 * @author SirSengir
 */
public interface IFruitBearer {
	/**
	 * @return true if the actual tile can bear fruits.
	 */
	boolean hasFruit();

	/**
	 * Picks the fruits of this tile, resetting it to unripe fruits.
	 *
	 * @param tool Tool used in picking the fruits. May be empty.
	 * @return Picked fruits.
	 */
	List<ItemStack> pickFruit(ItemStack tool);

	/**
	 * @return float indicating the ripeness of the fruit with >= 1.0f indicating full ripeness.
	 */
	float getRipeness();

	/**
	 * Increases the ripeness of the fruit. Adding 1.0f will fully ripen the fruit.
	 *
	 * @param add Float to add to the ripeness. Will truncate to valid values.
	 */
	void addRipeness(float add);
}
