package zjq.zjqtest;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

//创建物品组
public class ModItemGroups {
    public static final RegistryKey<ItemGroup> ZJQTEST_GROUP_KEY = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            Identifier.of(Zjqtest.MOD_ID, "zjqtest_group")
    );

    public static final ItemGroup ZJQTEST_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            Identifier.of(Zjqtest.MOD_ID, "zjqtest_group"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.zjqtest.zjqtest_group"))
                    .icon(() -> new ItemStack(ModBlocks.CHARGING_STATION))
                    .build()
    );
    //初始化
    public static void initialize() {
        Zjqtest.LOGGER.info("Item Groups registered successfully!");
    }
}
