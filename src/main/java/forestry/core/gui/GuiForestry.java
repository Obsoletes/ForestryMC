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

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedListMultimap;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import forestry.Forestry;
import forestry.api.ForestryConstants;
import forestry.api.client.ForestrySprites;
import forestry.api.client.IForestryClientApi;
import forestry.api.climate.IClimateProvider;
import forestry.api.core.IErrorLogicSource;
import forestry.api.core.IErrorSource;
import forestry.core.config.ForestryConfig;
import forestry.core.gui.elements.WindowGui;
import forestry.core.gui.ledgers.ClimateLedger;
import forestry.core.gui.ledgers.HintLedger;
import forestry.core.gui.ledgers.LedgerManager;
import forestry.core.gui.ledgers.OwnerLedger;
import forestry.core.gui.ledgers.PowerLedger;
import forestry.core.gui.slots.ISlotTextured;
import forestry.core.gui.widgets.TankWidget;
import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.owner.IOwnedTile;
import forestry.core.render.ColourProperties;
import forestry.energy.ForestryEnergyStorage;

public abstract class GuiForestry<C extends AbstractContainerMenu> extends AbstractContainerScreen<C> implements IGuiSizable {
	// Used to display "Did you know?" ledgers in GUI
	public static final LinkedListMultimap<String, String> HINTS = readDefaultHints();

	public final ResourceLocation textureFile;
	protected final WidgetManager widgetManager;
	protected final LedgerManager ledgerManager;
	protected final TextLayoutHelper textLayout;
	protected final WindowGui<?> window;

	protected GuiForestry(String texture, C menu, Inventory inv, Component title) {
		this(ForestryConstants.forestry(texture), menu, inv, title);
	}

	protected GuiForestry(ResourceLocation texture, C menu, Inventory inv, Component title) {
		super(menu, inv, title);

		this.widgetManager = new WidgetManager(this);
		this.ledgerManager = new LedgerManager(this);
		this.window = new WindowGui<>(imageWidth, imageHeight, this);

		this.textureFile = texture;

		this.textLayout = new TextLayoutHelper(this, ColourProperties.INSTANCE);
	}

	/* LEDGERS */
	@Override
	public void init() {
		super.init();

		int maxLedgerWidth = (this.width - this.imageWidth) / 2;

		this.ledgerManager.setMaxWidth(maxLedgerWidth);
		this.ledgerManager.clear();

		this.window.init(leftPos, topPos);

		addLedgers();
	}

	@Override
	public void resize(Minecraft mc, int width, int height) {
		window.setSize(width, height);
		super.resize(mc, width, height);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		window.updateClient();
	}

	@Override
	public void render(PoseStack transform, int mouseX, int mouseY, float partialTicks) {
		window.setMousePosition(mouseX, mouseY);
		this.renderBackground(transform);
		super.render(transform, mouseX, mouseY, partialTicks);
		renderTooltip(transform, mouseX, mouseY);
	}

	protected abstract void addLedgers();

	protected final void addErrorLedger(IErrorSource errorSource) {
		ledgerManager.add(errorSource);
	}

	protected final void addErrorLedger(IErrorLogicSource errorSource) {
		ledgerManager.add(errorSource.getErrorLogic());
	}

	protected final void addClimateLedger(IClimateProvider climatised) {
		ledgerManager.add(new ClimateLedger(ledgerManager, climatised));
	}

	protected final void addPowerLedger(ForestryEnergyStorage energyStorage) {
		ledgerManager.add(new PowerLedger(ledgerManager, energyStorage));
	}

	protected final void addHintLedger(String hintsKey) {
		if (ForestryConfig.CLIENT.enableHints.get()) {
			List<String> hints = HINTS.get(hintsKey);
			addHintLedger(hints);
		}
	}

	protected final void addHintLedger(List<String> hints) {
		if (ForestryConfig.CLIENT.enableHints.get()) {
			if (!hints.isEmpty()) {
				ledgerManager.add(new HintLedger(ledgerManager, hints));
			}
		}
	}

	protected final void addOwnerLedger(IOwnedTile ownedTile) {
		ledgerManager.add(new OwnerLedger(ledgerManager, ownedTile));
	}

	@Override
	public void onClose() {
		super.onClose();
		ledgerManager.onClose();
	}

	public ColourProperties getFontColor() {
		return ColourProperties.INSTANCE;
	}

