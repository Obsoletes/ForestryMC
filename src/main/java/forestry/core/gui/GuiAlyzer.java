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
package forestry.core.gui;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import forestry.api.core.ToleranceType;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.IMutationManager;
import forestry.api.genetics.ISpecies;
import forestry.api.genetics.ISpeciesType;
import forestry.api.genetics.ITaxon;
import forestry.api.genetics.alleles.AllelePair;
import forestry.api.genetics.alleles.IAllele;
import forestry.api.genetics.alleles.IChromosome;
import forestry.api.genetics.alleles.IRegistryChromosome;
import forestry.api.genetics.alleles.IValueAllele;
import forestry.api.genetics.alleles.IValueChromosome;
import forestry.api.genetics.capability.IIndividualHandlerItem;
import forestry.core.config.Constants;
import forestry.core.genetics.mutations.EnumMutateChance;
import forestry.core.gui.widgets.ItemStackWidget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.inventory.ItemInventoryAlyzer;
import forestry.core.render.ColourProperties;
import forestry.core.utils.Translator;

// Portable analyzer
public class GuiAlyzer extends GuiForestry<ContainerAlyzer> {
	public static final int COLUMN_0 = 12;
	public static final int COLUMN_1 = 90;
	public static final int COLUMN_2 = 155;

	private final ItemInventoryAlyzer itemInventory;

	public GuiAlyzer(ContainerAlyzer container, Inventory playerInv, Component name) {
		super(Constants.TEXTURE_PATH_GUI + "/portablealyzer.png", container, playerInv, Component.literal("GUI_ALYZER_TEST_TITLE"));

		this.itemInventory = container.inventory;
		this.imageWidth = 246;
		this.imageHeight = 238;
	}

	public static int getColorCoding(boolean dominant) {
		if (dominant) {
			return ColourProperties.INSTANCE.get("gui.beealyzer.dominant");
		} else {
			return ColourProperties.INSTANCE.get("gui.beealyzer.recessive");
		}
	}

	public final void drawLine(PoseStack transform, String text, int x, IIndividual individual, IChromosome<?> chromosome, boolean inactive) {
		if (!inactive) {
			textLayout.drawLine(transform, text, x, getColorCoding(individual.getGenome().getActiveAllele(chromosome).dominant()));
		} else {
			textLayout.drawLine(transform, text, x, getColorCoding(individual.getGenome().getInactiveAllele(chromosome).dominant()));
		}
	}

	public final void drawSplitLine(PoseStack transform, Component component, int x, int maxWidth, IIndividual individual, IChromosome<?> chromosome, boolean inactive) {
		if (!inactive) {
			textLayout.drawSplitLine(transform, component, x, maxWidth, getColorCoding(individual.getGenome().getActiveAllele(chromosome).dominant()));
		} else {
			textLayout.drawSplitLine(transform, component, x, maxWidth, getColorCoding(individual.getGenome().getInactiveAllele(chromosome).dominant()));
		}
	}

	public final void drawRow(PoseStack transform, Component text0, Component text1, Component text2, IIndividual individual, IChromosome<?> chromosome) {
		textLayout.drawRow(transform, text0, text1, text2, ColourProperties.INSTANCE.get("gui.screen"), getColorCoding(individual.getGenome().getActiveAllele(chromosome).dominant()),
				getColorCoding(individual.getGenome().getInactiveAllele(chromosome).dominant()));
	}

	public final void drawChromosomeRow(PoseStack transform, Component chromosomeName, IIndividual individual, IChromosome<?> chromosome) {
		IAllele active = individual.getGenome().getActiveAllele(chromosome);
		MutableComponent activeName = chromosome.getDisplayName(active.cast());
		IAllele inactive = individual.getGenome().getInactiveAllele(chromosome);
		MutableComponent inactiveName = chromosome.getDisplayName(inactive.cast());
		textLayout.drawRow(transform, chromosomeName, activeName, inactiveName, ColourProperties.INSTANCE.get("gui.screen"), getColorCoding(active.dominant()), getColorCoding(inactive.dominant()));
	}

