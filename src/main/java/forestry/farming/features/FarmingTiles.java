package forestry.farming.features;

import forestry.api.modules.ForestryModuleIds;
import forestry.farming.blocks.EnumFarmBlockType;
import forestry.farming.tiles.TileFarmControl;
import forestry.farming.tiles.TileFarmGearbox;
import forestry.farming.tiles.TileFarmHatch;
import forestry.farming.tiles.TileFarmPlain;
import forestry.farming.tiles.TileFarmValve;
import forestry.modules.features.FeatureProvider;
import forestry.modules.features.FeatureTileType;
import forestry.modules.features.IFeatureRegistry;
import forestry.modules.features.ModFeatureRegistry;

@FeatureProvider
public class FarmingTiles {
	private static final IFeatureRegistry REGISTRY = ModFeatureRegistry.get(ForestryModuleIds.FARMING);

	public static final FeatureTileType<TileFarmControl> CONTROL = REGISTRY.tile(TileFarmControl::new, "control", () -> FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.CONTROL));
	public static final FeatureTileType<TileFarmGearbox> GEARBOX = REGISTRY.tile(TileFarmGearbox::new, "gearbox", () -> FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.GEARBOX));
	public static final FeatureTileType<TileFarmHatch> HATCH = REGISTRY.tile(TileFarmHatch::new, "hatch", () -> FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.HATCH));
	public static final FeatureTileType<TileFarmPlain> PLAIN = REGISTRY.tile(TileFarmPlain::new, "plain", () -> FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.PLAIN));
	public static final FeatureTileType<TileFarmValve> VALVE = REGISTRY.tile(TileFarmValve::new, "valve", () -> FarmingBlocks.FARM.getRowBlocks(EnumFarmBlockType.VALVE));
}
