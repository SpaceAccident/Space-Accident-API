package space.accident.main.mixins;

import ic2.neiIntegration.core.NEIIC2Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NEIIC2Config.class)
public abstract class Ic2DisabledNEI_Mixin {
	
	@Inject(method = "loadConfig", at = @At("HEAD"), remap = false, cancellable = true)
	private void loadConfig(CallbackInfo ci) {
		ci.cancel();
	}
}
