package ayupitsali.pioneers.mixin;

import ayupitsali.pioneers.data.LivesGroup;
import ayupitsali.pioneers.data.ModComponents;
import ayupitsali.pioneers.data.PioneerData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {
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
                        attackingPlayer.sendMessage(Text.translatable("lives.gained_life.kill", 1));
                    }
                } else if (attackerGroup.equals(LivesGroup.RED)) {
                    if (playerGroup.equals(LivesGroup.GREEN) || playerGroup.equals(LivesGroup.YELLOW)) {
                        attackerData.addLives(1);
                        attackingPlayer.sendMessage(Text.translatable("lives.gained_life.kill", 1));
                    }
                }
            }
            playerData.addLives(-1);
            sendMessage(Text.translatable("lives.lives_changed.death", playerData.getLives()));
            LivesGroup newGroup = playerData.getLivesGroup();
            if (!newGroup.equals(playerGroup)) { // Group has changed
                sendMessage(Text.translatable("lives.group_changed.death", newGroup.getDisplayName()));
            }
        }
    }
}