	public <S extends ISpecies<?>> void drawSpeciesRow(PoseStack transform, Component text0, IIndividual individual, IRegistryChromosome<S> chromosome) {
		AllelePair<IValueAllele<S>> species = individual.getGenome().getAllelePair(chromosome);

		textLayout.drawLine(transform, text0, textLayout.column0);
		int columnwidth = textLayout.column2 - textLayout.column1 - 2;

		IValueAllele<S> activeSpecies = species.active();
		IValueAllele<S> inactiveSpecies = species.inactive();
		Map<ISpecies<?>, ItemStack> iconStacks = activeSpecies.value().getType().getAlyzerPlugin().getIconStacks();

		// todo fix Icon Stacks being empty for butterflies
		GuiUtil.drawItemStack(transform, this, iconStacks.get(activeSpecies.value()), leftPos + textLayout.column1 + columnwidth - 20, topPos + 10);
		GuiUtil.drawItemStack(transform, this, iconStacks.get(inactiveSpecies.value()), leftPos + textLayout.column2 + columnwidth - 20, topPos + 10);

		Component primaryName = chromosome.getDisplayName(activeSpecies);
		Component secondaryName = chromosome.getDisplayName(inactiveSpecies);

		drawSplitLine(transform, primaryName, textLayout.column1, columnwidth, individual, chromosome, false);
		drawSplitLine(transform, secondaryName, textLayout.column2, columnwidth, individual, chromosome, true);

		textLayout.newLine();
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(transform, partialTicks, mouseX, mouseY);
		widgetManager.clear();

		int specimenSlot = getSpecimenSlot();
		if (specimenSlot < ItemInventoryAlyzer.SLOT_ANALYZE_1) {
			drawAnalyticsOverview(transform);
			return;
		}

		ItemStack stackInSlot = itemInventory.getItem(specimenSlot);

		IIndividualHandlerItem.ifPresent(stackInSlot, individual -> {
			ISpeciesType<?, ?> type = individual.getType();

			switch (specimenSlot) {
				case ItemInventoryAlyzer.SLOT_ANALYZE_1 ->
						type.getAlyzerPlugin().drawAnalyticsPage1(transform, this, stackInSlot);
				case ItemInventoryAlyzer.SLOT_ANALYZE_2 ->
						type.getAlyzerPlugin().drawAnalyticsPage2(transform, this, stackInSlot);
				case ItemInventoryAlyzer.SLOT_ANALYZE_3 ->
						type.getAlyzerPlugin().drawAnalyticsPage3(transform, this, stackInSlot);
				case ItemInventoryAlyzer.SLOT_ANALYZE_4 -> drawAnalyticsPageMutations(transform, individual);
				case ItemInventoryAlyzer.SLOT_ANALYZE_5 -> drawAnalyticsPageClassification(transform, individual);
				default -> drawAnalyticsOverview(transform);
			}
		});
	}

	private int getSpecimenSlot() {
		for (int k = ItemInventoryAlyzer.SLOT_SPECIMEN; k <= ItemInventoryAlyzer.SLOT_ANALYZE_5; k++) {
			ItemStack stackInSlot = itemInventory.getItem(k);

			if (!stackInSlot.isEmpty() && IIndividualHandlerItem.filter(stackInSlot, IIndividual::isAnalyzed)) {
				return k;
			}
		}
		return -1;
	}

	public void drawAnalyticsOverview(PoseStack transform) {
		textLayout.startPage(transform);

		textLayout.newLine();
		Component title = Component.translatable("for.gui.portablealyzer");
		textLayout.drawCenteredLine(transform, title, 8, 208, ColourProperties.INSTANCE.get("gui.screen"));
		textLayout.newLine();

		getFontRenderer().drawWordWrap(Component.translatable("for.gui.portablealyzer.help"), leftPos + COLUMN_0 + 4, topPos + 42, 200, ColourProperties.INSTANCE.get("gui.screen"));
		textLayout.newLine();
		textLayout.newLine();
		textLayout.newLine();
		textLayout.newLine();

		textLayout.drawLine(transform, Component.translatable("for.gui.alyzer.overview").append(":"), COLUMN_0 + 4);
		textLayout.newLine();
		textLayout.drawLine(transform, Component.literal("I  : ").append(Component.translatable("for.gui.general")), COLUMN_0 + 4);
		textLayout.newLine();
		textLayout.drawLine(transform, Component.literal("II : ").append(Component.translatable("for.gui.environment")), COLUMN_0 + 4);
		textLayout.newLine();
		textLayout.drawLine(transform, Component.literal("III: ").append(Component.translatable("for.gui.produce")), COLUMN_0 + 4);
		textLayout.newLine();
		textLayout.drawLine(transform, Component.literal("IV : ").append(Component.translatable("for.gui.evolution")), COLUMN_0 + 4);

		textLayout.endPage(transform);
	}

