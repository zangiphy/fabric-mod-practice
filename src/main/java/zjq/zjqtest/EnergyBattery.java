package zjq.zjqtest;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EnergyBattery extends Item implements EnergyItem{
    private final int maxEnergy;
    //获取最大能量方法
    @Override
    public int getMaxEnergy(ItemStack stack) {return this.maxEnergy;}

    //获取当前能量值
    @Override
    public int getEnergy(ItemStack stack) {
        NbtComponent nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        return nbt.contains("Energy") ? Math.clamp(nbt.copyNbt().getInt("Energy"), 0, this.maxEnergy) : 0;
    }

    //设置能量
    @Override
    public void setEnergy(ItemStack stack, int newEnergy) {
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt -> {
            nbt.putInt("Energy", newEnergy);
        });
    }
    /**
     *
     * @param settings 添加能量标签
     */
    public EnergyBattery(int maxEnergy, Settings settings) {
        super(settings.component(DataComponentTypes.CUSTOM_DATA,
                NbtComponent.DEFAULT.apply(nbt ->{
                    nbt.putInt("Energy", 0);
                })));
        this.maxEnergy = maxEnergy;
    }
}
