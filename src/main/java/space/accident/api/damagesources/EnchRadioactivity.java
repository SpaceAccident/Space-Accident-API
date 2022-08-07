package space.accident.api.damagesources;


import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import space.accident.api.util.LanguageManager;
import space.accident.api.util.Utility;

public class EnchRadioactivity extends EnchantmentDamage {
	public static EnchRadioactivity INSTANCE;
	public static int ID_EFFECT = 14;
	
	public static void register() {
		new EnchRadioactivity();
	}
	
	public EnchRadioactivity() {
		super(ID_EFFECT, 0, -1);
		LanguageManager.addStringLocalization(getName(), "Radioactivity");
		INSTANCE = this;
	}
	
	@Override
	public int getMinEnchantability(int aLevel) {
		return Integer.MAX_VALUE;
	}
	
	@Override
	public int getMaxEnchantability(int aLevel) {
		return 0;
	}
	
	@Override
	public int getMaxLevel() {
		return 5;
	}
	
	@Override
	public boolean canApply(ItemStack itemStack) {
		return false;
	}
	
	@Override
	public boolean isAllowedOnBooks() {
		return false;
	}
	
	@Override
	public void func_151367_b(EntityLivingBase aHurtEntity, Entity aDamagingEntity, int aLevel) {
		Utility.applyRadioactivity(aHurtEntity, aLevel, 1);
	}
	
	@Override
	public String getName() {
		return "enchantment.damage.radioactivity";
	}
}