	public final void drawAnalyticsPageClassification(PoseStack transform, IIndividual individual) {

		textLayout.startPage(transform);

		textLayout.drawLine(transform, Component.translatable("for.gui.alyzer.classification").append(":"), 12);
		textLayout.newLine();

		ArrayDeque<ITaxon> hierarchy = new ArrayDeque<>();
		ISpecies<?> species = individual.getSpecies();
		ITaxon taxon = species.getGenus();
		while (taxon != null) {
			if (!taxon.name().isEmpty()) {
				hierarchy.push(taxon);
			}
			taxon = taxon.parent();
		}

		boolean overcrowded = hierarchy.size() > 5;
		int x = 12;
		ITaxon group;

		while (!hierarchy.isEmpty()) {
			group = hierarchy.pop();
			if (overcrowded && group.rank().isDroppable()) {
				continue;
			}

			String name = Character.toUpperCase(group.name().charAt(0)) + group.name().substring(1);
			textLayout.drawLine(transform, name, x, group.rank().getColour());
			textLayout.drawLine(transform, group.rank().name(), 170, group.rank().getColour());
			textLayout.newLineCompressed();
			x += 12;
		}

		// Add the species name
		String binomial = species.getBinomial();
		if (font.width(binomial) > 96) {
			binomial = Character.toUpperCase(species.getGenusName().charAt(0)) + ". " + species.getSpeciesName();
		}

		textLayout.drawLine(transform, binomial, x, 0xebae85);
		textLayout.drawLine(transform, "SPECIES", 170, 0xebae85);

		textLayout.newLine();
		textLayout.drawLine(transform, Component.translatable("for.gui.alyzer.authority").append(": ").append(species.getAuthority()), 12);

		textLayout.newLine();
		String description = species.getDescriptionTranslationKey();
		if (Translator.canTranslateToLocal(description)) {
			description = Component.translatable(description).getString();
			String[] tokens = description.split("\\|");
			textLayout.drawSplitLine(transform, tokens[0], 12, 200, 0x666666);
			if (tokens.length > 1) {
				String signature = "- " + tokens[1];
				getFontRenderer().drawShadow(transform, signature, leftPos + 210 - getFontRenderer().width(signature), topPos + 145 - 14, 0x99cc32);
			}
		} else {
			textLayout.drawSplitLine(transform, Component.translatable("for.gui.alyzer.nodescription"), 12, 200, 0x666666);
		}

		textLayout.endPage(transform);
	}

	public <I extends IIndividual> void drawAnalyticsPageMutations(PoseStack transform, I individual) {
		textLayout.startPage(transform, COLUMN_0, COLUMN_1, COLUMN_2);
		textLayout.drawLine(transform, Component.translatable("for.gui.beealyzer.mutations").append(":"), COLUMN_0);
		textLayout.newLine();

		ISpeciesType<?, ?> speciesRoot = individual.getType();
		ISpecies<?> species = individual.getSpecies();

		int columnWidth = 50;
		int x = 0;

		Player player = Minecraft.getInstance().player;
		IBreedingTracker breedingTracker = speciesRoot.getBreedingTracker(player.level, player.getGameProfile());

		IMutationManager<?> mutations = species.getType().getMutations();
		for (IMutation<?> mutation : mutations.getMutationsFrom(species.cast())) {
			if (breedingTracker.isDiscovered(mutation)) {
				drawMutationInfo(transform, mutation, species, COLUMN_0 + x, breedingTracker);
			} else {
				// Do not display secret undiscovered mutations.
				if (mutation.isSecret()) {
					continue;
				}

				drawUnknownMutation(transform, mutation, COLUMN_0 + x, breedingTracker);
			}

			x += columnWidth;
			if (x >= columnWidth * 4) {
				x = 0;
				textLayout.newLine(16);
			}
		}

		textLayout.endPage(transform);
	}

	public void drawMutationInfo(PoseStack transform, IMutation<?> combination, ISpecies<?> species, int x, IBreedingTracker breedingTracker) {
		Map<ISpecies<?>, ItemStack> iconStacks = combination.getType().getAlyzerPlugin().getIconStacks();

		ItemStack partnerBee = iconStacks.get(combination.getPartner(species));
		widgetManager.add(new ItemStackWidget(widgetManager, x, textLayout.getLineY(), partnerBee));

		drawProbabilityArrow(transform, combination, leftPos + x + 18, topPos + textLayout.getLineY() + 4, breedingTracker);

		ISpecies<?> result = combination.getResult();
		ItemStack resultBee = iconStacks.get(result);
		widgetManager.add(new ItemStackWidget(widgetManager, x + 33, textLayout.getLineY(), resultBee));
	}

