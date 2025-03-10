package forestry.api.core;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public interface IMenuTypeProvider<C extends AbstractContainerMenu> {
	MenuType<C> menuType();
}
