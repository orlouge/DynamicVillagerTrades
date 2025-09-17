package io.github.orlouge.dynamicvillagertrades.trade_offers.generators;

import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MasonGenerator extends OneByOneVanillaLikeGenerator {
    @Override
    protected boolean professionFilter(RegistryKey<VillagerProfession> profession) {
        return profession.equals(VillagerProfession.MASON);
    }

    @Override
    protected Map<String, Double> getAttributes(TradeOffers.Factory trade, Map<String, Double> defaultAttributes) {
        Optional<Item> item = AttributeUtils.getTradeItem(trade);
        Map<String, Double> attributes = new HashMap<>();

        item.flatMap(AttributeUtils::getBlockInstrument)
                .flatMap(m -> m == NoteBlockInstrument.BASEDRUM ? item.flatMap(AttributeUtils::getColorAttributes) : Optional.empty())
                .map(colors -> {attributes.put("stone", 1.0); attributes.putAll(colors); return true;})
                .map(x -> item.map(MasonGenerator::stoneModifier).map(s -> {attributes.put("raw", s.equals("raw") ? 1.0 : -1.0); return true;}).isPresent())
                .or(() -> {attributes.put("stone", -1.0); return Optional.of(true);});

        item.flatMap(AttributeUtils::getMod).ifPresent(modname -> attributes.put(modname, 1.0));
        if (attributes.size() < 3) attributes.putAll(defaultAttributes);
        normalizeAttributes(attributes);
        return attributes;
    }

    private static String stoneModifier(Item item) {
        String s[] = Registries.ITEM.getId(item).getPath().split("_");
        for (String string : s) {
            switch (string) {
                case "polished" -> {
                    return "polished";
                }
                case "chiseled" -> {
                    return "chiseled";
                }
                case "cut" -> {
                    return "cut";
                }
                case "smooth" -> {
                    return "smooth";
                }
                case "glazed" -> {
                    return "glazed";
                }
                case "bricks" -> {
                    return "bricks";
                }
                case "brick" -> {
                    return "brick";
                }
                case "tiles" -> {
                    return "tiles";
                }
                case "tile" -> {
                    return "tile";
                }
                case "pillar" -> {
                    return "pillar";
                }
            }
        }
        return "raw";
    }
}
