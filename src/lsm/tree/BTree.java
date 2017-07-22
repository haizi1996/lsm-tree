package lsm.tree;

import java.util.concurrent.atomic.AtomicInteger;

public class BTree<K extends Comparable<K> ,V> {

	private BTreeNode<K,V> root;

	// 定义B树是几阶，默认是4
	private static int M = 4;
	// 非叶节点的最大关键词个数:M-1
	private static int maxNodeSize;
	// 非叶节点的最大关键词个数:ceil(M)-1
	@SuppressWarnings("unused")
	private final int minNodeSize;

	private AtomicInteger count;
	private int depth;
	private int size;

	public BTree() {
		this(M);
	}
	
	@SuppressWarnings("static-access")
	public BTree(int m2) {
		// TODO Auto-generated constructor stub
		M = m2 > M ? m2 : M;
		this.maxNodeSize = m2 - 1;
		this.minNodeSize = ((m2 + 1) >> 1) - 1;
		this.root = new BTreeNode<K,V>(true,this.maxNodeSize);
	}

	static class SearchResult {
		private boolean result;

		private int index;

		public SearchResult(boolean result, int index) {
			this.result = result;
			this.index = index;
		}

		public boolean isResult() {
			return result;
		}

		public void setResult(boolean result) {
			this.result = result;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

	}

	 

	public boolean insert(K key,V value) {
		if (root.isFull()) {
			BTreeNode<K,V> newRoot = new BTreeNode<K,V>(false,maxNodeSize);
			newRoot.getChildren().add(root);
			splitFullNode(root, newRoot, 0);
			root = newRoot;
			depth++;
		}
		insert(root, key,value);
		count.incrementAndGet();
		return true;
	}
	
	private K insert(BTreeNode<K,V> currentNode, K key,V value) {
		// TODO Auto-generated method stub
		SearchResult sr = currentNode.searchKey(key);
		if (sr.isResult()) {
			K k = currentNode.getEntrys().get(sr.getIndex()).getKey();
			currentNode.getEntrys().remove(sr.getIndex());
			currentNode.getEntrys().add(sr.getIndex(), new Entry<K,V>(key,value));
			return k;
		}
		if (currentNode.isLeaf()) {
			currentNode.getEntrys().add(sr.getIndex(), new Entry<K,V>(key,value));
			size++;
			return key;
		} else {
			BTreeNode<K,V> node = currentNode.getChildren().get(sr.getIndex());
			if (node.isFull()) {
				splitFullNode(node, currentNode, sr.getIndex());
				if (key.compareTo(currentNode.getEntrys().get(sr.getIndex()).getKey()) > 0) {
					// 需要插入到分裂出的兄弟节点中
					node = currentNode.getChildren().get(sr.getIndex() + 1);
				}
			}
			return insert(node, key , value);
		}
	}
	public boolean delete(K key){
		Entry<K,V> e = search(root,key);
		if(e == null){
			return false;
		}
		e.delete();
		return true;
	}
	public V get(K key) {
		Entry<K,V> e = search(root,key);
		return e==null?null:e.getValue();
	}

	private Entry<K,V> search(BTreeNode<K,V> currentNode, K key) {
		// TODO Auto-generated method stub
		SearchResult sr = currentNode.searchKey(key);
		if (sr.isResult()) {
			return currentNode.getEntrys().get(sr.getIndex());
		} else {
			if (currentNode.isLeaf()) {
				return search(currentNode.getChildren().get(sr.getIndex()), key);
			}
			return null;
		}
	}

	private void splitFullNode(BTreeNode<K,V> fullNode, BTreeNode<K,V> parent, int index) {
		// TODO Auto-generated method stub
		if (!fullNode.isFull()) {
			throw new UnsupportedOperationException("不能对非满节点经行分裂!");
		}
		int middleIndex = (fullNode.getChildren().size() - 1) / 2;
		parent.getEntrys().add(0, fullNode.getEntrys().get(middleIndex));
		BTreeNode<K,V> sibling = new BTreeNode<K,V>(fullNode.isLeaf(),maxNodeSize);
		for (int i = fullNode.getChildren().size() - 1; i > middleIndex; i--) {
			sibling.getEntrys().add(0, fullNode.getEntrys().get(i));
			fullNode.getEntrys().remove(i);
		}
		fullNode.getEntrys().remove(middleIndex);
		parent.getChildren().add(index + 1, sibling);

		if (!fullNode.isLeaf()) {
			int cs = (fullNode.getChildren().size()) >> 1;
			for (int i = fullNode.getChildren().size() - 1; i >= cs; ++i) {
				sibling.getChildren().add(0, fullNode.getChildren().get(i));
				sibling.getChildren().remove(i);
			}
		}

	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	public int depth() {
		return depth;
	}
	
	

	public int getM() {
		// TODO Auto-generated method stub
		return M;
	}

	public BTreeNode<K,V> getRoot() {
		return root;
	}

}