	public Font getFontRenderer() {
		return font;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		// / Handle ledger clicks
		ledgerManager.handleMouseClicked(mouseX, mouseY, mouseButton);
		widgetManager.handleMouseClicked(mouseX, mouseY, mouseButton);
		if (window.onMouseClicked(mouseX, mouseY, mouseButton)) {
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (widgetManager.handleMouseRelease(mouseX, mouseY, mouseButton)
				|| window.onMouseReleased(mouseX, mouseY, mouseButton)) {
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, mouseButton);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		window.onMouseMove(mouseX, mouseY);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double oldMouseX, double oldMouseY) {
		if (window.onMouseDrag(mouseX, mouseY)) {
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, mouseButton, oldMouseX, oldMouseY);
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (window.onKeyPressed(key, scanCode, modifiers)) {
			return true;
		}
		return super.keyPressed(key, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int key, int scanCode, int modifiers) {
		if (window.onKeyReleased(key, scanCode, modifiers)) {
			return true;
		}
		return super.keyReleased(key, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (window.onCharTyped(codePoint, modifiers)) {
			return true;
		}
		return super.charTyped(codePoint, modifiers);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double deltaWheel) {
		super.mouseScrolled(mouseX, mouseY, deltaWheel);
		if (deltaWheel != 0) {
			if (window.onMouseScrolled(mouseX, mouseY, deltaWheel)) {
				return true;
			}
		}
		return super.mouseScrolled(mouseX, mouseY, deltaWheel);
	}

	@Nullable
	public FluidStack getFluidStackAtPosition(double mouseX, double mouseY) {
		for (Widget widget : widgetManager.getWidgets()) {
			if (widget instanceof TankWidget tankWidget && widget.isMouseOver(mouseX - leftPos, mouseY - topPos)) {
				IFluidTank tank = tankWidget.getTank();
				if (tank != null) {
					return tank.getFluid();
				}
			}
		}
		return null;
	}

	@Nullable
	protected Slot getSlotAtPosition(double mouseX, double mouseY) {
		for (int k = 0; k < this.menu.slots.size(); ++k) {
			Slot slot = this.menu.slots.get(k);

			if (isMouseOverSlot(slot, mouseX, mouseY)) {
				return slot;
			}
		}

		return null;
	}

	private boolean isMouseOverSlot(Slot par1Slot, double mouseX, double mouseY) {
		return isHovering(par1Slot.x, par1Slot.y, 16, 16, mouseX, mouseY);
	}

	@Override
	protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
		return !window.isMouseOver(mouseX - guiLeft, mouseY - guiTop) && super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, mouseButton);
	}

	@Override
	public void renderSlot(PoseStack transform, Slot slot) {
		if (slot instanceof ISlotTextured textured) {
			ItemStack stack = slot.getItem();
			if (stack.isEmpty() && slot.isActive()) {
				ResourceLocation location = textured.getBackgroundTexture();
				if (location != null) {
					RenderSystem.setShaderTexture(0, ForestrySprites.TEXTURE_ATLAS);
					blit(transform, slot.x, slot.y, this.getBlitOffset(), 16, 16, IForestryClientApi.INSTANCE.getTextureManager().getSprite(location));
				}
			}
		}
		super.renderSlot(transform, slot);
	}

	@Override
	protected void renderLabels(PoseStack transform, int mouseX, int mouseY) {
		ledgerManager.drawTooltips(transform, mouseX, mouseY);

		if (this.menu.getCarried().isEmpty()) {
			GuiUtil.drawToolTips(transform, this, widgetManager.getWidgets(), mouseX, mouseY);
			GuiUtil.drawToolTips(transform, this, this.renderables, mouseX, mouseY);
			GuiUtil.drawToolTips(transform, this, menu.slots, mouseX, mouseY);
			window.drawTooltip(transform, mouseX, mouseY);
		}
	}

	@Override
	protected void renderBg(PoseStack transform, float partialTicks, int mouseX, int mouseY) {
		drawBackground(transform);

		widgetManager.updateWidgets(mouseX - leftPos, mouseY - topPos);

		//RenderHelper.enableGUIStandardItemLighting(); //TODO: Is there an replacement ?
		// RenderSystem.disableLighting();
		// RenderSystem.enableRescaleNormal();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		transform.pushPose();
		{
			transform.translate(leftPos, topPos, 0.0F);
			drawWidgets(transform);
		}
		transform.popPose();

		// RenderSystem.color3f(1.0F, 1.0F, 1.0F);

		window.draw(transform, mouseX, mouseY);

		bindTexture(textureFile);
	}

	protected void drawBackground(PoseStack transform) {
		bindTexture(textureFile);

		blit(transform, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	protected void drawWidgets(PoseStack transform) {
		ledgerManager.drawLedgers(transform);
		widgetManager.drawWidgets(transform);
	}

	protected void bindTexture(ResourceLocation texturePath) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texturePath);
	}

	@Override
	public int getSizeX() {
		return imageWidth;
	}

	@Override
	public int getSizeY() {
		return imageHeight;
	}

	@Override
	public Minecraft getGameInstance() {
		return Preconditions.checkNotNull(minecraft);
	}

	public List<Rect2i> getExtraGuiAreas() {
		return ledgerManager.getLedgerAreas();
	}

	public TextLayoutHelper getTextLayout() {
		return textLayout;
	}

	private static LinkedListMultimap<String, String> readDefaultHints() {
		LinkedListMultimap<String, String> map = LinkedListMultimap.create();
		Properties prop = new Properties();

		try {
			InputStream hintStream = GuiForestry.class.getResourceAsStream("/config/forestry/hints.properties");
			prop.load(hintStream);
		} catch (IOException | NullPointerException e) {
			Forestry.LOGGER.error("Failed to load hints file.", e);
		}

		for (String key : prop.stringPropertyNames()) {
			String list = prop.getProperty(key);

			if (!list.isEmpty()) {
				for (String parsedHint : list.split(";+")) {
					map.put(key, parsedHint);
				}
			}
		}

		return map;
	}
}
