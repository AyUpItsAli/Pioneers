package ayupitsali.pioneers.mixin;

import ayupitsali.pioneers.data.LivesGroup;
import ayupitsali.pioneers.data.Pioneer;
import ayupitsali.pioneers.data.PioneersData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
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
        Pioneer pioneer = PioneersData.getPioneer(this);
        LivesGroup pioneerGroup = pioneer.getLivesGroup();
        if (!pioneerGroup.equals(LivesGroup.GHOST)) {
            if (attackingPlayer != null) {
                Pioneer attacker = PioneersData.getPioneer(attackingPlayer);
                LivesGroup attackerGroup = attacker.getLivesGroup();
                if (attackerGroup.equals(LivesGroup.YELLOW)) {
                    if (pioneerGroup.equals(LivesGroup.GREEN)) {
                        attacker.addLives(1);
                        attackingPlayer.sendMessage(Text.translatable("lives.gained_life", new Object[]{Pioneer.getLivesText(1, Formatting.GREEN)}));
                    }
                } else if (attackerGroup.equals(LivesGroup.RED)) {
                    if (pioneerGroup.equals(LivesGroup.GREEN) || pioneerGroup.equals(LivesGroup.YELLOW)) {
                        attacker.addLives(1);
                        attackingPlayer.sendMessage(Text.translatable("lives.gained_life", new Object[]{Pioneer.getLivesText(1, Formatting.GREEN)}));
                    }
                }
            }
            pioneer.addLives(-1);
            int newLives = pioneer.getLives();
            if (newLives == 0) {
                World world = getWorld();
                LightningEntity lightningEntity = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
                lightningEntity.setCosmetic(true);
                lightningEntity.setPosition(getPos());
                world.spawnEntity(lightningEntity);
                world.getPlayers().forEach(playerEntity -> playerEntity.sendMessage(Text.translatable("lives.out_of_lives", new Object[]{getDisplayName()})));
            } else {
                sendMessage(Text.translatable("lives.lives_changed", new Object[]{pioneer.getLivesDisplay()}));
            }
        }
    }

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void onGetDisplayName(CallbackInfoReturnable<Text> cir) {
        LivesGroup livesGroup = PioneersData.getPioneer(this).getLivesGroup();
        cir.setReturnValue(cir.getReturnValue().copy().formatted(livesGroup.getColourFormatting()));
    }
}