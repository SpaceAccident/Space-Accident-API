package space.accident.api.enums;

import static space.accident.api.enums.OrePrefixes.*;

public enum MaterialType {
	DUST_DEFAULT(dust), DUST_METAL(dust),
	INGOT_DEFAULT(ingot), INGOT_METAL(ingot),
	PLATE_DEFAULT(plate), PLATE_METAL(plate),
	
	NONE(null);
	
	public final OrePrefixes ore;
	
	MaterialType(OrePrefixes ore) {
		this.ore = ore;
	}
}