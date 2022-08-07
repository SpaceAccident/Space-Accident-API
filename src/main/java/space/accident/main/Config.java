package space.accident.main;

public class Config {
	
	
	/**
	 * How verbose should tooltips be? 0: disabled, 1: one-line, 2: normal, 3+: extended
	 */
	public static int mTooltipVerbosity = 2;
	
	/**
	 * How verbose should tooltips be when LSHIFT is held? 0: disabled, 1: one-line, 2: normal, 3+: extended
	 */
	public static int mTooltipShiftVerbosity = 3;
	
	/**
	 *  This enables ambient-occlusion smooth lighting on tiles
	 */
	public static boolean mRenderTileAmbientOcclusion = true;
	
	/**
	 * This enables rendering of glowing textures
	 */
	public static boolean mRenderGlowTextures = true;
	
	/**
	 * Render flipped textures
	 */
	public static boolean mRenderFlippedMachinesFlipped = true;
	
	/**
	 * This enables indicators on input/output hatches
	 */
	public static boolean mRenderIndicatorsOnHatch = true;
	
	/**
	 * This enables the rendering of dirt particles if pollution is enabled too
	 */
	public static boolean mRenderDirtParticles = true;
	
	/**
	 * This enables the rendering of the pollution fog if pollution is enabled too
	 */
	public static boolean mRenderPollutionFog = true;
	
	/**
	 * This makes cover tabs visible on machines
	 */
	public static boolean mCoverTabsVisible = true;
	
	/**
	 * This controls whether cover tabs display on the left (default) or right side of the UI
	 */
	public static boolean mCoverTabsFlipped = false;
}
