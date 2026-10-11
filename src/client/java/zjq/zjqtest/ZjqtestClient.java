package zjq.zjqtest;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * 客户端专属主入口类：实现 ClientModInitializer 接口
 * 专门处理只在玩家电脑上生效的渲染逻辑，如模型谓词、粒子效果、客户端屏幕等
 */
public class ZjqtestClient implements ClientModInitializer {

    /**
     * 客户端初始化入口：在游戏加载材质和渲染系统时执行
     */

    @Override
    public void onInitializeClient() {
        registerEnergyTooltips();

        // 注册客户端专属的模型谓词（Model Predicate）
        // 作用：向 JSON 模型暴露一个名为 "zjqtest:active" 的浮点数参数（1.0F 为开，0.0F 为关），用于驱动模型根据开关动态切换
        ModelPredicateProviderRegistry.register(
                ModItems.LASER_SWORD,
                Identifier.of(Zjqtest.MOD_ID, "active"),
                (stack, world, entity, seed) -> {
                    // 读取激光剑状态：开启返回 1.0F，关闭返回 0.0F
                    if (stack.getItem() instanceof LaserSword laserSword) {
                        return laserSword.isActive(stack) ? 1.0F : 0.0F;
                    }
                    return 1.0F;
                }
        );

        // 把充能底座实体类型，和刚才写的渲染器绑定在一起
        BlockEntityRendererFactories.register(ModBlockEntities.CHARGING_STATION_BE, ChargingStationBlockEntityRenderer::new);
    }

    private void registerEnergyTooltips() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.getItem() instanceof LaserSword laserSword) {
                ClientWorld world = MinecraftClient.getInstance().world;
                int displayEnergy = (world != null) ? laserSword.updateEnergyByTime(stack, world) : laserSword.getEnergy(stack);
                lines.add(Text.literal("能量值:" + displayEnergy + '/' + laserSword.getMaxEnergy(stack))
                        .formatted(Formatting.AQUA));
            }

            if (stack.getItem() instanceof EnergyBattery energyBattery) {
                lines.add(Text.literal("能量值:" + energyBattery.getEnergy(stack) + '/' + energyBattery.getMaxEnergy(stack))
                        .formatted(Formatting.AQUA));
            }
        });
    }
}
