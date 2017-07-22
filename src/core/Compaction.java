package core;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

import lsm.util.Task;
import lsm.util.Vector;

public class Compaction<K extends Comparable<K>, V> {

	private ExecutorService threadPool;
	@SuppressWarnings("unused")
	private int coreSize, maxSize, minSize;
	private final Map<K, Integer> deleted;

	public Compaction(Map<K, Integer> deleted) {
		coreSize = Runtime.getRuntime().availableProcessors() + 1;
		maxSize = (coreSize - 1) >> 1;
		minSize = coreSize << 1;
		this.deleted = deleted;
		threadPool = new ThreadPoolExecutor(coreSize, maxSize, 0l, TimeUnit.MINUTES,
				new LinkedBlockingQueue<Runnable>(), new AbortPolicy());
	}

	public boolean compaction(Vector<K, V> sstable) {
		if (sstable == null || sstable.size() <= 0) {
			throw new RuntimeException("sstable is empty !");
		}
		int left = 0, right = sstable.getArrayLen();
		while (left < right) {
			threadPool.submit(new Task<K, V>(sstable, left, right, deleted));
		}
		sstable.setSize(left);
		return true;
	}

}
