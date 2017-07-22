package filter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

public class BloomFilter<T> {
	
//    protected BitSet bitset;
//    protected int m; //Bloom Filter的位数
//    protected double c; //每个元素的位数
//    protected int n_max;  //Bloom Filter最大的元素个数
//    protected int n; // Bloom Filter实际元素的个数
//    protected int k; // hash函数的个数
	
	private static int defaultbits = 5000 << 10000; //Bloom Filter的位数
	private int bits ;
	private BitSet bitset;
	private long n; //每个元素的位数
	private int size; // Bloom Filter实际元素的个数
	private int k = 8; // hash函数的个数
	private int count[];
	//在大多数情况下MD5准确率较好，
	public static final String hashName = "MD5";
    public static final MessageDigest digestFunction;
    static {
        MessageDigest tmp;
        try {
            tmp = MessageDigest.getInstance(hashName);
        } catch (NoSuchAlgorithmException e) {
            tmp = null;
        }
        digestFunction = tmp;
    }
	public BloomFilter(){ 
		this(defaultbits);
    } 
	public BloomFilter(int bits){
		if(bits < 0){
			throw new RuntimeException("bits is should more than zero !!!");
		}
		this.bits = bits;
		Init();
    } 
	private void Init() {
		// TODO Auto-generated method stub
		bitset = new BitSet(bits);
		count = new int[bits];
	}
	public boolean add(T value){
		return add(value.toString().getBytes());
	}
	private boolean add(byte[] bytes) {
		// TODO Auto-generated method stub
		int[] hashes = createHashes(bytes, k);
        for (int hash : hashes) {
        	int hashcode = Math.abs(hash % defaultbits);
            bitset.set(hashcode, true); //使用K个Hash函数映射到1位
//            if(count[hashcode]==0){
//            	size++;
//            }
//            count[hashcode] ++;
        }
        n++;
        return true;
	}
	private int[] createHashes(byte[] data, int k2) {
		// TODO Auto-generated method stub
		int[] result = new int[k2];
        byte salt = 0;
        int k1 = 0;
        while(k1 < k2){
        	byte[] digest;
            synchronized (digestFunction) {
                digestFunction.update(salt);
                salt++;
                digest = digestFunction.digest(data);                
            }
            for (int i = 0; i < digest.length/4 && k1 < k2; i++) {
                int h = 0;
                for (int j = (i*4); j < (i*4)+4; j++) {
                    h <<= 8;
                    h |= ((int) digest[j]) & 0xFF;
                }
                result[k1] = h;
                k1++;
            }
        }
        return result;
	}
	public boolean contains(T value){
		return contains(value.toString().getBytes());
	}
	private boolean contains(byte[] bytes) {
		// TODO Auto-generated method stub
		int[] hashes = createHashes(bytes, k);
		for (int hash : hashes) {
			int hashcode = Math.abs(hash % defaultbits);
			if(!bitset.get(hashcode)){
				return false;
			}
		}
		return true;
	}
	public void remove(T value){
		remove(value.toString().getBytes());
	}
	public void remove(byte[] bytes){
        int[] hashes = createHashes(bytes, k);
        for (int hash : hashes) {
        	int hashcode = Math.abs(hash % defaultbits);
            if(--count[hashcode] == 0){ //如果数据为空，则将标志位也归位
                size --;
                bitset.clear(hashcode);
            }
        }
        n--;
    }
	public void newInstance(){
		Init();
	}
	
	public int size(){
		return size;
	}
	public void clear() {
        bitset.clear();
        n = 0;
    }
}
