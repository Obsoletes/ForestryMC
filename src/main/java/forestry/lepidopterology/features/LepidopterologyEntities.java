package forestry.lepidopterology.features;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;

import forestry.api.modules.ForestryModuleIds;
import forestry.lepidopterology.entities.EntityButterfly;
import forestry.modules.features.FeatureEntityType;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class LepidopterologyEntities {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ForestryModuleIds.LEPIDOPTEROLOGY);

	public static final FeatureEntityType<EntityButterfly> BUTTERFLY = REGISTRY.entity(EntityButterfly::new, MobCategory.CREATURE, "butterfly", builder -> builder.sized(0.5f, 0.25f), Mob::createMobAttributes);
}
