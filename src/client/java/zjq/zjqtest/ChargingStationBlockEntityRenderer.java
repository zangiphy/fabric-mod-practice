package zjq.zjqtest;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class ChargingStationBlockEntityRenderer implements BlockEntityRenderer<ChargingStationBlockEntity> {
    private final ItemRenderer itemRenderer;
    public ChargingStationBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ChargingStationBlockEntity entity, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider provider, int light, int overLay) {
        ItemStack sword = entity.getStoredSword();
        if (sword.isEmpty()) {
            return;
        }
        int realLight = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
        //渲染剑
        if (sword.isOf(ModItems.LASER_SWORD)) {
            matrixStack.push();
            matrixStack.translate(0.5, 1.2- (3*0.0625)-0.015625, 0.5-0.0625);//平移
            matrixStack.scale(1.0F, 1.0F, 1.0F);//缩放
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));//旋转
            this.itemRenderer.renderItem(
                    sword,                          //物品
                    ModelTransformationMode.FIXED,  //渲染模式
                    realLight,                      //环境光照
                    overLay,                        //覆盖层
                    matrixStack,                    //空间位置
                    provider,                       //顶点缓冲区
                    entity.getWorld(),              //所在世界
                    0                               //种子随机数
            );
            matrixStack.pop();
        }
        //渲染电池
        else if (sword.isOf(ModItems.ENERGY_BATTERY)) {
            matrixStack.push();
            matrixStack.translate(0.5, 1.2- (3*0.0625)-0.015625-0.1875, 0.5-0.0625);//平移
            this.itemRenderer.renderItem(
                    sword,                          //物品
                    ModelTransformationMode.FIXED,  //渲染模式
                    realLight,                      //环境光照
                    overLay,                        //覆盖层
                    matrixStack,                    //空间位置
                    provider,                       //顶点缓冲区
                    entity.getWorld(),              //所在世界
                    0                               //种子随机数
            );
            matrixStack.pop();
        }
    }
}
