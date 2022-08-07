package space.accident.api.objects;

import net.minecraft.item.ItemStack;
import space.accident.api.API;
import space.accident.extensions.ItemStackUtils;

import java.util.*;

public class ReloadHashSet<E extends ItemStackData> extends AbstractSet<E> {
	private static final Object OBJECT = new Object();
	private final transient HashMap<ItemStackData, Object> map;
	
	public ReloadHashSet() {
		map = new HashMap<>();
		API.sItemStackMappings.add(map);
	}
	
	public ReloadHashSet(Collection<? extends E> c) {
		map = new HashMap<>(Math.max((int) (c.size() / .75f) + 1, 16));
		addAll(c);
		API.sItemStackMappings.add(map);
	}
	
	public ReloadHashSet(int initialCapacity, float loadFactor) {
		map = new HashMap<>(initialCapacity, loadFactor);
		API.sItemStackMappings.add(map);
	}
	
	public ReloadHashSet(int initialCapacity) {
		map = new HashMap<>(initialCapacity);
		API.sItemStackMappings.add(map);
	}
	
	ReloadHashSet(int initialCapacity, float loadFactor, boolean dummy) {
		map = new LinkedHashMap<>(initialCapacity, loadFactor);
		API.sItemStackMappings.add(map);
	}
	
	public HashMap getMap() {
		return map;
	}
	
	@Override
	public Iterator<E> iterator() {
		return (Iterator<E>) map.keySet().iterator();
	}
	
	@Override
	public int size() {
		return map.size();
	}
	
	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}
	
	public boolean add(ItemStack stack) {
		if (ItemStackUtils.isStackInvalid(stack)) return false;
		return map.put(new ItemStackData(stack), OBJECT) == null;
	}
	
	@Override
	public boolean add(E e) {
		return map.put(e, OBJECT) == null;
	}
	
	@Override
	public boolean remove(Object o) {
		return map.remove(o) == OBJECT;
	}
	
	@Override
	public void clear() {
		map.clear();
	}
}

