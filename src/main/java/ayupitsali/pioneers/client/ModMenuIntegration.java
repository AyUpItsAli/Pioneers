package ayupitsali.pioneers.client;

import ayupitsali.pioneers.Pioneers;
import ayupitsali.pioneers.PioneersConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> PioneersConfig.getScreen(parent, Pioneers.MOD_ID);
    }
}
