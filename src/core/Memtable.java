package core;

import lsm.tree.RBTree;
import lsm.util.Result;

public class Memtable<K extends Comparable<K>,V> {
	
	private RBTree<K,V> RBtree ;

	public Memtable() {
		RBtree = new RBTree<K,V>();
	}
	
	public Result<V> get(K key){
		return RBtree.find(key);
	}
	public Result<V> remove(K key){
		return RBtree.remove(key);
	}
	public boolean add(K key){
		return RBtree.addNode(key)== null;
	}
	public int size(){
		return RBtree.size();
	}

	public RBTree<K, V> getRBtree() {
		return RBtree;
	}
	
}
