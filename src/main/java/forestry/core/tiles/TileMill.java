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
package forestry.core.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class TileMill extends TileBase {
	public float speed;
	public int stage = 0;
	public int charge = 0;
	public float progress;

	protected TileMill(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		speed = 0.01F;
	}

	@Override
	public void clientTick(Level level, BlockPos pos, BlockState state) {
		update(level, pos, false);
	}

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state) {
		update(level, pos, true);
	}

	@Override
	public void writeData(FriendlyByteBuf data) {
		super.writeData(data);
		data.writeInt(charge);
		data.writeFloat(speed);
		data.writeInt(stage);
	}

	@Override
	public void readData(FriendlyByteBuf data) {
		super.readData(data);
		charge = data.readInt();
		speed = data.readFloat();
		stage = data.readInt();
	}

	private void update(Level level, BlockPos pos, boolean isSimulating) {
		// Stop gracefully if discharged.
		if (charge <= 0) {
			if (stage > 0) {
				progress += speed;
			}
			if (progress > 0.5) {
				stage = 2;
			}
			if (progress > 1) {
				progress = 0;
				stage = 0;
			}
			return;
		}

		// Update blades
		progress += speed;
		if (stage <= 0) {
			stage = 1;
		}

		if (progress > 0.5 && stage == 1) {
			stage = 2;
			if (charge < 7 && isSimulating) {
				charge++;
				sendNetworkUpdate();
			}
		}
		if (progress > 1) {
			progress = 0;
			stage = 0;

			// Fully charged! Do something!
			if (charge >= 7) {
				activate(level, pos);
			}
		}

	}

	protected abstract void activate(Level level, BlockPos pos);
}
