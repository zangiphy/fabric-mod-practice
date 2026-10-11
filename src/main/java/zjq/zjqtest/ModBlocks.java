package zjq.zjqtest;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;


//注册用类
public class ModBlocks {
    public static Block register(Block block, String id) {
        Identifier blockID = Identifier.of(Zjqtest.MOD_ID, id);
        Block registeredBlock = Registry.register(Registries.BLOCK, blockID, block);
        Registry.register(Registries.ITEM, blockID, new BlockItem(registeredBlock, new Item.Settings()));
        return registeredBlock;
    }

    public static final Block CHARGING_STATION = register(//注册充能底座
            new ChargingStation(
                    4.0F,
                    6.0F),
            "charging_station");
    public static final Block LITHIUM_ORE = register(//注册锂矿
            new Block(AbstractBlock.Settings.create()
                    .strength(2.0F, 3.0F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)),
            "lithium_ore");
    public static final Block DEEPSLATE_LITHIUM_ORE = register(//注册深层锂矿
            new Block(AbstractBlock.Settings.create()
                    .strength(3.5F, 3.0F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.DEEPSLATE)),
            "deepslate_lithium_ore");
    public static final Block COBALT_ORE = register(//注册钴矿
            new Block(AbstractBlock.Settings.create()
                    .strength(3.5F, 3.0F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.STONE)),
            "cobalt_ore");
    public static final Block DEEPSLATE_COBALT_ORE = register(//注册深层钴矿
            new Block(AbstractBlock.Settings.create()
                    .strength(5.5F, 3.0F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.DEEPSLATE)),
            "deepslate_cobalt_ore");

    //添加方块到物品栏功能方块组
    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.ZJQTEST_GROUP_KEY).register(entries -> {
            entries.add(CHARGING_STATION);
            entries.add(LITHIUM_ORE);
            entries.add(DEEPSLATE_LITHIUM_ORE);
            entries.add(COBALT_ORE);
            entries.add(DEEPSLATE_COBALT_ORE);
        });
        Zjqtest.LOGGER.info("Blocks loaded successfully!");
    }
}
