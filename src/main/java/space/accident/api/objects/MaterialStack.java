package space.accident.api.objects;

import space.accident.api.enums.MaterialList;
import space.accident.api.enums.Materials;
import space.accident.api.util.Utility;

public class MaterialStack implements Cloneable {
	public long mAmount;
	public Materials mMaterial;
	
	public MaterialStack(Materials aMaterial, long amount) {
		mMaterial = aMaterial == null ? MaterialList._NULL : aMaterial;
		mAmount = amount;
	}
	
	public MaterialStack copy(long amount) {
		return new MaterialStack(mMaterial, amount);
	}
	
	@Override
	public MaterialStack clone() {
		try { return (MaterialStack) super.clone(); } catch (Exception e) { return new MaterialStack(mMaterial, mAmount); }
	}
	
	@Override
	public boolean equals(Object aObject) {
		if (aObject == this) return true;
		if (aObject == null) return false;
		if (aObject instanceof Materials) return aObject == mMaterial;
		if (aObject instanceof MaterialStack)
			return ((MaterialStack) aObject).mMaterial == mMaterial && (mAmount < 0 || ((MaterialStack) aObject).mAmount < 0 || ((MaterialStack) aObject).mAmount == mAmount);
		return false;
	}
	
	@Override
	public String toString() {
		String temp1 = "", temp2 = mMaterial.getToolTip(true), temp3 = "", temp4 = "";
		if (mAmount > 1) {
			temp4 = Utility.toSubscript(mAmount);
			
			if (mMaterial.materialList.size() > 1 || isMaterialListComplex(this)) {
				temp1 = "(";
				temp3 = ")";
			}
		}
		return String.valueOf(new StringBuilder().append(temp1).append(temp2).append(temp3).append(temp4));
	}
	
	private boolean isMaterialListComplex(MaterialStack materialStack){
		if (materialStack.mMaterial.materialList.size() > 1) {
			return true;
		}
		if (materialStack.mMaterial.materialList.size() == 0) {
			return false;
		}
		return isMaterialListComplex(materialStack.mMaterial.materialList.get(0));
	}
	
	@Override
	public int hashCode() {
		return mMaterial.hashCode();
	}
}
