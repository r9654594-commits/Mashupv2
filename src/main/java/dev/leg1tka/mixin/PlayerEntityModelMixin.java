package dev.leg1tka.mixin;

import dev.leg1tka.modules.render.CustomModels;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BipedEntityModel.class})
public class PlayerEntityModelMixin {
   @Shadow
   @Final
   public ModelPart head;
   @Shadow
   @Final
   public ModelPart body;
   @Shadow
   @Final
   public ModelPart rightArm;
   @Shadow
   @Final
   public ModelPart leftArm;
   @Shadow
   @Final
   public ModelPart rightLeg;
   @Shadow
   @Final
   public ModelPart leftLeg;

   @Inject(
      method = {"setAngles"},
      at = {@At("TAIL")}
   )
   private void AspectDLC$applyCustomModelTransforms(BipedEntityRenderState state, CallbackInfo ci) {
      if (CustomModels.INSTANCE != null && CustomModels.INSTANCE.isEnable()) {
         float headScale = CustomModels.INSTANCE.getHeadScale();
         float bodyScale = CustomModels.INSTANCE.getBodyScale();
         float armsScale = CustomModels.INSTANCE.getArmsScale();
         float legsScale = CustomModels.INSTANCE.getLegsScale();
         this.head.xScale = headScale;
         this.head.yScale = headScale;
         this.head.zScale = headScale;
         this.body.xScale = bodyScale;
         this.body.yScale = bodyScale * 0.9F;
         this.body.zScale = bodyScale;
         this.rightArm.xScale = armsScale;
         this.rightArm.yScale = 1.0F;
         this.rightArm.zScale = armsScale;
         this.leftArm.xScale = armsScale;
         this.leftArm.yScale = 1.0F;
         this.leftArm.zScale = armsScale;
         this.rightLeg.xScale = bodyScale * 0.8F;
         this.rightLeg.yScale = legsScale;
         this.rightLeg.zScale = bodyScale * 0.8F;
         this.leftLeg.xScale = bodyScale * 0.8F;
         this.leftLeg.yScale = legsScale;
         this.leftLeg.zScale = bodyScale * 0.8F;
         float legYOffset = (1.0F - legsScale) * 12.0F;
         this.rightLeg.pivotY += legYOffset;
         this.leftLeg.pivotY += legYOffset;
      } else {
         this.head.xScale = 1.0F;
         this.head.yScale = 1.0F;
         this.head.zScale = 1.0F;
         this.body.xScale = 1.0F;
         this.body.yScale = 1.0F;
         this.body.zScale = 1.0F;
         this.rightArm.xScale = 1.0F;
         this.rightArm.yScale = 1.0F;
         this.rightArm.zScale = 1.0F;
         this.leftArm.xScale = 1.0F;
         this.leftArm.yScale = 1.0F;
         this.leftArm.zScale = 1.0F;
         this.rightLeg.xScale = 1.0F;
         this.rightLeg.yScale = 1.0F;
         this.rightLeg.zScale = 1.0F;
         this.leftLeg.xScale = 1.0F;
         this.leftLeg.yScale = 1.0F;
         this.leftLeg.zScale = 1.0F;
      }
   }
}
