package lsm.tree;

import java.math.BigInteger;

import lsm.util.MD5Util;

public class Test {
	public static void main(String[] args) {
        RBTree<BigInteger,String> bst = new RBTree<BigInteger,String>();
        bst.addNode(MD5Util.getMD5("d"),"d");
        bst.addNode(MD5Util.getMD5("c"),"c");
        bst.addNode(MD5Util.getMD5("h"),"h");
        bst.addNode(MD5Util.getMD5("d"),"d");
        bst.addNode(MD5Util.getMD5("d"),"d");
        bst.addNode(MD5Util.getMD5("d"),"d");
        bst.addNode(MD5Util.getMD5("d"),"d");
        bst.addNode(MD5Util.getMD5("d"),"d");
        bst.addNode(MD5Util.getMD5("d"),"d");
        bst.addNode(MD5Util.getMD5("d"),"d");
        bst.addNode(MD5Util.getMD5("d"),"d");

      //  Map map = new HashMap();
        
        bst.printTree(bst.getRoot());
    }

        
}
