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
package forestry.arboriculture;

import java.util.function.Consumer;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import forestry.api.arboriculture.TreeManager;
import forestry.api.client.IClientModuleHandler;
import forestry.api.core.IArmorNaturalist;
import forestry.api.genetics.IIndividual;
import forestry.api.modules.ForestryModule;
import forestry.api.modules.ForestryModuleIds;
import forestry.api.modules.IPacketRegistry;
import forestry.arboriculture.client.ArboricultureClientHandler;
import forestry.arboriculture.commands.CommandTree;
import forestry.arboriculture.network.PacketRipeningUpdate;
import forestry.arboriculture.villagers.RegisterVillager;
import forestry.core.ModuleCore;
import forestry.core.network.PacketIdClient;
import forestry.modules.BlankForestryModule;

@ForestryModule
public class ModuleArboriculture extends BlankForestryModule {
	@Override
	public ResourceLocation getId() {
		return ForestryModuleIds.ARBORICULTURE;
	}

	@Override
	public void registerEvents(IEventBus modBus) {
		RegisterVillager.POINTS_OF_INTEREST.register(modBus);
		RegisterVillager.PROFESSIONS.register(modBus);
		MinecraftForge.EVENT_BUS.addListener(RegisterVillager::villagerTrades);

		modBus.addListener(ModuleArboriculture::registerCapabilities);
		MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, ModuleArboriculture::attachCapabilities);
	}

	private static void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
		// todo attach vanilla capabilities to saplings
		/*if (!event.getCapabilities().containsKey(IIndividual.CAPABILITY_ID)) {

		}*/
	}

	@Override
	public void setupApi() {
		TreeManager.woodAccess = WoodAccess.INSTANCE;
	}

	@Override
	public void addToRootCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
		command.then(CommandTree.register());
	}

	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(IArmorNaturalist.class);
	}

	@Override
	public void registerPackets(IPacketRegistry registry) {
		registry.clientbound(PacketIdClient.RIPENING_UPDATE, PacketRipeningUpdate.class, PacketRipeningUpdate::decode, PacketRipeningUpdate::handle);
	}

	@Override
	public void registerClientHandler(Consumer<IClientModuleHandler> registrar) {
		registrar.accept(new ArboricultureClientHandler());
	}
}
