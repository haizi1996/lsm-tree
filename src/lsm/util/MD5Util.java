package lsm.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	
	private static MessageDigest md;
	static{
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static BigInteger getMD5(Object obj) {
		md.update(obj.toString().getBytes());
		return new BigInteger(1,md.digest());
	}
	
}