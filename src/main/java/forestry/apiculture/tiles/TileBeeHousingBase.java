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
package forestry.apiculture.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.IForestryApi;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.climate.IClimateProvider;
import forestry.api.core.HumidityType;
import forestry.api.core.TemperatureType;
import forestry.apiculture.gui.IGuiBeeHousingDelegate;
import forestry.core.climate.ClimateProvider;
import forestry.core.climate.FakeClimateProvider;
import forestry.core.network.IStreamableGui;
import forestry.core.owner.IOwnedTile;
import forestry.core.owner.IOwnerHandler;
import forestry.core.owner.OwnerHandler;
import forestry.core.render.ParticleRender;
import forestry.core.tiles.TileBase;

public abstract class TileBeeHousingBase extends TileBase implements IBeeHousing, IOwnedTile, IClimateProvider, IGuiBeeHousingDelegate, IStreamableGui {
	private final String hintKey;
	private final OwnerHandler ownerHandler = new OwnerHandler();
	private final IBeekeepingLogic beeLogic;
	protected IClimateProvider climate = FakeClimateProvider.INSTANCE;

	// CLIENT
	private int breedingProgressPercent = 0;

	protected TileBeeHousingBase(BlockEntityType<?> type, BlockPos pos, BlockState state, String hintKey) {
		super(type, pos, state);
		this.hintKey = hintKey;
		this.beeLogic = IForestryApi.INSTANCE.getHiveManager().createBeekeepingLogic(this);
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		this.climate = new ClimateProvider(level, this.worldPosition);
	}

	@Override
	public String getHintKey() {
		return hintKey;
	}

	@Override
	public IBeekeepingLogic getBeekeepingLogic() {
		return beeLogic;
	}

	/* LOADING & SAVING */
	@Override
	public void saveAdditional(CompoundTag compoundNBT) {
		super.saveAdditional(compoundNBT);
		beeLogic.write(compoundNBT);
		ownerHandler.write(compoundNBT);
	}

	@Override
	public void load(CompoundTag compoundNBT) {
		super.load(compoundNBT);
		beeLogic.read(compoundNBT);
		ownerHandler.read(compoundNBT);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag updateTag = super.getUpdateTag();
		beeLogic.write(updateTag);
		ownerHandler.write(updateTag);
		return updateTag;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		beeLogic.read(tag);
		ownerHandler.read(tag);
	}

	@Override
	public IOwnerHandler getOwnerHandler() {
		return ownerHandler;
	}

	/* ICLIMATISED */
	@Override
	public TemperatureType temperature() {
		return climate.temperature();
	}

	@Override
	public HumidityType humidity() {
		return climate.humidity();
	}

	/* UPDATING */
	@Override
	public void clientTick(Level level, BlockPos pos, BlockState state) {
		if (beeLogic.canDoBeeFX() && updateOnInterval(4)) {
			beeLogic.doBeeFX();

			if (updateOnInterval(50)) {
				doPollenFX(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void doPollenFX(Level world, double xCoord, double yCoord, double zCoord) {
		double fxX = xCoord + 0.5F;
		double fxY = yCoord + 0.25F;
		double fxZ = zCoord + 0.5F;
		float distanceFromCenter = 0.6F;
		float leftRightSpreadFromCenter = distanceFromCenter * (world.random.nextFloat() - 0.5F);
		float upSpread = world.random.nextFloat() * 6F / 16F;
		fxY += upSpread;

		ParticleRender.addEntityHoneyDustFX(world, fxX - distanceFromCenter, fxY, fxZ + leftRightSpreadFromCenter);
		ParticleRender.addEntityHoneyDustFX(world, fxX + distanceFromCenter, fxY, fxZ + leftRightSpreadFromCenter);
		ParticleRender.addEntityHoneyDustFX(world, fxX + leftRightSpreadFromCenter, fxY, fxZ - distanceFromCenter);
		ParticleRender.addEntityHoneyDustFX(world, fxX + leftRightSpreadFromCenter, fxY, fxZ + distanceFromCenter);
	}

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state) {
		if (beeLogic.canWork()) {
			beeLogic.doWork();
		}
	}

	@Override
	public int getHealthScaled(int i) {
		return breedingProgressPercent * i / 100;
	}

	@Override
	public void writeGuiData(FriendlyByteBuf data) {
		data.writeVarInt(beeLogic.getBeeProgressPercent());
	}

	@Override
	public void readGuiData(FriendlyByteBuf data) {
		breedingProgressPercent = data.readVarInt();
	}

	// / IBEEHOUSING
	@Override
	public Holder<Biome> getBiome() {
		return level.getBiome(getBlockPos());
	}

	//TODO check this call
	@Override
	public int getBlockLightValue() {
		return level.getMaxLocalRawBrightness(getBlockPos().above());
	}

	@Override
	public boolean canBlockSeeTheSky() {
		return level.canSeeSky(getBlockPos().above());
	}

	@Override
	public boolean isRaining() {
		return level.isRainingAt(getBlockPos().above());
	}

	@Override
	public GameProfile getOwner() {
		return getOwnerHandler().getOwner();
	}

	@Override
	public Vec3 getBeeFXCoordinates() {
		return new Vec3(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
	}
}
