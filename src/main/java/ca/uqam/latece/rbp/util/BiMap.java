package ca.uqam.latece.rbp.util;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
/**
 * This data structure implements a Map and combines two reverse maps. The first has keys with 
 * type K and values with type V. The second has keys with type V and values with type K. 
 * Both maps are kept in sync. Use the method inverse() to get the reverse map. Any operation
 * can be performed on any one of the maps (put, remove, etc.). The inverse() operation makes sure
 * we never have more than 2 BiMaps created (ie. someBiMap.inverse().inverse().inverse().inverse() will
 * produce only two BiMaps).   
 * 
 * This data structure is available in some Apache package. This class lets you use a BiMap without
 * downloading the whole Apache thing. This class was implemented with no prior knowledge of the 
 * version provided by Apache (so can't say which one is better). 
 * 
 * @author Anis Boubaker
 *
 * @param <K> Keys type
 * @param <V> Values type
 */
public class BiMap<K extends Object, V extends Object> implements Map<K,V>{

	private Map<K, V> forward = new Hashtable<K, V>();
	private Map<V, K> backward = new Hashtable<V, K>();
	private BiMap<V,K> inverse = null;
	
	public BiMap(){
		this.forward = new Hashtable<K, V>();
		this.backward = new Hashtable<V, K>();
	}
	
	private BiMap(Map<K,V>forward, Map<V,K>backward, BiMap<V,K> inverse){
		this.forward = forward;
		this.backward = backward;
		this.inverse = inverse;
	}
	
	public Map<V,K> inverse(){
		if(this.inverse==null) this.inverse = new BiMap<V,K>(backward, forward, this);
		return inverse;
	}

	@Override
	public V put(K key, V value) {
		backward.put(value, key);
		return forward.put(key, value);
	}

	public int size() {
		return forward.size();
	}

	@Override
	public void clear() {
		forward.clear();
		backward.clear();
	}

	@Override
	public boolean containsKey(Object arg0) {
		return forward.containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object arg0) {
		return forward.containsValue(arg0);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return forward.entrySet();
	}

	@Override
	public V get(Object arg0) {
		return forward.get(arg0);
	}

	@Override
	public boolean isEmpty() {
		return forward.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return forward.keySet();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		forward.putAll(arg0);
		for(java.util.Map.Entry<? extends K, ? extends V> entry : arg0.entrySet()){
			backward.put(entry.getValue(), entry.getKey());
		}
	}

	@Override
	public V remove(Object arg0) {
		V removed = forward.remove(arg0);
		backward.remove(removed);
		return removed;
	}

	@Override
	public Collection<V> values() {
		return forward.values();
	}

}