package zjq.zjqtest;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

/**
 * 物品注册管理类：集中声明并注册模组所有的物品，并将其添加至创造模式物品栏
 */
public class ModItems {

    /**
     * 辅助注册方法：将物品绑定模组 ID 并注册进原版 ITEM 注册表中
     *
     * @param item 要注册的物品实例
     * @param id   物品的路径 ID（例如 "laser_sword"）
     * @return 注册完成后的物品对象
     */
    public static Item register(Item item, String id) {
        Identifier itemID = Identifier.of(Zjqtest.MOD_ID, id);
        return Registry.register(Registries.ITEM, itemID, item);
    }

    public static final Item LASER_SWORD = register(        // 激光剑物品实例：使用专属 LASER 材质、额外 +4 伤害、-2.4F 攻速、史诗级紫色品质
            new LaserSword(
                    ItemMaterials.LASER,
                    4,
                    -2.0F,
                    1.0,
                    1000,
                    new Item.Settings()
                            .rarity(Rarity.EPIC)),
            "laser_sword");
    public static final Item ENERGY_BATTERY = register(     //注册能量电池
            new EnergyBattery(
                    1000,
                    new Item.Settings()
                            .maxCount(1)
                            .rarity(Rarity.RARE)),
            "energy_battery");
    public static final Item COBALT_PICKAXE = register(     //注册钴镐
            new PickaxeItem(ItemMaterials.COBALT,
                    new Item.Settings().attributeModifiers(PickaxeItem.createAttributeModifiers(ItemMaterials.COBALT, 1.0F, -2.8F))),
            "cobalt_pickaxe");
    public static final Item COBALT_SHOVEL = register(      //注册钴锹
            new ShovelItem(ItemMaterials.COBALT,
                    new Item.Settings().attributeModifiers(ShovelItem.createAttributeModifiers(ItemMaterials.COBALT, 2.0F, -3.0F))),
            "cobalt_shovel");
    public static final Item COBALT_HOE = register(         //注册钴锄
            new HoeItem(ItemMaterials.COBALT,
                    new Item.Settings().attributeModifiers(HoeItem.createAttributeModifiers(ItemMaterials.COBALT, -2.0F, -0.0F))),
            "cobalt_hoe");
    public static final Item COBALT_AXE = register(         //注册钴斧
            new AxeItem(ItemMaterials.COBALT,
                    new Item.Settings().attributeModifiers(AxeItem.createAttributeModifiers(ItemMaterials.COBALT, 6.0F, -3.0F))),
            "cobalt_axe");
    public static final Item COBALT_SWORD = register(       //注册钴剑
            new SwordItem(ItemMaterials.COBALT,
                    new Item.Settings().attributeModifiers(SwordItem.createAttributeModifiers(ItemMaterials.COBALT, 4, -2.4F))),
            "cobalt_sword");
    public static final Item LITHIUM_INGOT = register(      //注册锂锭
            new Item(new Item.Settings()),
            "lithium_ingot");
    public static final Item RAW_LITHIUM = register(        //注册粗锂
            new Item(new Item.Settings()),
            "raw_lithium");
    public static final Item COBALT_INGOT = register(       //注册钴锭
            new Item(new Item.Settings()),
            "cobalt_ingot");
    public static final Item RAW_COBALT = register(         //注册粗钴
            new Item(new Item.Settings()),
            "raw_cobalt");
    public static final Item GRAPHITE = register(           //注册石墨
            new Item(new Item.Settings()),
            "graphite");
    public static final Item BATTERY_SHELL = register(      //注册电池壳
            new Item(new Item.Settings()),
            "battery_shell");
    public static final Item ELECTROLYTE_BOTTLE = register( //注册电解液瓶
            new Item(new Item.Settings()),
            "electrolyte_bottle");


    /**
     * 物品初始化方法：由主类调用，触发静态变量加载，并将物品添加至创造栏
     */
    public static void initialize() {
        // 将物品添加进原版创造模式背包栏
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.ZJQTEST_GROUP_KEY).register(entries -> {
            entries.add(LASER_SWORD);
            entries.add(ENERGY_BATTERY);
            entries.add(COBALT_PICKAXE);
            entries.add(COBALT_SHOVEL);
            entries.add(COBALT_HOE);
            entries.add(COBALT_AXE);
            entries.add(COBALT_SWORD);
            entries.add(LITHIUM_INGOT);
            entries.add(RAW_LITHIUM);
            entries.add(COBALT_INGOT);
            entries.add(RAW_COBALT);
            entries.add(GRAPHITE);
            entries.add(BATTERY_SHELL);
            entries.add(ELECTROLYTE_BOTTLE);
        });
        Zjqtest.LOGGER.info("Items loaded succesfully!");
    }
}
