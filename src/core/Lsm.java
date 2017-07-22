package core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import filter.BloomFilter;
import lsm.util.Result;
import lsm.util.TreeUtil;
import lsm.util.Vector;
import resource.load.Content;
import resource.load.ResourceLoad;

public class Lsm<K extends Comparable<K>,V>{
	private Vector<K,V> sstable;
	private volatile Map<K,Integer> delLog;
	private volatile Map<K,Integer> delLogBack;
	private Memtable<K,V> memTable;
	private Memtable<K,V> immutableMemtable;
	private BloomFilter<K> bloomFilter ; 
	private static Map<String , String> content;
	private AtomicInteger logSize;
	//默认值
	private final int default_rbTreeSize = 100; 
	private final int default_delLogSize = 200;
	
	private final int default_sstableSize = 10 ;
	private final int default_M = 4;
	private final int default_bloomFilterSize = 5000 << 10000;
	//配置信息
	//B树阶层
	private int M; 
	
	private int rbTreeSize ;

	private int delLogSize;
	
	private int sstableSize;
	
	private int bloomFilterSize;
	
	static{
		content = ResourceLoad.getContent();
	}
	public Lsm() {
		init();
		this.sstable = new Vector<K,V>(sstableSize);
		delLog = new ConcurrentHashMap<K,Integer>();
		bloomFilter = new BloomFilter<>(bloomFilterSize);
		memTable = new Memtable<K,V>();
		logSize = new AtomicInteger(0);
	}
	private void init() {
		// TODO Auto-generated method stub
		String key = content.get(Content.MemtableSize);
		rbTreeSize = (key == null|| key.equals(""))?default_rbTreeSize: Integer.parseInt(key);
		key = content.get(Content.delLogSize);
		delLogSize = (key == null|| key.equals(""))?default_delLogSize: Integer.parseInt(key);
		key = content.get(Content.sstableSize);
		sstableSize = (key == null|| key.equals(""))?default_sstableSize: Integer.parseInt(key);
		key = content.get(Content.bloomFilterSize);
		bloomFilterSize = (key == null|| key.equals(""))?default_bloomFilterSize: Integer.parseInt(key);
		key = content.get(Content.Bcha);
		M = (key == null|| key.equals(""))?default_M: Integer.parseInt(key);
	}
	public Result<K> put(K key,V value){
		boolean res = memTable.add(key);
		if(res){
			bloomFilter.add(key);
			if(memTable.size() == rbTreeSize ){
				immutableMemtable = memTable;
				memTable = new Memtable<K,V>();
				new Thread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						//将红黑树转换成B树
						sstable.add(TreeUtil.RBTreeToBTree(immutableMemtable,M));
						if(sstable.size() == sstable.getArrayLen()){
							new Compaction<K,V>(delLog).compaction(sstable);
						}
					}
				}).start();;
			}
		}
		return new Result<K>(true, key);
	}
	public V get(K key){
		if(delLog.containsKey(key)){
			return null;
		}
		if(bloomFilter.contains(key)){
			if(immutableMemtable != null){
				Result<V> res1 = immutableMemtable.get(key);
				if(res1.isResult()){
					return res1.getMessage();
				}
			}
			return TreeUtil.findKeyFromSStable(key,sstable) ;
		}
		return null;
	} 
	public boolean contain(K key ){
		if(delLog.containsKey(key)){
			return false;
		}
		if(bloomFilter.contains(key)){
			if(immutableMemtable != null){
				boolean res1 = immutableMemtable.get(key)!=null;
				if(res1){
					return res1;
				}
			}
			return TreeUtil.findKeyFromSStable(key,sstable) == null;
		}
		return false;
	}
	
	public boolean remove(K key){
		//无锁操作
		while(logSize.get()==delLogSize);
		
		
		logSize.incrementAndGet();
		
		
		if(delLog.containsKey(key)||(delLogBack!=null&&delLogBack.containsKey(key))){
			return false;
		}
		if(!memTable.remove(key).isResult()){
			delLog.put(key, 1);
			if(delLog.size() == delLogSize){
				delLogBack = delLog;
				delLog = new ConcurrentHashMap<K,Integer>();
				//如此设计？
				logSize.addAndGet(-delLogSize);
				new Thread(new Runnable(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						TreeUtil.delByDelLog(sstable,delLogBack);
					}
				}).start();
			}
		}else{
			logSize.decrementAndGet();
		}
		return true;
	}
	
}
