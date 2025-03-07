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
package forestry.apiculture.genetics.effects;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import forestry.api.genetics.IGenome;
import forestry.core.render.ParticleRender;

public class PotionBeeEffect extends ThrottledBeeEffect {
	private final MobEffect potion;
	private final int potionFXColor;
	private final int duration;
	private final float chance;

	public PotionBeeEffect(boolean dominant, MobEffect potion, int duration) {
		this(dominant, potion, duration, 200, 1.0f);
	}

	public PotionBeeEffect(boolean dominant, MobEffect potion, int duration, int throttle, float chance) {
		super(dominant, throttle, true, false);
		this.potion = potion;
		this.duration = duration;
		this.chance = chance;

		Collection<MobEffectInstance> potionEffects = Collections.singleton(new MobEffectInstance(potion, 1, 0));
		this.potionFXColor = PotionUtils.getColor(potionEffects);
	}

	@Override
	public IEffectData doEffectThrottled(IGenome genome, IEffectData storedData, IBeeHousing housing) {
		RandomSource rand = housing.getWorldObj().random;
		List<LivingEntity> entities = ThrottledBeeEffect.getEntitiesInRange(genome, housing, LivingEntity.class);

		for (LivingEntity entity : entities) {
			if (rand.nextFloat() >= chance) {
				continue;
			}

			int dur = this.duration;
			if (potion.getCategory() == MobEffectCategory.HARMFUL) {
				// Entities are not attacked if they wear a full set of apiarist's armor.
				int count = BeeManager.armorApiaristHelper.wearsItems(entity, this, true);
				if (count >= 4) {
					continue; // Full set, no damage/effect
				} else if (count == 3) {
					dur = this.duration / 4;
				} else if (count == 2) {
					dur = this.duration / 2;
				} else if (count == 1) {
					dur = this.duration * 3 / 4;
				}
			} else {
				// don't apply positive effects to mobs
				if (entity instanceof Enemy) {
					continue;
				}
			}

			entity.addEffect(new MobEffectInstance(potion, dur, 0));
		}

		return storedData;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IEffectData doFX(IGenome genome, IEffectData storedData, IBeeHousing housing) {
		Level level = housing.getWorldObj();
		if (level.random.nextBoolean()) {
			super.doFX(genome, storedData, housing);
		} else {
			Vec3 beeFXCoordinates = housing.getBeeFXCoordinates();
			ParticleRender.addEntityPotionFX(level, beeFXCoordinates.x, beeFXCoordinates.y + 0.5, beeFXCoordinates.z, potionFXColor);
		}
		return storedData;
	}
}
