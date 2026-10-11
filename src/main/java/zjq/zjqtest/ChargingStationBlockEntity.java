package zjq.zjqtest;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 充能底座方块实体：负责在底座中存储插在上面的剑，并处理存盘和网络同步
 */
public class ChargingStationBlockEntity extends BlockEntity {

    // 核心仓库：存放插在底座上的激光剑（统一命名为 storedSword）
    private ItemStack storedSword = ItemStack.EMPTY;

    private int chargeTime = 0;

    public ChargingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHARGING_STATION_BE, pos, state);
    }

    /**
     * 获取当前插在底座上的剑
     */
    public ItemStack getStoredSword() {
        return this.storedSword;
    }

    /**
     * 存放/更新底座上的剑，并触发存盘与网络同步
     */
    public void setStoredSword(ItemStack stack) {
        this.storedSword = stack;
        this.markDirty();
        if (this.world != null) {
            this.world.updateListeners(this.pos, getCachedState(), getCachedState(), 3);
        }
    }

    //充电速率/s
    public static final int CHARGE_PER_SECOND = 5;

    //充电逻辑
    public static void tick(World world, BlockPos pos, BlockState state, ChargingStationBlockEntity stationBlockEntity) {

        //拒绝客户端
        if (world.isClient) {
            return;
        }
        ItemStack stored = stationBlockEntity.getStoredSword();

        //只有底座内有东西且是激光剑才能充
        if (!stored.isEmpty() && stored.getItem() instanceof EnergyItem energyItem) {
            int currentEnergy = energyItem.getEnergy(stored);//记录当前能量值
            int maxEnergy = energyItem.getMaxEnergy(stored);//记录最大能量值
            //没满再充
            if (currentEnergy < maxEnergy) {
                stationBlockEntity.chargeTime ++;
                //计时器满20tick增加对应能量
                if (stationBlockEntity.chargeTime >= 20) {
                    stationBlockEntity.chargeTime = 0;
                    energyItem.setEnergy(stored, currentEnergy + CHARGE_PER_SECOND);
                    stationBlockEntity.setStoredSword(stored);
                    //播放音效
                    if (currentEnergy + CHARGE_PER_SECOND >= maxEnergy) {
                        world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 0.3F, 1.8F);
                        if (stored.isOf(ModItems.LASER_SWORD)) {
                            LaserSword.setEnergyTicks(stored, 0);
                        }
                    }
                }
            } else {//充满恢复临时tick并跟进时间戳
                stationBlockEntity.chargeTime = 0;
                if (stored.getItem() instanceof LaserSword sword && sword.isActive(stored)) {
                    sword.setLastActiveTime(stored, world.getTime());

                }
            }
        } else {
            stationBlockEntity.chargeTime = 0;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.storedSword.isEmpty()) {
            nbt.put("StoredSword", this.storedSword.encode(registryLookup));
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        if (nbt.contains("StoredSword")) {
            this.storedSword = ItemStack.fromNbtOrEmpty(lookup, nbt.getCompound("StoredSword"));
        } else {
            this.storedSword = ItemStack.EMPTY;
        }
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup wrapperLookup) {
        return createNbt(wrapperLookup);
    }
}
