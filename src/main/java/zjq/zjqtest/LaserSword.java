package zjq.zjqtest;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/**
 * 激光剑物品类：继承原版剑类，实现右键伸缩、动态伤害切换以及开关音效
 */
public class LaserSword extends SwordItem implements EnergyItem{
    // 保存开启状态下的完整伤害和攻速属性修饰符
    private final AttributeModifiersComponent activeAttributes;

    private final int maxEnergy;

    //每秒消耗能量值数
    public static final int ENERGY_PER_SECOND = 1;

    @Override
    public int getMaxEnergy(ItemStack stack) {
        return this.maxEnergy;
    }

    //获取当前能量值
    @Override
    public int getEnergy(ItemStack stack) {
        NbtComponent nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        return nbt.contains("Energy") ? Math.clamp(nbt.copyNbt().getInt("Energy"), 0, this.maxEnergy) : 0;
    }

    //设置能量值和开关方法
    public void setActive(ItemStack stack, boolean active) {
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> {
            nbt.putBoolean("Active", active);

            if (active && getEnergy(stack) > 0) {
                stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, this.activeAttributes);
            } else {
                stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
            }
        });
    }

    @Override
    public void setEnergy(ItemStack stack, int newEnergy) {
        int safeEnergy = Math.clamp(newEnergy, 0, this.maxEnergy);
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> {
            nbt.putInt("Energy", safeEnergy);
        });
    }

    //获取上一次扣能量世界时间
    public long getLastActiveTime(ItemStack stack, World world) {
        NbtComponent nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        return nbt.contains("LastActiveTime") ? nbt.copyNbt().getLong("LastActiveTime") : world.getTime();
    }

    //设置上一次扣能量世界时间
    public void setLastActiveTime(ItemStack stack, long time) {
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> {
            nbt.putLong("LastActiveTime", time);
        });
    }

    //根据世界时间计算消耗的能量
    public int updateEnergyByTime(ItemStack stack, World world) {

        //未激活不动能量
        if (!isActive(stack)) {
            return getEnergy(stack);
        }
        long currentTime = world.getTime();
        long lastTime = getLastActiveTime(stack, world);

        //若世界时间异常,返回最初能量值保险
        if (currentTime < lastTime) {
            setLastActiveTime(stack, currentTime);
            return getEnergy(stack);
        }

        //计算流逝时间
        long passingTime = (currentTime - lastTime) / 20;

        //计算需要消耗的能量并重置时间
        long reducedEnergy = passingTime * ENERGY_PER_SECOND;
        int currentEnergy = getEnergy(stack);

        //输出计算的能量
        if (passingTime > 0) {
            if (currentEnergy > reducedEnergy) {
                int remainingEnergy = (int) (currentEnergy - reducedEnergy);
                setEnergy(stack, remainingEnergy);
                setLastActiveTime(stack, lastTime + passingTime * 20);
                return remainingEnergy;
            } else {
                setEnergy(stack, 0);
                setActive(stack, false);
                setLastActiveTime(stack, currentTime);
                return 0;
            }
        }
        return currentEnergy;
    }

    //开关剑tick零头计算
    public int getEnergyTicks(ItemStack stack) {
        NbtComponent nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        return nbt.contains("EnergyTicks") ? nbt.copyNbt().getInt("EnergyTicks") : 0;
    }

    public static void setEnergyTicks(ItemStack stack, int ticks) {
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> {
            nbt.putInt("EnergyTicks", ticks);
        });
    }

    /**
     * 构造函数：初始化材质与属性，并将计算出的高额伤害保存在 activeAttributes 中
     *
     * @param material         工具材质（包含基础伤害、耐久等）
     * @param baseAttackDamage 额外攻击伤害加成
     * @param attackSpeed      攻击速度（通常为负值）
     * @param settings         物品出厂配置单（稀有度、耐久上限等）
     */
    public LaserSword(
            ToolMaterial material, int baseAttackDamage, float attackSpeed, double sweepingRatio, int maxEnergy, Settings settings) {
        super(material, settings.component(DataComponentTypes.CUSTOM_DATA,
                        NbtComponent.DEFAULT.apply(nbt -> {
                            nbt.putBoolean("Active", false);
                            nbt.putInt("Energy", 0);
                            nbt.putInt("EnergyTicks", 0);
                        }))
                .component(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(false))
                .attributeModifiers(AttributeModifiersComponent.DEFAULT));
        this.maxEnergy = maxEnergy;

        // 计算并缓存开启状态下的伤害和攻速
        this.activeAttributes = AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                BASE_ATTACK_DAMAGE_MODIFIER_ID,
                                (double) baseAttackDamage + material.getAttackDamage(),
                                EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND)
                .add(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(
                                BASE_ATTACK_SPEED_MODIFIER_ID,
                                attackSpeed,
                                EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND)
                .add(
                        EntityAttributes.PLAYER_SWEEPING_DAMAGE_RATIO,
                        new EntityAttributeModifier(
                                Identifier.of(Zjqtest.MOD_ID, "laser_sweeping"),
                                sweepingRatio,
                                EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND).build();
    }

    /**
     * 读取物品的 NBT 自定义数据，判断当前激光剑是否处于展开（激活）状态
     *
     * @param itemStack 当前检查的物品堆
     * @return true 为激活展开状态，false 为关闭收回状态（默认未激活为 false）
     */
    public boolean isActive(ItemStack itemStack) {
        NbtComponent nbt = itemStack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        if (nbt.contains("Active")) {
            return nbt.copyNbt().getBoolean("Active");
        } else {
            return false;
        }
    }

    /**
     * 悬浮提示框：为物品下方添加紫色斜体的描述文本和能量值显示
     */
    @Override
    public void appendTooltip(ItemStack stack,
                              TooltipContext context,
                              List<Text> tooltip,
                              TooltipType type) {
        tooltip.add(
                Text.literal(
                                "一把能够切开一切的等离子光剑。")
                        .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC));
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return false; // 无论什么情况，统统不画原版耐久条！
    }

    /**
     * 鼠标右键使用：切换激光剑的开/关状态、动态赋予/移除伤害属性并播放专属音效
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack stack = playerEntity.getStackInHand(hand);
        boolean nextState = !isActive(stack);

        //能量为0时阻止激光剑开启
        if (nextState && getEnergy(stack) <= 0) {
            //发送能量不足提示信息
            playerEntity.sendMessage(Text.literal("能量不足!").formatted(Formatting.RED), true);
            return TypedActionResult.fail(stack);
        }

        // 根据最新状态切换伤害属性和音效
        // 展开：赋予高额激光伤害并播放激活音效
        if (nextState) {
            setLastActiveTime(stack, world.getTime());
            setEnergy(stack, getEnergy(stack));
            setActive(stack, true);
            playerEntity.playSound(ModSounds.LASER_SWORD_ON, 1.0f, 1.0f);

        } else {        // 收回：清空伤害加成并播放收回音效
            int passingTicks = (int)(world.getTime() - getLastActiveTime(stack, world));
            int totalTicks = passingTicks + getEnergyTicks(stack);
            int energyToDrain = totalTicks / 20;
            setEnergyTicks(stack, totalTicks % 20);
            setEnergy(stack, getEnergy(stack) - energyToDrain * ENERGY_PER_SECOND);
            setActive(stack, false);
            playerEntity.playSound(ModSounds.LASER_SWORD_OFF, 1.0f, 1.0f);
        }

        // 仅在服务端设置短暂冷却，防止快速连点右键
        if (!world.isClient()) {
            playerEntity.getItemCooldownManager().set(this, 5);
        }
        return TypedActionResult.success(stack, world.isClient());
    }


    //能量值损耗系统
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        //是否为服务端运行
        if (world.isClient() || !(entity instanceof PlayerEntity playerEntity)) {
            return;
        }

        if (isActive(stack)) {
            int currentEnergy = updateEnergyByTime(stack, world);
            if (currentEnergy <= 0) {
                world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(),
                        ModSounds.LASER_SWORD_OFF, SoundCategory.PLAYERS, 1.0F, 0.6F);

                playerEntity.sendMessage(Text.literal("能量耗尽！").formatted(Formatting.RED), true);
            }
        }
    }

    //禁止激光剑数据改变引起手部动画
    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        //如果开关剑就播放手部动画
        return isActive(oldStack) != isActive(newStack);  //只是数值改变不播放动画
    }

    public AttributeModifiersComponent getActiveAttributes() {
        return activeAttributes;
    }
}
