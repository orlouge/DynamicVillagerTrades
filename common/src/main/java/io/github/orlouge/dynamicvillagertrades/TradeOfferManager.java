package io.github.orlouge.dynamicvillagertrades;

import io.github.orlouge.dynamicvillagertrades.trade_offers.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.github.orlouge.dynamicvillagertrades.trade_offers.generators.Generator;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.VillagerProfession;

import java.util.*;

public class TradeOfferManager extends JsonDataLoader<TradeOfferManager.VillagerTrades> {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Identifier WANDERING_TRADER_PROFESSION_ID = Registries.ENTITY_TYPE.getId(EntityType.WANDERING_TRADER);
    public static final Identifier ID = DynamicVillagerTradesMod.id("trade_offers");

    private Map<RegistryKey<VillagerProfession>, List<VillagerTrades>> loadedTrades = Map.of();
    private final Map<RegistryKey<VillagerProfession>, Map<String, TradeGroup>> tradeGroups = new HashMap<>();

    public TradeOfferManager() {
        super(VillagerTrades.CODEC, ResourceFinder.json(ID.getPath()));
    }

    @Override
    protected void apply(Map<Identifier, VillagerTrades> prepared, ResourceManager manager, Profiler profiler) {
        Map<RegistryKey<VillagerProfession>, List<VillagerTrades>> builderMap = new HashMap<>();

        //manager.findResources(ID.getPath(), id -> id.getPath().endsWith(".json")).forEach((id, res) -> {
        //    Identifier identifier = Identifier.of(id.getNamespace(), id.getPath().substring(ID.getPath().length() + 1, id.getPath().length() - 5));
        //    VillagerTrades trades = prepared.get(identifier);
        prepared.forEach((identifier, trades) -> {
            if (trades != null) {
                builderMap.computeIfAbsent(trades.profession, k -> new LinkedList<>()).add(trades);
            }
        });

        this.loadedTrades = builderMap;
        Generator.resetAll();
    }

    public Optional<Map<String, TradeGroup>> getVillagerOffers(RegistryEntry<VillagerProfession> profession, DynamicRegistryManager registryManager) {
        return Optional.ofNullable(tradeGroups.get(profession.getKey().get())).or(() -> generateTradeGroups(profession, registryManager));
    }

    private Optional<Map<String, TradeGroup>> generateTradeGroups(RegistryEntry<VillagerProfession> profession, DynamicRegistryManager registryManager) {
        Objects.requireNonNull(registryManager);
        Map<String, TradeGroup> generated = Generator.generateAll(profession, registryManager).orElse(new TreeMap<>());
        for (VillagerTrades trades : loadedTrades.getOrDefault(profession.getKey().get(), Collections.emptyList())) {
            if (trades.replace) {
                generated = new TreeMap<>();
            }
            for (Map.Entry<String, TradeGroup> entry : trades.offers().entrySet()) {
                generated.computeIfPresent(entry.getKey(), (_name, oldGroup) -> TradeGroup.merge(oldGroup, entry.getValue()));
                generated.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
        tradeGroups.put(profession.getKey().get(), generated);
        return Optional.of(generated);
    }

    public enum MerchantLevel implements StringIdentifiable {
        NOVICE("novice", 1),
        APPRENTICE("apprentice", 2),
        JOURNEYMAN("journeyman", 3),
        EXPERT("expert", 4),
        MASTER("master", 5),
        COMMON("common", 1),
        RARE("rare", 2);

        public final String name;
        public final int id;

        MerchantLevel(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public static DataResult<MerchantLevel> fromId(int id) {
            for (MerchantLevel value : values()) {
                if (value.id == id) {
                    return DataResult.success(value);
                }
            }

            return DataResult.error(() -> "Invalid level index " + id + " provided.");
        }

        @Override
        public String asString() {
            return this.name;
        }
    }

    public record VillagerTrades(RegistryKey<VillagerProfession> profession, boolean replace, Map<String, TradeGroup> offers) {
        public static final Codec<VillagerTrades> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
                RegistryKey.createCodec(RegistryKeys.VILLAGER_PROFESSION).fieldOf("profession").forGetter(VillagerTrades::profession),
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(VillagerTrades::replace),
                Codec.unboundedMap(Codec.STRING, TradeGroup.CODEC).fieldOf("offers").forGetter(VillagerTrades::offers)
        ).apply(instance, VillagerTrades::new)));
    }

}
