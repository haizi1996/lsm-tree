package lsm.tree;

import java.util.ArrayList;
import java.util.List;

import lsm.tree.BTree.SearchResult;

public class BTreeNode<K extends Comparable<K>, V> {

	private int keyNum;
	private List<Entry<K,V>> entrys;
	private List<BTreeNode<K,V>> children;
	private final int maxNodeSize;
	private boolean isLeaf;
	public BTreeNode(boolean isLeaf ,int maxNodeSize) {
		this.maxNodeSize = maxNodeSize;
		entrys = new ArrayList<Entry<K,V>>();
		children = new ArrayList<BTreeNode<K,V>>();
		this.isLeaf = false;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public int size() {
		return entrys.size();
	}

	public int getKeyNum() {
		return keyNum;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public SearchResult searchKey(K key) {

		int left = 0;
		int right = entrys.size() - 1;
		int mid, cmp;
		while (left <= right) {
			mid = (left + right) >> 1;
			cmp = entrys.get(mid).getKey().compareTo(key);
			if (cmp == 0) {
				return new SearchResult(true, mid);
			} else if (cmp > 0) {
				right = mid - 1;
			} else {
				left = mid + 1;
			}
		}
		return new SearchResult(false, left);
	}

	public boolean isFull() {
		return maxNodeSize == entrys.size();
	}

	public List<BTreeNode<K,V>> getChildren() {
		return children;
	}

	public void setChildren(List<BTreeNode<K,V>> children) {
		this.children = children;
	}


	public void setKeyNum(int keyNum) {
		this.keyNum = keyNum;
	}

	public List<Entry<K,V>> getEntrys() {
		return entrys;
	}

	public void setEntrys(List<Entry<K,V>> entrys) {
		this.entrys = entrys;
	}

}
