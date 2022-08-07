package space.accident.api.interfaces.tileentity;

import static space.accident.api.enums.Values.ALL_VALID_SIDES;

public interface ITurn {
	/**
	 * Get the block's facing.
	 *
	 * @return front Block facing
	 */
	int getFrontFace();
	
	/**
	 * Set the block's facing
	 *
	 * @param side facing to set the block to
	 */
	void setFrontFace(int side);
	
	/**
	 * Get the block's back facing.
	 *
	 * @return opposite Block facing
	 */
	int getBackFace();
	
	/**
	 * Determine if the wrench can be used to set the block's facing.
	 */
	boolean isValidFace(int side);
	
	/**
	 * Get the list of valid facings
	 */
	default boolean[] getValidFaces() {
		final boolean[] validFacings = new boolean[6];
		for(int facing : ALL_VALID_SIDES) {
			validFacings[facing] = isValidFace(facing);
		}
		return validFacings;
	}
}