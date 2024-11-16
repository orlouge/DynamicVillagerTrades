package io.github.orlouge.dynamicvillagertrades.trade_offers;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import io.github.orlouge.dynamicvillagertrades.DynamicVillagerTradesMod;
import io.github.orlouge.dynamicvillagertrades.PlatformHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.village.TradeOffers;

import java.util.Map;
import java.util.function.Supplier;

public interface TradeOfferFactoryType<P extends TradeOffers.Factory> {
    PlatformHelper.RegistryHelper<TradeOfferFactoryType<?>> TRADE_OFFER_FACTORY_REGISTRY = PlatformHelper.getTradeOfferRegistry();
    Codec<TradeOffers.Factory> CODEC = Codec.lazyInitialized(() -> TRADE_OFFER_FACTORY_REGISTRY.getCodec().get().dispatch("type", factory -> ((TradeOfferFactoryTypeHolder) factory).getType().get(), TradeOfferFactoryType::codec));
    Supplier<TradeOfferFactoryType<TradeOffers.BuyItemFactory>> BUY_FOR_ONE_EMERALD = register(Identifier.ofVanilla("buy_for_one_emerald"), VanillaTradeOfferFactories.BUY_FOR_ONE_EMERALD);
    Supplier<TradeOfferFactoryType<TradeOffers.SellItemFactory>> SELL_ITEM = register(Identifier.ofVanilla("sell_item"), VanillaTradeOfferFactories.SELL_ITEM);
    Supplier<TradeOfferFactoryType<TradeOffers.SellSuspiciousStewFactory>> SELL_SUSPICIOUS_STEW = register(Identifier.ofVanilla("sell_suspicious_stew"), VanillaTradeOfferFactories.SELL_SUSPICIOUS_STEW);
    Supplier<TradeOfferFactoryType<TradeOffers.ProcessItemFactory>> PROCESS_ITEM = register(Identifier.ofVanilla("process_item"), VanillaTradeOfferFactories.PROCESS_ITEM);
    Supplier<TradeOfferFactoryType<TradeOffers.SellEnchantedToolFactory>> SELL_ENCHANTED_TOOL = register(Identifier.ofVanilla("sell_enchanted_tool"), VanillaTradeOfferFactories.SELL_ENCHANTED_TOOL);
    Supplier<TradeOfferFactoryType<TradeOffers.TypeAwareBuyForOneEmeraldFactory>> TYPE_AWARE_BUY_FOR_ONE_EMERALD = register(Identifier.ofVanilla("type_aware_buy_for_one_emerald"), VanillaTradeOfferFactories.TYPE_AWARE_BUY_FOR_ONE_EMERALD);
    Supplier<TradeOfferFactoryType<TradeOffers.SellPotionHoldingItemFactory>> SELL_POTION_HOLDING_ITEM = register(Identifier.ofVanilla("sell_potion_holding_item"), VanillaTradeOfferFactories.SELL_POTION_HOLDING_ITEM);
    Supplier<TradeOfferFactoryType<TradeOffers.EnchantBookFactory>> ENCHANT_BOOK = register(Identifier.ofVanilla("enchant_book"), VanillaTradeOfferFactories.ENCHANT_BOOK);
    Supplier<TradeOfferFactoryType<TradeOffers.SellMapFactory>> SELL_MAP = register(Identifier.ofVanilla("sell_map"), VanillaTradeOfferFactories.SELL_MAP);
    Supplier<TradeOfferFactoryType<TradeOffers.SellDyedArmorFactory>> SELL_DYED_ARMOR = register(Identifier.ofVanilla("sell_dyed_armor"), VanillaTradeOfferFactories.SELL_DYED_ARMOR);

    Supplier<TradeOfferFactoryType<SellItemForItemsOfferFactory>> SELL_ITEM_FOR_ITEMS = register(DynamicVillagerTradesMod.id("sell_item_for_items"), SellItemForItemsOfferFactory.CODEC);
    Supplier<TradeOfferFactoryType<TypeAwareSellItemForItemsOfferFactory>> TYPE_AWARE_SELL_ITEMS_FOR_ITEM = register(DynamicVillagerTradesMod.id("type_aware_sell_item_for_items"), TypeAwareSellItemForItemsOfferFactory.CODEC);
    Supplier<TradeOfferFactoryType<TypeAwareTradeOfferFactory>> TYPE_AWARE = register(DynamicVillagerTradesMod.id("type_aware"), TypeAwareTradeOfferFactory.CODEC);

    Supplier<TradeOfferFactoryType<EnchantSpecificBookFactory>> ENCHANT_SPECIFIC_BOOK = register(DynamicVillagerTradesMod.id("enchant_specific_book"), EnchantSpecificBookFactory.CODEC);
    Supplier<TradeOfferFactoryType<SellSpecificPotionHoldingItemFactory>> SELL_SPECIFIC_POTION_HOLDING_ITEM = register(DynamicVillagerTradesMod.id("sell_specific_potion_holding_item"), SellSpecificPotionHoldingItemFactory.CODEC);

    MapCodec<P> codec();

    static <P extends TradeOffers.Factory> Supplier<TradeOfferFactoryType<P>> register(Identifier id, MapCodec<P> codec) {
        return TRADE_OFFER_FACTORY_REGISTRY.register(id, () -> () -> codec);
    }

    static void init() {
    }
}
