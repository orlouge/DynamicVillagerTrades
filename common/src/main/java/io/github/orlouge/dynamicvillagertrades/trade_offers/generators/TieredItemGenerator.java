package io.github.orlouge.dynamicvillagertrades.trade_offers.generators;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TieredItemGenerator extends OneByOneVanillaLikeGenerator {
    @Override
    protected boolean professionFilter(RegistryKey<VillagerProfession> profession) {
        return profession.equals(VillagerProfession.TOOLSMITH) || profession.equals(VillagerProfession.WEAPONSMITH) || profession.equals(VillagerProfession.ARMORER);
    }

    @Override
    protected Map<String, Double> getAttributes(TradeOffers.Factory trade, Map<String, Double> defaultAttributes) {
        Optional<Item> item = AttributeUtils.getTradeItem(trade);
        String primary = item.map(TieredItemGenerator::getItemType).orElse("other");
        Map<String, Double> attributes = new HashMap<>();
        if (!primary.equals("other")) {
            if (primary.equals("material")) {
                item.flatMap(m -> AttributeUtils.getIngotMaterialName(m, false)).ifPresent(material -> attributes.put(material, 1.0));
            } else {
                item.flatMap(AttributeUtils::getToolOrArmorMaterialName).ifPresent(material -> attributes.put(material, 1.0));
            }
        }
        item.flatMap(AttributeUtils::getMod).ifPresent(modname -> attributes.put(modname, 1.0));
        if (attributes.isEmpty()) attributes.putAll(defaultAttributes);
        normalizeAttributes(attributes);
        attributes.put(primary, 1.0);
        return attributes;
    }

    private static String getItemType(Item item) {
        if (item.getComponents().contains(DataComponentTypes.EQUIPPABLE)) return "armor";
        if (item instanceof ShovelItem) return "shovel";
        if (item instanceof AxeItem) return "axe";
        if (item instanceof HoeItem) return "hoe";
        if (item.getComponents().contains(DataComponentTypes.WEAPON)) return "sword";
        if (item.getComponents().contains(DataComponentTypes.TOOL)) return "tool";
        if (AttributeUtils.getIngotMaterialName(item, false).isPresent()) return "material";
        return "other";
    }
}
