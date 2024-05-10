package ayupitsali.pioneers.mixin;

import ayupitsali.pioneers.data.LivesGroup;
import ayupitsali.pioneers.data.ModComponents;
import ayupitsali.pioneers.data.PioneersDataComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
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
            PioneersDataComponent component = ModComponents.PIONEERS_DATA.get(world.getScoreboard());
            LivesGroup playerGroup = component.getPioneerData(entry.getProfile().getId().toString()).getLivesGroup();
            cir.setReturnValue(cir.getReturnValue().copy().formatted(playerGroup.getColourFormatting()));
        }
    }
}
