package space.accident.api.recipe;

import space.accident.api.objects.ItemStackData;

import java.util.HashSet;

public interface IRecipeFurnaceRemove {
	void remove(HashSet<ItemStackData> outputsFurnace);
}
