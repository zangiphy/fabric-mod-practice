package zjq.zjqtest;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

/**
 * 声音事件注册管理类：负责声明并注册模组专属的所有自定义音效
 */
public class ModSounds {

    /**
     * 辅助注册方法：将音效 ID 包装为 SoundEvent，并注册进原版的 SOUND_EVENT 注册表
     *
     * @param id 音效路径名称（需与 sounds.json 中的定义一致）
     * @return 注册完成后的 SoundEvent 实例
     */
    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(Zjqtest.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    // 激光剑开剑音效：出鞘、展开激光刃
    public static final SoundEvent LASER_SWORD_ON = registerSound("laser_sword_on");
    // 激光剑关剑音效：收起、关闭激光刃
    public static final SoundEvent LASER_SWORD_OFF = registerSound("laser_sword_off");

    /**
     * 音效初始化方法：由模组主类调用，触发静态字段的加载和注册
     */
    public static void initialize() {
        Zjqtest.LOGGER.info("Sound effects loaded sucessfully!");
    }
}

