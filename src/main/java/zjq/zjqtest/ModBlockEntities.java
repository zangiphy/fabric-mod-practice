package zjq.zjqtest;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    //注册方法
    public static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType<T> type) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Zjqtest.MOD_ID, id), type);
    }

    //注册chargingstationblockentity
    public static final BlockEntityType<ChargingStationBlockEntity> CHARGING_STATION_BE = register(
            "charging_station",
            BlockEntityType.Builder.create(ChargingStationBlockEntity::new, ModBlocks.CHARGING_STATION).build()
    );

    //初始化
    public static void initialize() {
        Zjqtest.LOGGER.info("Block entities registered successfully!");
    }
}
