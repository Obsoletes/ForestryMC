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
package forestry.core.gui.widgets;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import forestry.api.core.tooltips.IToolTipProvider;
import forestry.api.core.tooltips.ToolTip;

/**
 * Basic non-ItemStack slot
 */
public abstract class Widget implements IToolTipProvider {
	protected final WidgetManager manager;
	protected final int xPos;
	protected final int yPos;
	protected int width = 16;
	protected int height = 16;

	public Widget(WidgetManager manager, int xPos, int yPos) {
		this.manager = manager;
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getX() {
		return xPos;
	}

	public int getY() {
		return yPos;
	}

	public abstract void draw(PoseStack transform, int startX, int startY);

	public void update(int mouseX, int mouseY) {
	}

	@Nullable
	@Override
	public ToolTip getToolTip(int mouseX, int mouseY) {
		return null;
	}

	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= xPos && mouseX <= xPos + this.width && mouseY >= yPos && mouseY <= yPos + this.height;
	}

	@Override
	public boolean isHovering(double mouseX, double mouseY) {
		return isMouseOver(mouseX, mouseY);
	}

	public void handleMouseClick(double mouseX, double mouseY, int mouseButton) {
	}

	public boolean handleMouseRelease(double mouseX, double mouseY, int eventType) {
		return false;
	}

	public void handleMouseMove(int mouseX, int mouseY, int mouseButton, long time) {
	}
}
