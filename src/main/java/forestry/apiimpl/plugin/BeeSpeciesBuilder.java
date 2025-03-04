package forestry.apiimpl.plugin;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;

import forestry.api.apiculture.IBeeJubilance;
import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.apiculture.genetics.IBeeSpeciesType;
import forestry.api.core.IProduct;
import forestry.api.plugin.IBeeSpeciesBuilder;
import forestry.apiculture.BeeSpecies;
import forestry.apiculture.genetics.DefaultBeeJubilance;

public class BeeSpeciesBuilder extends SpeciesBuilder<IBeeSpeciesType, IBeeSpecies, IBeeSpeciesBuilder> implements IBeeSpeciesBuilder {
	private final ArrayList<IProduct> products = new ArrayList<>();
	private final ArrayList<IProduct> specialties = new ArrayList<>();
	private int bodyColor = 0xffdc16;
	private int stripesColor = 0;
	private int outlineColor = -1;
	private boolean nocturnal;
	private IBeeJubilance jubilance = DefaultBeeJubilance.INSTANCE;

	public BeeSpeciesBuilder(ResourceLocation id, String genus, String species, MutationsRegistration mutations) {
		super(id, genus, species, mutations);
	}

	@Override
	public ISpeciesFactory<IBeeSpeciesType, IBeeSpecies, IBeeSpeciesBuilder> createSpeciesFactory() {
		return BeeSpecies::new;
	}

	@Override
	public IBeeSpeciesBuilder addProduct(IProduct product) {
		this.products.add(product);
		return this;
	}

	@Override
	public IBeeSpeciesBuilder addSpecialty(IProduct specialty) {
		this.specialties.add(specialty);
		return this;
	}

	@Override
	public IBeeSpeciesBuilder setBody(Color color) {
		this.bodyColor = color.getRGB();
		return this;
	}

	@Override
	public IBeeSpeciesBuilder setStripes(Color color) {
		this.stripesColor = color.getRGB();
		return this;
	}

	@Override
	public IBeeSpeciesBuilder setOutline(Color color) {
		this.outlineColor = color.getRGB();
		return this;
	}

	@Override
	public IBeeSpeciesBuilder setJubilance(IBeeJubilance jubilance) {
		this.jubilance = jubilance;
		return this;
	}

	@Override
	public IBeeSpeciesBuilder setNocturnal(boolean nocturnal) {
		this.nocturnal = nocturnal;
		return this;
	}

	@Override
	public boolean isNocturnal() {
		return this.nocturnal;
	}

	@Override
	public List<IProduct> buildProducts() {
		return List.copyOf(this.products);
	}

	@Override
	public List<IProduct> buildSpecialties() {
		return List.copyOf(this.specialties);
	}

	@Override
	public int getBody() {
		return this.bodyColor;
	}

	@Override
	public int getStripes() {
		return this.stripesColor;
	}

	@Override
	public int getOutline() {
		return this.outlineColor;
	}

	@Override
	public IBeeJubilance getJubilance() {
		return this.jubilance;
	}
}
