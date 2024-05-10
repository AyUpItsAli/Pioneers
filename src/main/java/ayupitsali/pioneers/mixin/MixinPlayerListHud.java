package ayupitsali.pioneers.mixin;

import ayupitsali.pioneers.data.LivesGroup;
import ayupitsali.pioneers.data.ModComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public abstract class MixinPlayerListHud {
    @Shadow protected abstract Text applyGameModeFormatting(PlayerListEntry entry, MutableText name);

    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void onGetPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) {
            PlayerEntity playerEntity = world.getPlayerByUuid(entry.getProfile().getId());
            if (playerEntity != null) {
                LivesGroup playerGroup = ModComponents.PIONEER_DATA.get(playerEntity).getLivesGroup();
                MutableText playerName = playerEntity.getName().copy().formatted(playerGroup.getColourFormatting());
                cir.setReturnValue(applyGameModeFormatting(entry, playerName));
            }
        }
    }
}
