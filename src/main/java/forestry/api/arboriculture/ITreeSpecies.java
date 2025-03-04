package forestry.api.arboriculture;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import forestry.api.arboriculture.genetics.ITree;
import forestry.api.arboriculture.genetics.ITreeSpeciesType;
import forestry.api.core.HumidityType;
import forestry.api.core.IProductProducer;
import forestry.api.core.ISpecialtyProducer;
import forestry.api.core.TemperatureType;
import forestry.api.genetics.ILifeStage;
import forestry.api.genetics.ISpecies;

public interface ITreeSpecies extends ISpecies<ITree>, ITreeGenData, IProductProducer, ISpecialtyProducer {
	@Override
	ITreeSpeciesType getType();

	ITreeGenerator getGenerator();

	/**
	 * @return The "decorative" form of this leaf block, with no block entity or genome. Used for pick-block and shears.
	 */
	ItemStack getDecorativeLeaves();

	/**
	 * @return The ideal temperature for this tree to grow in.
	 */
	TemperatureType getTemperature();

	/**
	 * @return The ideal humidity for this tree to grow in.
	 */
	HumidityType getHumidity();

	/**
	 * @return A list of mundane forms of this species's leaf blocks. Example: Vanilla leaves, Forestry decorative leaves
	 */
	List<BlockState> getVanillaLeafStates();

	/**
	 * @return A list of mundane item forms of this species's saplings. Example: Oak Sapling, Birch Sapling
	 */
	List<Item> getVanillaSaplingItems();

	int getGermlingColor(ILifeStage stage, int renderPass);

	/**
	 * @return The chance between 0 and 1 that this tree will spawn in a chunk during world generation. Default is 0.
	 */
	float getRarity();

	/**
	 * Determines if the block at the position is a leaves block with fruit on it.
	 *
	 * @param level The world.
	 * @param pos   The position of the block to check.
	 * @return {@code true} if this is a leaves block with fruit on it, false otherwise.
	 */
	boolean isFruitLeaf(LevelAccessor level, BlockPos pos);
}
