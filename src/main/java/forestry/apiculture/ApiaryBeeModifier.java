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
package forestry.apiculture;

import forestry.api.apiculture.IBeeModifier;
import forestry.api.genetics.IGenome;

public class ApiaryBeeModifier implements IBeeModifier {
	@Override
	public float modifyProductionSpeed(IGenome genome, float currentSpeed) {
		return 0.1f * currentSpeed;
	}
}
