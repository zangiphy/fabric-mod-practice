package zjq.zjqtest;

import com.google.common.base.Suppliers;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.function.Supplier;

/**
 * 自定义工具材质枚举：实现原版 ToolMaterial 接口
 * 作用：统一定义材质的挖掘等级、耐久上限、基础伤害、附魔属性与修复材料
 */
public enum ItemMaterials implements ToolMaterial {

    // 专属激光材质条目：具备钻石级挖掘能力、3000 耐久、12 挖掘速度、5 基础伤害、20 附魔品质、消耗红石修复
    LASER(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            3000,
            12.0f,
            5.0f,
            20,
            () -> Ingredient.ofItems(Items.REDSTONE)
    ),

    COBALT(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            768,
            7.0F,
            2.0F,
            12,
            () -> Ingredient.ofItems(ModItems.COBALT_INGOT)

    );

    private final TagKey<Block> inverseTag;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairIngredient;

    // 构造方法：保存各项物理和战斗属性，并通过 Suppliers.memoize 缓存修复材料以提高性能
    ItemMaterials(TagKey<Block> inverseTag, int itemDurability, float miningSpeed,
                  float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient) {
        this.inverseTag = inverseTag;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = Suppliers.memoize(repairIngredient::get);
    }

    @Override
    public int getDurability() {
        return this.itemDurability; // 返回该材质制作工具时的最大耐久度（3000）
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed; // 返回挖掘方块的速度加成倍率
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage; // 返回该材质提供的基础攻击力加成
    }

    @Override
    public TagKey<Block> getInverseTag() {
        return this.inverseTag; // 返回不能挖掘的方块标签（决定挖掘等级）
    }

    @Override
    public int getEnchantability() {
        return this.enchantability; // 返回附魔能力数值（越高越容易出高阶附魔）
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get(); // 返回在铁砧中修理该工具所需的材料
    }
}

