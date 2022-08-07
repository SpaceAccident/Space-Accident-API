package space.accident.main.common.power;


import space.accident.extensions.NumberUtils;

import static space.accident.api.enums.Values.VN;
import static space.accident.extensions.NumberUtils.format;

public class EUPower extends Power {
	protected final int amperage;
	protected int originalVoltage;
	
	public EUPower(int tier, int amperage) {
		super(tier);
		this.amperage = amperage;
	}
	
	@Override
	// This generic EU Power class has no overclock defined and does no special calculations.
	public void computePowerUsageAndDuration(int euPerTick, int duration) {
		originalVoltage = computeVoltageForEuRate(euPerTick);
		recipeEuPerTick = euPerTick;
		recipeDuration  = duration;
	}
	
	@Override
	public String getTierString() {
		return VN[tier];
	}
	
	@Override
	public String getTotalPowerString() {
		return format((long) recipeDuration * recipeEuPerTick) + " EU";
	}
	
	@Override
	public String getPowerUsageString() {
		return format(recipeEuPerTick) + " EU/t";
	}
	
	@Override
	public String getVoltageString() {
		String voltageDescription = format(originalVoltage) + " EU";
		int recipeTier = NumberUtils.getTier(originalVoltage);
		if (recipeTier >= 0 && recipeTier < 16) {
			voltageDescription += " (" + VN[recipeTier] + ")";
		}
		return voltageDescription;
	}
	
	@Override
	public String getAmperageString() {
		return format(amperage);
	}
	
	protected int computeVoltageForEuRate(int euPerTick) {
		return euPerTick / amperage;
	}
}
