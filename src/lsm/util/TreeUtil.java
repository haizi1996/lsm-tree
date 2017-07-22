package lsm.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import core.Memtable;
import core.Sstable;
import lsm.tree.BTree;
import lsm.tree.BTreeNode;
import lsm.tree.Entry;
import lsm.tree.RBTreeNode;

public class TreeUtil {

	/**
	 * 将红黑树转换成B树
	 * 
	 * @param rbTree
	 * @param bTree
	 * @return
	 */
	public static <K extends Comparable<K>, V> Sstable<K, V> RBTreeToBTree(Memtable<K, V> memTable, int M) {
		Sstable<K, V> table = new Sstable<K, V>(M);
		rbTreeToBTree(memTable.getRBtree().getRoot(), table.getbTree());
		return table;
	}

	private static <K extends Comparable<K>, V> void rbTreeToBTree(RBTreeNode<K, V> root, BTree<K, V> bTree) {
		// TODO Auto-generated method stub
		if (root != null) {
			bTree.insert(root.getKey(), root.getValue());
			rbTreeToBTree(root.getLeft(), bTree);
			rbTreeToBTree(root.getRight(), bTree);
		}
	}

	/**
	 * 合并两颗B树
	 * 
	 * @param ss
	 * @param st
	 * @param deleted
	 * @return
	 */
	public static <K extends Comparable<K>, V> Sstable<K, V> memgerTwoBtree(Sstable<K, V> ss, Sstable<K, V> st,
			Map<? extends Comparable<?>, Integer> deleted) {
		// TODO Auto-generated method stub
		Sstable<K, V> newBTree = new Sstable<K, V>(st.getM());
		memgerTwoBtree(newBTree, ss.getbTree().getRoot(), deleted);
		memgerTwoBtree(newBTree, st.getbTree().getRoot(), deleted);
		return newBTree;
	}

	private static <K extends Comparable<K>, V> void memgerTwoBtree(Sstable<K, V> newBTree, BTreeNode<K, V> root,
			Map<? extends Comparable<?>, Integer> deleted) {
		// TODO Auto-generated method stub
		if (root != null) {
			List<Entry<K, V>> entrys = root.getEntrys();
			for (Entry<K, V> entry : entrys) {
				if(!deleted.containsKey(entry.getKey())){
					newBTree.put(entry.getKey(), entry.getValue());
				}
			}
			List<BTreeNode<K, V>> children = root.getChildren();
			for (BTreeNode<K, V> child : children) {
				memgerTwoBtree(newBTree, child, deleted);
			}
		}

	}

	/**
	 * 从SStable查找元素
	 * 
	 * @param key
	 * @param sstable
	 * @return
	 */
	public static <K extends Comparable<K>, V> V findKeyFromSStable(K key, Vector<K, V> sstable) {
		// TODO Auto-generated method stub
		if (sstable != null && sstable.size() > 0) {

			for (int i = 0; i < sstable.size(); i++) {
				Sstable<K, V> s = sstable.get(i);
				V res = s.get(key);
				if (res != null) {
					return res;
				}
			}
		}
		return null;
	}

	/**
	 * 将delLog日志刷新到sstable
	 * 
	 * @param sstable
	 * @param delLog
	 */
	public static <K extends Comparable<K>, V> void delByDelLog(Vector<K, V> sstable, Map<K, Integer> delLog) {
		// TODO Auto-generated method stub
		for (int i = 0; i < sstable.size(); i++) {
			Sstable<K, V> stable = sstable.get(i);
			Set<K> keySet = new HashSet<K>(delLog.keySet());
			for (K key : keySet) {
				stable.delete(key);
				// delLog.remove(key);
			}
		}
	}
}
