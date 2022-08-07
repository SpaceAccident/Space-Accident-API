package space.accident.api.interfaces.tools;

import space.accident.api.enums.Materials;

import java.util.List;

import static net.minecraft.util.EnumChatFormatting.*;
import static space.accident.api.items.GenericItem.transItem;
import static space.accident.api.util.Utility.safeInt;

public interface IToolTurbine {
	
	default void addToolTip(List<String> list, int ind, IToolStats stats, Materials mat, long damage, long maxDamage, float combatDamage, int harvestLevel) {
		int aBaseEff = (int) (5 + combatDamage) * 1000;
		int aOptFlow = safeInt((long) Math.max(Float.MIN_NORMAL, stats.getSpeedMultiplier() * mat.mToolSpeed * 50));
		
		list.add(ind + 0, GRAY + String.format(transItem("001", "Durability: %s/%s"), "" + GREEN + (maxDamage - damage) + " ", " " + maxDamage) + GRAY);
		list.add(ind + 1, GRAY + String.format(transItem("002", "%s lvl %s"), mat.localName + YELLOW, "" + harvestLevel) + GRAY);
		list.add(ind + 2, WHITE + String.format(transItem("005", "Turbine Efficiency: %s"), "" + BLUE + (50.0F + (10.0F * combatDamage))) + "%" + GRAY);
		list.add(ind + 3, WHITE + String.format(transItem("006", "Optimal Steam flow: %s L/t"), "" + GOLD + safeInt((long) (Math.max(Float.MIN_NORMAL, stats.getSpeedMultiplier() * mat.mToolSpeed * (1000 / 20f)))) + GRAY));
		list.add(ind + 4, WHITE + String.format(transItem("900", "Energy from Optimal Steam Flow: %s EU/t"), "" + GOLD + safeInt((long) (Math.max(Float.MIN_NORMAL, stats.getSpeedMultiplier() * mat.mToolSpeed * (1000 / 20f)) * (50.0F + (10.0F * combatDamage)) / 200)) + GRAY));
		
		long[] calculatedFlow = calculateLooseFlow(aOptFlow, aBaseEff);
		int aOptFlowLoose = (int) calculatedFlow[0];
		int aBaseEffLoose = (int) calculatedFlow[1];
		
		list.add(ind + 5, AQUA + String.format(transItem("500", "Turbine Efficiency (Loose): %s"), "" + BLUE + aBaseEffLoose / 100 + "%" + GRAY));
		list.add(ind + 6, AQUA + String.format(transItem("501", "Optimal Steam flow (Loose): %s L/t"), "" + GOLD + aOptFlowLoose + GRAY));
		list.add(ind + 7, AQUA + String.format(transItem("901", "Energy from Optimal Steam Flow (Loose): %s EU/t"), "" + GOLD + (aOptFlowLoose / 10000) * (aBaseEffLoose / 2) + GRAY));
		list.add(ind + 8, GRAY + "(Superheated Steam EU values are 2x those of Steam)");
		list.add(ind + 9, LIGHT_PURPLE + String.format(transItem("007", "Energy from Optimal Gas Flow: %s EU/t"), "" + GOLD + safeInt((long) (Math.max(Float.MIN_NORMAL, stats.getSpeedMultiplier() * mat.mToolSpeed * 50) * (50.0F + (10.0F * combatDamage)) / 100)) + GRAY));
		list.add(ind + 10, LIGHT_PURPLE + String.format(transItem("008", "Energy from Optimal Plasma Flow: %s EU/t"), "" + GOLD + safeInt((long) (Math.max(Float.MIN_NORMAL, stats.getSpeedMultiplier() * mat.mToolSpeed * 2000) * (50.0F + (10.0F * combatDamage)) * (1.05 / 100))) + GRAY));
		
		int toolQualityLevel = mat.mToolQuality;
		int overflowMultiplier;
		if (toolQualityLevel >= 6) {
			overflowMultiplier = 3;
		} else if (toolQualityLevel >= 3) {
			overflowMultiplier = 2;
		} else {
			overflowMultiplier = 1;
		}
		list.add(ind + 11, LIGHT_PURPLE + String.format(transItem("502", "Overflow Efficiency Tier: %s"), "" + GOLD + overflowMultiplier + GRAY));
		list.add(ind + 12, GRAY + "(EU/t values include efficiency and are not 100% accurate)");
	}
	
	public static long[] calculateLooseFlow(int aOptFlow, int aBaseEff) {
		aOptFlow *= 4;
		if (aBaseEff >= 26000) {
			aOptFlow *= Math.pow(1.1f, ((aBaseEff - 8000) / 10000F) * 20f);
			aBaseEff *= 0.6f;
		} else if (aBaseEff > 22000) {
			aOptFlow *= Math.pow(1.1f, ((aBaseEff - 7000) / 10000F) * 20f);
			aBaseEff *= 0.65f;
		} else if (aBaseEff > 18000) {
			aOptFlow *= Math.pow(1.1f, ((aBaseEff - 6000) / 10000F) * 20f);
			aBaseEff *= 0.70f;
		} else if (aBaseEff > 14000) {
			aOptFlow *= Math.pow(1.1f, ((aBaseEff - 5000) / 10000F) * 20f);
			aBaseEff *= 0.75f;
		} else if (aBaseEff > 10000) {
			aOptFlow *= Math.pow(1.1f, ((aBaseEff - 4000) / 10000F) * 20f);
			aBaseEff *= 0.8f;
		} else if (aBaseEff > 6000) {
			aOptFlow *= Math.pow(1.1f, ((aBaseEff - 3000) / 10000F) * 20f);
			aBaseEff *= 0.85f;
		} else {
			aBaseEff *= 0.9f;
		}
		
		if (aBaseEff % 100 != 0) {
			aBaseEff -= aBaseEff % 100;
		}
		
		long[] looseFlow = new long[2];
		looseFlow[0] = safeInt(aOptFlow);
		looseFlow[1] = safeInt(aBaseEff);
		return looseFlow;
	}
}
