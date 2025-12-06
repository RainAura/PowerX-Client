package net.minecraft.client.multiplayer;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public class WeakHashSet<E> extends AbstractSet<E> implements Set<E> {
	private transient WeakHashMap<E, Object> map;
	private static final Object PRESENT = new Object();

	public WeakHashSet() {
		this.map = new WeakHashMap<>();
	}

	public WeakHashSet(final Collection<? extends E> c) {
		this.map = new WeakHashMap<>(Math.max((int) (c.size() / .75f) + 1, 16));
		this.addAll(c);
	}

	public WeakHashSet(final int initialCapacity, final float loadFactor) {
		this.map = new WeakHashMap<>(initialCapacity, loadFactor);
	}

	public WeakHashSet(final int initialCapacity) {
		this.map = new WeakHashMap<>(initialCapacity);
	}

	@Override
	public Iterator<E> iterator() {
		return this.map.keySet().iterator();
	}

	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	@Override
	public boolean contains(final Object o) {
		return this.map.containsKey(o);
	}

	@Override
	public boolean add(final E o) {
		return this.map.put(o, WeakHashSet.PRESENT) == null;
	}

	@Override
	public boolean remove(final Object o) {
		return this.map.remove(o) == WeakHashSet.PRESENT;
	}

	@Override
	public void clear() {
		this.map.clear();
	}
}