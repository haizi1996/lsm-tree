package lsm.tree;

public class Entry<K extends Comparable<K>, V> {
	private K key;
	private V value;
	private boolean isDel;

	public Entry(K key, V value) {
		this.key = key;
		this.value = value;
		this.isDel = false;
	}

	public void delete() {
		// TODO Auto-generated method stub
		setDel(true);
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public boolean isDel() {
		return isDel;
	}

	public void setDel(boolean isDel) {
		this.isDel = isDel;
	}

	@Override
	public String toString() {
		return "Entry [key=" + key + ", value=" + value + "]";
	}
}
