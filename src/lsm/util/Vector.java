package lsm.util;

import core.Sstable;

public class Vector<K extends Comparable<K> ,V>{
	
	private static final int defaultSize = 10;
	private int arrayLen;
	
	private int size;
	
	private Sstable<K,V>[] sstable;
	
	@SuppressWarnings("unchecked")
	public Vector(int arrayLen){
		this.arrayLen = arrayLen;
		sstable = new Sstable[arrayLen];
	}
	public Vector(){
		this(defaultSize);
	}
	
	public void put(Sstable<K,V> stable , int index){
		if(index >= arrayLen ){
			throw new RuntimeException("数组角标越界!");
		}else if(index < 0){
			throw new RuntimeException("角标从0开始!");
		}
		sstable[index] = stable;
	}
	public void add(Sstable<K,V> stable ){
		if(size >= arrayLen ){
			throw new RuntimeException("数组已满!");
		}
		sstable[size++] = stable;
	}
	public Sstable<K,V> get(int index){
		if(index >= arrayLen ){
			throw new RuntimeException("数组角标越界!");
		}else if(index < 0){
			throw new RuntimeException("角标从0开始!");
		}
		return sstable[index];
	}
	public void setSize(int size){
		this.size = size;
	}
	public int size(){
		return size;
	}
	public int getArrayLen() {
		return arrayLen;
	}
	
	
}
