package ayupitsali.pioneers.mixin;

import ayupitsali.pioneers.data.LivesGroup;
import ayupitsali.pioneers.data.ModComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public abstract class MixinPlayerListHud {
    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void onGetPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) {
            PlayerEntity playerEntity = world.getPlayerByUuid(entry.getProfile().getId());
            if (playerEntity != null) {
                LivesGroup playerGroup = ModComponents.PIONEER_DATA.get(playerEntity).getLivesGroup();
                cir.setReturnValue(cir.getReturnValue().copy().formatted(playerGroup.getColourFormatting()));
            }
        }
    }
}
