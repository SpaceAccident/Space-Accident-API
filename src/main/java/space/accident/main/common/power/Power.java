package space.accident.main.common.power;


import static space.accident.extensions.NumberUtils.format;
import static space.accident.extensions.StringUtils.trans;

public abstract class Power {
	protected final int tier;
	protected int recipeEuPerTick;
	protected int recipeDuration;
	
	public Power(int tier) {
		this.tier = tier;
	}
	
	public int getTier() {
		return tier;
	}
	
	public abstract String getTierString();
	
	/**
	 * This method should be called prior to usage of any value except the power tier.
	 */
	public abstract void computePowerUsageAndDuration(int euPerTick, int duration);
	
	public int getEuPerTick() {
		return recipeEuPerTick;
	}
	
	public int getDurationTicks() {
		return recipeDuration;
	}
	
	public double getDurationSeconds() {
		return 0.05d * getDurationTicks();
	}
	
	public String getDurationStringSeconds() {
		return format(getDurationSeconds()) + trans("161", " secs");
	}
	
	public String getDurationStringTicks() {
		return format(getDurationTicks()) + trans("224", " ticks");
	}
	
	public abstract String getTotalPowerString();
	
	public abstract String getPowerUsageString();
	
	public abstract String getVoltageString();
	
	public abstract String getAmperageString();
}
