package zjq.zjqtest;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模组通用主入口类：实现 ModInitializer 接口
 * 负责在 Minecraft 启动时统一初始化模组所有核心模块（物品、方块、音效等）
 */
public class Zjqtest implements ModInitializer {
    // 模组唯一标识符（命名空间）
    public static final String MOD_ID = "zjqtest";

    // 模组日志输出工具：使用统一的 MOD_ID 作为标签
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /**
     * 模组主初始化入口：游戏启动时首批执行的代码
     */
    @Override
    public void onInitialize() {
        // 1. 初始化物品注册
        ModItems.initialize();

        // 2. 初始化音效注册
        ModSounds.initialize();

        //3.  初始化方块注册
        ModBlocks.initialize();

        //4.  初始化方块实体注册
        ModBlockEntities.initialize();
        LOGGER.info("success to load!");
    }
}
