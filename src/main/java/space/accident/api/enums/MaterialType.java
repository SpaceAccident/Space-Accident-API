package space.accident.api.enums;

import static space.accident.api.enums.OrePrefixes.*;

public enum MaterialType {
	DUST_DEFAULT(dust), DUST_METAL(dust),
	INGOT_DEFAULT(ingot), INGOT_METAL(ingot),
	PLATE_DEFAULT(plate), PLATE_METAL(plate),
	
	BLOCK_DEFAULT(block), BLOCK_METAL(block),
	
	WIRE1(wireGt1), WIRE2(wireGt2), WIRE3(wireGt3), WIRE4(wireGt4), WIRE6(wireGt6), WIRE8(wireGt8), WIRE9(wireGt9), WIRE12(wireGt12), WIRE16(wireGt16),
	
	NONE(null);
	
	public final OrePrefixes ore;
	
	MaterialType(OrePrefixes ore) {
		this.ore = ore;
	}
}