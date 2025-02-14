package io.github.orlouge.dynamicvillagertrades.forge;

import com.mojang.serialization.Codec;
import io.github.orlouge.dynamicvillagertrades.PlatformHelper;
import io.github.orlouge.dynamicvillagertrades.TradeOfferManager;
import io.github.orlouge.dynamicvillagertrades.trade_offers.TradeOfferFactoryType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.Level;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.nio.file.Path;

public class PlatformHelperImpl {

    public static final TradeOfferManager tradeOfferManager = new TradeOfferManager();

    public static final RegistryHelperForge<TradeOfferFactoryType<?>> tradeOfferFactoryTypeRegistryHelper = new RegistryHelperForge<>(() -> DynamicVillagerTradesModForge.supplier.get());

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    public static PlatformHelper.RegistryHelper<TradeOfferFactoryType<?>> getTradeOfferRegistry() {
        return tradeOfferFactoryTypeRegistryHelper;
    }

    public static TradeOfferManager getTradeOfferManager() {
        return tradeOfferManager;
    }

    public static class RegistryHelperForge<T> implements PlatformHelper.RegistryHelper<T> {
        private boolean registered = false;
        private final Supplier<IForgeRegistry<T>> registry;
        private final Map<Identifier, Supplier<? extends T>> entries = new LinkedHashMap<>();

        private RegistryHelperForge(Supplier<IForgeRegistry<T>> registry) {
            this.registry = registry;
        }

        @Override
        public Supplier<Codec<T>> getCodec() {
            return () -> this.registry.get().getCodec();
        }

        public void registerAll(RegisterEvent event) {
            RegistryKey<Registry<T>> registryKey = this.registry.get().getRegistryKey();
            DynamicVillagerTradesModForge.LOGGER.log(Level.INFO, event.getRegistryKey() + "_" + registryKey.toString());
            if (event.getRegistryKey().equals(registryKey)) {
                registered = true;
                DynamicVillagerTradesModForge.LOGGER.log(Level.INFO, event.getRegistryKey() + "_" + registryKey);
                for (Map.Entry<Identifier, Supplier<? extends T>> e : this.entries.entrySet()) {
                    event.register(registryKey, e.getKey(), () -> e.getValue().get());
                    DynamicVillagerTradesModForge.LOGGER.log(Level.INFO, e.getKey().toString());
                    //this.registry.get().register(e.getKey(), e.getValue().get());
                }
                this.entries.clear();
            }
        }

        @Override
        public <V extends T> Supplier<V> register(Identifier name, Supplier<V> entry) {
            DynamicVillagerTradesModForge.LOGGER.log(Level.INFO, "register " + name.toString());
            if (registered) {
                this.registry.get().register(name, entry.get());
            } else {
                this.entries.put(name, entry);
            }
            return () -> (V) this.registry.get().getValue(name);
        }
    }
}
