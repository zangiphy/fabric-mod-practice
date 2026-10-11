package zjq.zjqtest;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;


public class ChargingStation extends Block implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    /*构造充电底座
    hardness硬度
    resistence爆炸抗性
    */
    public ChargingStation(float hardness, float resistence) {
        super(AbstractBlock.Settings.create()
                .strength(hardness, resistence)
                .requiresTool()
                .sounds(BlockSoundGroup.METAL)
                .nonOpaque());
    }

    //碰撞箱修改,符合模型
    private static final VoxelShape SHAPE = VoxelShapes.union(
            // 1. 底部厚底座 (高 9)
            Block.createCuboidShape(0, 0, 0, 16, 9, 16),

            // 2. 中间台面和充电触点凸起
            Block.createCuboidShape(3, 9, 3, 13, 10, 13),
            Block.createCuboidShape(7, 10, 7, 9, 11, 9),

            // 3. 四根竖向立柱 (zhu1 ~ zhu4)
            Block.createCuboidShape(0, 9, 7, 2, 16, 9),   // 西柱
            Block.createCuboidShape(14, 9, 7, 16, 16, 9), // 东柱
            Block.createCuboidShape(7, 9, 14, 9, 16, 16), // 南柱
            Block.createCuboidShape(7, 9, 0, 9, 16, 2),   // 北柱

            // 4. 四根向内弯曲的横杆 (gan1 ~ gan4)
            Block.createCuboidShape(2, 14, 7, 6, 16, 9),   // gan1
            Block.createCuboidShape(10, 14, 7, 14, 16, 9), // gan2
            Block.createCuboidShape(7, 14, 10, 9, 16, 14), // gan3
            Block.createCuboidShape(7, 14, 2, 9, 16, 6)    // gan4
    );


    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE; // 准星瞄准时，黑色边框只贴在有模型的实体上
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE; // 玩家和箭矢、掉落物的物理实体碰撞准确
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChargingStationBlockEntity(pos, state);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if ((world.getBlockEntity(pos) instanceof ChargingStationBlockEntity station)) {
            ItemStack storedSword = station.getStoredSword();
            //底座是空的就就存入
            if (storedSword.isEmpty() && stack.getItem() instanceof EnergyItem) {
                if (!world.isClient) {
                    world.playSound(null, pos, SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                station.setStoredSword(stack.copyWithCount(1));
                stack.decrement(1);
                return ItemActionResult.CONSUME;
            }
            //底座是满的手里是空的就取出
            if (!storedSword.isEmpty() && stack.isEmpty()) {
                if (!world.isClient) {
                    world.playSound(null, pos, SoundEvents.BLOCK_NETHERITE_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                station.setStoredSword(ItemStack.EMPTY);
                player.setStackInHand(hand, storedSword.copy());
                return ItemActionResult.CONSUME;
            }
        }
        //都不是就返回原版交互
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    //给充电底座附加tick心跳
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState blockState, BlockEntityType<T> type) {
        if (world.isClient) {
            return null;
        }
        if (type == ModBlockEntities.CHARGING_STATION_BE) {
            return (w, p, s, be) -> ChargingStationBlockEntity.tick(w, p, s, (ChargingStationBlockEntity) be);
        }
        return null;
    }

    //破坏把剑变成凋落物返还
    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(!state.isOf(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof ChargingStationBlockEntity station) {
                ItemStack storedSword = station.getStoredSword();
                if (!storedSword.isEmpty()) {
                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), storedSword);
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
