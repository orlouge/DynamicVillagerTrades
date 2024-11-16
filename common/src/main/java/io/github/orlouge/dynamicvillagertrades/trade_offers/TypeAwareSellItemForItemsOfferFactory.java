package io.github.orlouge.dynamicvillagertrades.trade_offers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.orlouge.dynamicvillagertrades.api.CodecHelper;
import io.github.orlouge.dynamicvillagertrades.api.SerializableTradeOfferFactory;
import net.minecraft.village.TradedItem;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.registry.Registries;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerType;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class TypeAwareSellItemForItemsOfferFactory implements SerializableTradeOfferFactory {
    public static final MapCodec<TypeAwareSellItemForItemsOfferFactory> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CodecHelper.villagerTypeMap(TradedItem.CODEC).fieldOf("buy_1").forGetter(typeAwareSellItemForItemsOfferFactory -> typeAwareSellItemForItemsOfferFactory.buyMap1),
            CodecHelper.villagerTypeMap(TradedItem.CODEC).optionalFieldOf("buy_2").forGetter(typeAwareSellItemForItemsOfferFactory -> Optional.ofNullable(typeAwareSellItemForItemsOfferFactory.buyMap2)),
            CodecHelper.villagerTypeMap(CodecHelper.SIMPLE_ITEM_STACK_CODEC).fieldOf("sell").forGetter(typeAwareSellItemForItemsOfferFactory -> typeAwareSellItemForItemsOfferFactory.sellMap),
            Codec.INT.optionalFieldOf("max_uses", 12).forGetter(factory -> factory.maxUses),
            Codec.INT.optionalFieldOf("experience", 2).forGetter(factory -> factory.experience)
    ).apply(instance, TypeAwareSellItemForItemsOfferFactory::new));

    public final Map<VillagerType, TradedItem> buyMap1;
    public final Map<VillagerType, TradedItem> buyMap2;
    public final Map<VillagerType, ItemStack> sellMap;
    public final int maxUses;
    public final int experience;

    public TypeAwareSellItemForItemsOfferFactory(Map<VillagerType, TradedItem> buyMap1, Optional<Map<VillagerType, TradedItem>> buyMap2, Map<VillagerType, ItemStack> sellMap, int maxUses, int experience) {
        Registries.VILLAGER_TYPE.stream().filter((villagerType) -> !buyMap1.containsKey(villagerType) || (buyMap2.isPresent() && !buyMap2.get().containsKey(villagerType)) || !sellMap.containsKey(villagerType)).findAny().ifPresent((villagerType) -> {
            throw new IllegalStateException("Missing trade for villager type: " + Registries.VILLAGER_TYPE.getId(villagerType));
        });
        this.buyMap1 = buyMap1;
        this.buyMap2 = buyMap2.orElse(null);
        this.sellMap = sellMap;
        this.maxUses = maxUses;
        this.experience = experience;
    }

    @Nullable
    @Override
    public TradeOffer create(Entity entity, Random random) {
        if (entity instanceof VillagerDataContainer) {
            TradedItem buy1 = this.buyMap1.get(((VillagerDataContainer)entity).getVillagerData().getType());
            Optional<TradedItem> buy2 = this.buyMap2 != null ? Optional.ofNullable(this.buyMap2.get(((VillagerDataContainer) entity).getVillagerData().getType())) : Optional.empty();
            ItemStack sell = this.sellMap.get(((VillagerDataContainer)entity).getVillagerData().getType()).copy();
            return new TradeOffer(buy1, buy2, sell, this.maxUses, this.experience, 0.05F);
        } else {
            return null;
        }
    }

    @Override
    public Supplier<TradeOfferFactoryType<?>> getType() {
        return TradeOfferFactoryType.TYPE_AWARE_SELL_ITEMS_FOR_ITEM::get;
    }
}
