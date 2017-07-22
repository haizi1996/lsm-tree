package core;

import lsm.tree.BTree;

public class Sstable<K extends Comparable<K> ,V> {
	
	
	private BTree<K,V> bTree ;

	public Sstable(int m) {
		bTree = new BTree<K,V>(m);
	}
	
	public boolean delete(K key){
		return bTree.delete(key);
	}
	
	public boolean put(K key ,V value){
		return bTree.insert(key, value);
	}

	public BTree<K, V> getbTree() {
		return bTree;
	}
	public int getM(){
		return bTree.getM();
	}
	public V get(K key){
		return bTree.get(key);
	}
}
