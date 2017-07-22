package lsm.util;

import java.util.Map;
import java.util.concurrent.Callable;

import core.Sstable;

public class Task<K extends Comparable<K>,V> implements Callable<Sstable<K,V>> {
	private Sstable<K,V> ss ;
	private Sstable<K,V> st ;
	private Vector<K,V> sstable;
	private int left ;
	private final Map<? extends Comparable<?>,Integer> deleted;
	public Task(Vector<K, V> sstable, int left, int right, Map<K, Integer> deleted) {
		// TODO Auto-generated constructor stub
		this.deleted = deleted;
		this.sstable = sstable;
		this.left = left;
		ss = this.sstable.get(left);
		st = this.sstable.get(right);
		
	}
	@Override
	public Sstable<K,V> call() throws Exception {
		this.sstable.put(TreeUtil.memgerTwoBtree(ss,st,deleted), left); 
		return this.sstable.get(left);
	}
	

}
