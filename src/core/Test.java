package core;

import lsm.util.Result;

public class Test {
	
	
	public static void main(String[] args) {
		Lsm<Integer,Integer> lsm = new Lsm<>();
		
		Result<Integer> res = lsm.put(5, 50);
		
		if(res.isResult()){
			System.out.println(res.getMessage());
		}
		
		System.out.println(lsm.get(5));
	}
}
