package space.accident.api.metatileentity.base;

import org.lwjgl.input.Keyboard;
import space.accident.api.API;
import space.accident.api.interfaces.ISecondaryDescribable;
import space.accident.api.metatileentity.implementations.mutlis.Multiblock_Tooltip_Builder;

import java.util.concurrent.atomic.AtomicReferenceArray;

public abstract class TooltipMultiBlockBase extends MultiBlockBase implements ISecondaryDescribable {
	
	private static final AtomicReferenceArray<Multiblock_Tooltip_Builder> tooltips = new AtomicReferenceArray<>(API.METATILEENTITIES.length);
	
	public TooltipMultiBlockBase(int id, String name, String aNameRegional) {
		super(id, name, aNameRegional);
	}
	
	public TooltipMultiBlockBase(String name) {
		super(name);
	}
	
	protected Multiblock_Tooltip_Builder getTooltip() {
		int tId = getBaseMetaTileEntity().getMetaTileID();
		Multiblock_Tooltip_Builder tooltip = tooltips.get(tId);
		if (tooltip == null) {
			tooltip = createTooltip();
			tooltips.set(tId, tooltip);
		}
		return tooltip;
	}
	
	protected abstract Multiblock_Tooltip_Builder createTooltip();
	
	@Override
	public String[] getDescription() {
		return getCurrentDescription();
	}
	
	@Override
	public boolean isDisplaySecondaryDescription() {
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
	}
	
	public String[] getPrimaryDescription() {
		return getTooltip().getInformation();
	}
	
	public String[] getSecondaryDescription() {
		return getTooltip().getStructureInformation();
	}
}