package lsm.tree;


public class RBTreeNode <K extends Comparable<K>,V>{
	private K Key;
	private V value;
	private RBTreeNode<K,V> left;
	private RBTreeNode<K,V> right;
	private RBTreeNode<K,V> parent;
	private boolean red;
	private boolean isRemove;
	
	
	
	
	public RBTreeNode() {
		
	}


	public RBTreeNode(K Key , V value) {
		this.Key = Key;
		this.value = value;
		this.isRemove = false;
		this.red = true;
		
	}
	public K getKey() {
		return Key;
	}
	
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}
	public RBTreeNode<K,V> getLeft() {
		return left;
	}
	public void setLeft(RBTreeNode<K,V> left) {
		this.left = left;
	}
	public RBTreeNode<K,V> getRight() {
		return right;
	}
	public void setRight(RBTreeNode<K,V> right) {
		this.right = right;
	}
	public RBTreeNode<K,V> getParent() {
		return parent;
	}
	public void setParent(RBTreeNode<K,V> parent) {
		this.parent = parent;
	}
	public void setRed(boolean red) {
		this.red = red;
	}
	public boolean isRemove() {
		return isRemove;
	}
	public void setRemove(boolean isRemove) {
		this.isRemove = isRemove;
	}
	public boolean isRed(){
		return red;
	} 
	public boolean isBlack(){
		return !red;
	}
	public boolean isLeaf(){
		return this.left==null&&this.right==null;
	}
	public void makeRed(){
		this.red = true;
	}
	public void makeBlack(){
		this.red = false;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.value.toString();
	}
}
