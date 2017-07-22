package filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class BloomFilterTest {
	 private int total = 100000; //测试元素的总数


	    private void printStat(long start, long end) {
	        double diff = (end - start) / 1000.0;
	        System.out.println(diff + "s, " + (total / diff) + " 元素/s");
	    }

	    
	    public void test(){


	        BloomFilter<String> bloom = new BloomFilter<String>(10000);
	        List<String> existingElements = new ArrayList<String>();
	        // 添加元素
	        System.out.print("Ruyu Bloom Filter添加元素: ");
	        long start = System.currentTimeMillis();
	        long end = System.currentTimeMillis();
	        printStat(start, end);
	        
	        int size = 10;
	        System.out.print("Google Bloom Filter添加元素: 0-" + size);
	        start = System.currentTimeMillis();
	        for (int i = 1; i <= size; i++) {
	            existingElements.add("元素"+i);
	            bloom.add(existingElements.get(i-1));
	            
	        }
	        end = System.currentTimeMillis();
	        printStat(start, end);

	        //测试已经存在的元素
	        System.out.print("Ruyu Bloom Filter测试已经存在的元素: ");
	        start = System.currentTimeMillis();
	        for (int i = 0; i < total; i++) {
	        	Random r = new Random();
	        	int index = r.nextInt(1000) + 1;
	        	String value = "元素"+index;
	        	System.out.println("测试===>" + value + ": " + (bloom.contains(value) ? "存在的":"不存在的"));
	        }
	        end = System.currentTimeMillis();

	        //测试不存在的元素
	        System.out.print("Ruyu Bloom Filter 测试不存在的元素: ");
	        start = System.currentTimeMillis();
	        for (int i = 0; i < total; i++) {
	        	Random r = new Random();
	        	int index = r.nextInt(1000) + 1 + total;
	        	String value = "元素"+index;
	        	System.out.println("测试===>" + value + ": " + (bloom.contains(value) ? "存在的":"不存在的"));
	        }
	        end = System.currentTimeMillis();
	        printStat(start, end);

	    }

//	    public void test2(){
//
//	        int count = 0;
//
//	        BloomFilter<String> cbf = new BloomFilter<String>(total);
//	        //BloomFilter<String> bf = new BloomFilter<String>(0.01,total);
//
//	        existingElements.forEach(x->{
//	            cbf.add(x);
//	            //bf.add(x);
//	        });
//
//	        for (int i = 0; i < total; i++) {
//	           if(!cbf.contains(existingElements.get(i))){
//	                count++;
//	            };
//	            /*if(!bf.contains(existingElements.get(i))){
//	                count++;
//	            };*/
//	        }
//	        System.out.println(count);
//	    }
	    public static void main(String[] args) {
	    	BloomFilterTest demo = new BloomFilterTest();
	    	demo.test();
	    //	demo.test2();
	    	
		}

}
