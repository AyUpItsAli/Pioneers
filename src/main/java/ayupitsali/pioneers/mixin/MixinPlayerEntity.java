package ayupitsali.pioneers.mixin;

import ayupitsali.pioneers.data.LivesGroup;
import ayupitsali.pioneers.data.PioneerData;
import ayupitsali.pioneers.data.PioneersDataComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {
    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "dropInventory", at = @At("HEAD"))
    private void onDropInventory(final CallbackInfo info) {
        PioneerData pioneerData = PioneersDataComponent.getPioneerData(this);
        LivesGroup playerGroup = pioneerData.getLivesGroup();
        if (!playerGroup.equals(LivesGroup.GHOST)) {
            if (attackingPlayer != null) {
                PioneerData attackerData = PioneersDataComponent.getPioneerData(attackingPlayer);
                LivesGroup attackerGroup = attackerData.getLivesGroup();
                if (attackerGroup.equals(LivesGroup.YELLOW)) {
                    if (playerGroup.equals(LivesGroup.GREEN)) {
                        attackerData.addLives(1);
                        attackingPlayer.sendMessage(Text.translatable("lives.gained_life.kill", new Object[]{PioneerData.getLivesText(1, Formatting.GREEN)}));
                    }
                } else if (attackerGroup.equals(LivesGroup.RED)) {
                    if (playerGroup.equals(LivesGroup.GREEN) || playerGroup.equals(LivesGroup.YELLOW)) {
                        attackerData.addLives(1);
                        attackingPlayer.sendMessage(Text.translatable("lives.gained_life.kill", new Object[]{PioneerData.getLivesText(1, Formatting.GREEN)}));
                    }
                }
            }
            pioneerData.addLives(-1);
            sendMessage(Text.translatable("lives.lives_changed.death", new Object[]{pioneerData.getLivesDisplay()}));
        }
    }

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Text> cir) {
        LivesGroup playerGroup = PioneersDataComponent.getPioneerData(this).getLivesGroup();
        cir.setReturnValue(cir.getReturnValue().copy().formatted(playerGroup.getColourFormatting()));
    }
}