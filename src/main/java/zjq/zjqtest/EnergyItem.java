package zjq.zjqtest;

import net.minecraft.item.ItemStack;

public interface EnergyItem {
    //获取当前能量
    int getEnergy(ItemStack stack);
    //获取最大能量
    int getMaxEnergy(ItemStack stack);
    //设置能量
    void setEnergy(ItemStack stack, int newEnergy);
}
