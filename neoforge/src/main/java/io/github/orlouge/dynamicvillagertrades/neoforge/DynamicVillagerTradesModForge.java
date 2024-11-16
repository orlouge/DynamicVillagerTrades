package io.github.orlouge.dynamicvillagertrades.forge;

import io.github.orlouge.dynamicvillagertrades.DynamicVillagerTradesMod;
import io.github.orlouge.dynamicvillagertrades.TradeOfferManager;
import io.github.orlouge.dynamicvillagertrades.trade_offers.TradeOfferFactoryType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.neoforged.common.MinecraftForge;
import net.neoforged.event.AddReloadListenerEvent;
import net.neoforged.event.RegisterCommandsEvent;
import net.neoforged.eventbus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod(DynamicVillagerTradesMod.MOD_ID)
public class DynamicVillagerTradesModForge {
    public static Supplier<IForgeRegistry<TradeOfferFactoryType<?>>> supplier = null;
    public static Identifier TRADE_OFFER_FACTORY_REGISTER_ID = DynamicVillagerTradesMod.id("trade_offer_factory");

    public static Logger LOGGER = LogManager.getLogger(DynamicVillagerTradesMod.MOD_ID);

    public DynamicVillagerTradesModForge() {
        DynamicVillagerTradesMod.init();
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::newRegistryEventListener);
        eventBus.addListener(this::registerEventListener);
        MinecraftForge.EVENT_BUS.addListener(this::reloadListenerEventHandler);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommandsEventHandler);
    }

    private void registerCommandsEventHandler(RegisterCommandsEvent event) {
        DynamicVillagerTradesMod.registerCommands(event.getDispatcher());
    }

    private void reloadListenerEventHandler(net.neoforged.neoforge.event.AddReloadListenerEvent event) {
        DynamicRegistryManager registry = event.getRegistryAccess();
        TradeOfferManager.REGISTRY_ACCESS = () -> registry;
        event.addListener(DynamicVillagerTradesMod.TRADE_OFFER_MANAGER);
    }

    private void newRegistryEventListener(NewRegistryEvent event) {
        RegistryBuilder<TradeOfferFactoryType<?>> builder = new RegistryBuilder<>();
        builder.setName(TRADE_OFFER_FACTORY_REGISTER_ID);
        //builder.onCreate((owner, stage) -> PlatformHelperImpl.tradeOfferFactoryTypeRegistryHelper.registerAll());
        supplier = event.create(builder);
    }

    private void registerEventListener(RegisterEvent event) {
        PlatformHelperImpl.tradeOfferFactoryTypeRegistryHelper.registerAll(event);
    }
}