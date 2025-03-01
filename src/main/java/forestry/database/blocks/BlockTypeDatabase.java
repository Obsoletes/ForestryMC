package forestry.database.blocks;

import java.util.function.Supplier;

import forestry.core.blocks.IBlockTypeCustom;
import forestry.core.blocks.IMachineProperties;
import forestry.core.blocks.MachineProperties;
import forestry.core.tiles.TileForestry;
import forestry.database.features.DatabaseTiles;
import forestry.modules.features.FeatureTileType;

public enum BlockTypeDatabase implements IBlockTypeCustom {
	DATABASE(DatabaseTiles.DATABASE, "database");

	private final IMachineProperties machineProperties;

	<T extends TileForestry> BlockTypeDatabase(FeatureTileType<T> teClass, String name) {
		this.machineProperties = new MachineProperties.Builder<>(teClass, name).create();
	}

	@Override
	public IMachineProperties getMachineProperties() {
		return machineProperties;
	}

	@Override
	public String getSerializedName() {
		return getMachineProperties().getSerializedName();
	}
}
