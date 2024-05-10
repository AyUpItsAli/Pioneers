package ayupitsali.pioneers.mixin;

import ayupitsali.pioneers.data.LivesGroup;
import ayupitsali.pioneers.data.ModComponents;
import ayupitsali.pioneers.data.PioneerData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {
    @Shadow protected abstract MutableText addTellClickEvent(MutableText component);

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "dropInventory", at = @At("HEAD"))
    private void onDropInventory(final CallbackInfo info) {
        PioneerData playerData = ModComponents.PIONEER_DATA.get(this);
        LivesGroup playerGroup = playerData.getLivesGroup();
        if (!playerGroup.equals(LivesGroup.GHOST)) {
            if (attackingPlayer != null) {
                PioneerData attackerData = ModComponents.PIONEER_DATA.get(attackingPlayer);
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
            playerData.addLives(-1);
            sendMessage(Text.translatable("lives.lives_changed.death", new Object[]{playerData.getLivesDisplay()}));
        }
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Text> cir) {
        LivesGroup playerGroup = ModComponents.PIONEER_DATA.get(this).getLivesGroup();
        MutableText displayName = getName().copy().formatted(playerGroup.getColourFormatting());
        cir.setReturnValue(addTellClickEvent(displayName));
    }
}