package forestry.worktable.screens.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import forestry.api.client.ForestrySprites;
import forestry.api.client.IForestryClientApi;
import forestry.core.gui.widgets.ItemStackWidgetBase;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.render.ForestryTextureManager;
import forestry.core.utils.SoundUtil;
import forestry.worktable.recipes.RecipeMemory;
import forestry.worktable.screens.WorktableMenu;

public class MemorizedRecipeSlot extends ItemStackWidgetBase {
	private final RecipeMemory memory;
	private final int slotIndex;

	public MemorizedRecipeSlot(WidgetManager manager, int xPos, int yPos, RecipeMemory memory, int slotIndex) {
		super(manager, xPos, yPos);
		this.memory = memory;
		this.slotIndex = slotIndex;
	}

	@Override
	protected ItemStack getItemStack() {
		return memory.getRecipeDisplayOutput(Minecraft.getInstance().level, slotIndex);
	}

	@Override
	public void draw(PoseStack transform, int startX, int startY) {
		super.draw(transform, startX, startY);

		RenderSystem.disableDepthTest();

		if (memory.isLocked(slotIndex)) {
			RenderSystem.setShaderTexture(0, ForestrySprites.TEXTURE_ATLAS);
			TextureAtlasSprite lockedSprite = IForestryClientApi.INSTANCE.getTextureManager().getSprite(ForestrySprites.SLOT_LOCKED);
			GuiComponent.blit(transform, startX + xPos, startY + yPos, manager.gui.getBlitOffset(), 16, 16, lockedSprite);
		}

		RenderSystem.enableDepthTest();
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
		if (!getItemStack().isEmpty()) {
			WorktableMenu.sendRecipeClick(mouseButton, slotIndex);
			SoundUtil.playButtonClick();
		}
	}
}
