package forestry.worktable.screens.widgets;

import com.mojang.blaze3d.vertex.PoseStack;

import forestry.core.gui.widgets.Widget;
import forestry.core.gui.widgets.WidgetManager;
import forestry.core.utils.SoundUtil;
import forestry.worktable.screens.WorktableMenu;

public class ClearWorktable extends Widget {
	public ClearWorktable(WidgetManager manager, int xPos, int yPos) {
		super(manager, xPos, yPos);
		width = 7;
		height = 7;
	}

	@Override
	public void draw(PoseStack transform, int startX, int startY) {
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
		WorktableMenu.clearRecipe();
		SoundUtil.playButtonClick();
	}
}