	private void drawUnknownMutation(PoseStack transform, IMutation<?> combination, int x, IBreedingTracker breedingTracker) {

		drawQuestionMark(transform, leftPos + x, topPos + textLayout.getLineY());

		drawProbabilityArrow(transform, combination, leftPos + x + 18, topPos + textLayout.getLineY() + 4, breedingTracker);

		drawQuestionMark(transform, leftPos + x + 32, topPos + textLayout.getLineY());
	}

	private void drawQuestionMark(PoseStack transform, int x, int y) {
		bindTexture(textureFile);
		blit(transform, x, y, 78, 240, 16, 16);
	}

	private void drawProbabilityArrow(PoseStack transform, IMutation<?> combination, int x, int y, IBreedingTracker breedingTracker) {
		float chance = combination.getChance();
		int line = 247;
		int column = switch (EnumMutateChance.rateChance(chance)) {
			case HIGHER -> 100 + 15;
			case HIGH -> 100 + 15 * 2;
			case NORMAL -> 100 + 15 * 3;
			case LOW -> 100 + 15 * 4;
			case LOWEST -> 100 + 15 * 5;
			default -> 100;
		};

		// Probability arrow
		bindTexture(textureFile);
		blit(transform, x, y, column, line, 15, 9);

		boolean researched = breedingTracker.isResearched(combination);
		if (researched) {
			getFontRenderer().draw(transform, "+", x + 9, y + 1, 0);
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		}
	}

	public void drawToleranceInfo(PoseStack transform, IValueChromosome<ToleranceType> chromosome, IValueAllele<ToleranceType> toleranceAllele, int x) {
		int textColor = getColorCoding(toleranceAllele.dominant());
		ToleranceType tolerance = toleranceAllele.value();
		Component text = Component.literal("(").append(chromosome.getDisplayName(toleranceAllele)).append(")");

		// Enable correct lighting.
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		switch (tolerance) {
			case BOTH_1, BOTH_2, BOTH_3, BOTH_4, BOTH_5 -> {
				drawBothSymbol(transform, x - 2, textLayout.getLineY() - 1);
				textLayout.drawLine(transform, text, x + 14, textColor);
			}
			case DOWN_1, DOWN_2, DOWN_3, DOWN_4, DOWN_5 -> {
				drawDownSymbol(transform, x - 2, textLayout.getLineY() - 1);
				textLayout.drawLine(transform, text, x + 14, textColor);
			}
			case UP_1, UP_2, UP_3, UP_4, UP_5 -> {
				drawUpSymbol(transform, x - 2, textLayout.getLineY() - 1);
				textLayout.drawLine(transform, text, x + 14, textColor);
			}
			default -> {
				drawNoneSymbol(transform, x - 2, textLayout.getLineY() - 1);
				textLayout.drawLine(transform, "(0)", x + 14, textColor);
			}
		}
	}

	private void drawDownSymbol(PoseStack transform, int x, int y) {
		bindTexture(textureFile);
		blit(transform, leftPos + x, topPos + y, 0, 247, 15, 9);
	}

	private void drawUpSymbol(PoseStack transform, int x, int y) {
		bindTexture(textureFile);
		blit(transform, leftPos + x, topPos + y, 15, 247, 15, 9);
	}

	private void drawBothSymbol(PoseStack transform, int x, int y) {
		bindTexture(textureFile);
		blit(transform, leftPos + x, topPos + y, 30, 247, 15, 9);
	}

	private void drawNoneSymbol(PoseStack transform, int x, int y) {
		bindTexture(textureFile);
		blit(transform, leftPos + x, topPos + y, 45, 247, 15, 9);
	}

	public void drawFertilityInfo(PoseStack transform, int fertility, int x, int textColor, int texOffset) {
		// Enable correct lighting.
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		String fertilityString = fertility + " x";

		int stringWidth = getFontRenderer().width(fertilityString);

		bindTexture(textureFile);
		blit(transform, leftPos + x + stringWidth + 2, topPos + textLayout.getLineY() - 1, 60, 240 + texOffset, 12, 8);

		textLayout.drawLine(transform, fertilityString, x, textColor);
	}

	public WidgetManager getWidgetManager() {
		return widgetManager;
	}

	@Override
	protected void addLedgers() {
		addErrorLedger(itemInventory);
		addHintLedger(getHints());
	}

	public List<String> getHints() {
		ItemStack specimen = itemInventory.getSpecimen();
		if (!specimen.isEmpty()) {
			IIndividual individual = IIndividualHandlerItem.getIndividual(specimen);
			if (individual != null) {
				return individual.getType().getAlyzerPlugin().getHints();
			}
		}
		return Collections.emptyList();
	}
}
