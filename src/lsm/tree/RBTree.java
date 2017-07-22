package lsm.tree;

import java.util.concurrent.atomic.AtomicInteger;

import lsm.util.Result;


public class RBTree<K extends Comparable<K>,V> {
	private final RBTreeNode<K,V> root;
	
	private volatile AtomicInteger size = new AtomicInteger(0);
	
	private volatile boolean overrideMode = true;
	public RBTree(){
		this.root = new RBTreeNode<K,V>();
	}
	public RBTree(boolean overrideMode) {
		this();
		this.overrideMode = overrideMode;
	}
	public boolean isOverrideMode() {
		return overrideMode;
	}
	public void setOverrideMode(boolean overrideMode) {
		this.overrideMode = overrideMode;
	}
	public RBTreeNode<K,V> getRoot() {
		return root.getLeft();
	}
	public int size(){
		return size.get(); 
	}
	
	@SuppressWarnings("unchecked")
	public Result<K> addNode(K key){
		return addNode(key,(V)key);
	} 
	
	public Result<K> addNode(K key,V value){
		RBTreeNode<K,V> t = new RBTreeNode<K,V>(key,value);
		return addNode(t);
	}
	
	private Result<K> addNode(RBTreeNode<K,V> node) {
		// TODO Auto-generated method stub
		node.setLeft(null);
        node.setRight(null);
        node.setRed(true);
        setParent(node,null);
		if(root.getLeft() == null){
			root.setLeft(node);
			node.setRed(false);
			size.incrementAndGet();
		}else{
			RBTreeNode<K,V> x = findParentNode(node);
			int cmp = x.getKey().compareTo(node.getKey());
			if(this.overrideMode && cmp == 0 ){
				K k = x.getKey();
				x.setValue(node.getValue());
				return new Result<K>(true,k);
			}else if(cmp == 0){
				return new Result<K>(true,x.getKey());
			}
			setParent(node, x);
			if(cmp > 0){
				x.setLeft(node);
			}else{
				x.setRight(node);
			}
			/*
			 * 父节点为红色，违背了父子节点不能同时为红色，此时要进行插入修复
			 * (那么祖父节点为黑色,叔父节点要么为空，要么不存在，肯定不会为黑色：
			 * 原因(如果为红色，那么从祖父节点到父节点和到叔父节点就出现了不一样的黑节点个数))
			 * 此时有三种情况		
			 * 		1:叔父节点为红色
			 * 		2:叔父节点为空，并且祖父节点，父节点，新节点在同一条斜线上
			 * 		3:叔父节点为空，并且祖父节点，父节点，新节点不在同一条斜线上
			 */
			fixInsert(node);
			size.getAndIncrement();
		}
		return null;
	}
	private void fixInsert(RBTreeNode<K,V> node) {
		// TODO Auto-generated method stub
		RBTreeNode<K,V> parent = node.getParent();
		while(parent != null && parent.isRed()){
			RBTreeNode<K,V> uncle = getUncle(node);
			if(uncle == null){
				RBTreeNode<K,V> ancestor = parent.getParent();
                //ancestor is not null due to before before add,tree color is balance
                if(parent == ancestor.getLeft()){
                    boolean isRight = node == parent.getRight();
                    if(isRight){
                        rotateLeft(parent);
                    }
                    rotateRight(ancestor);

                    if(isRight){
                        node.setRed(false);
                        parent=null;//end loop
                    }else{
                        parent.setRed(false);
                    }
                    ancestor.setRed(true);
                }else{
                    boolean isLeft = node == parent.getLeft();
                    if(isLeft){
                        rotateRight(parent);
                    }
                    rotateLeft(ancestor);

                    if(isLeft){
                        node.setRed(false);
                        parent=null;//end loop
                    }else{
                        parent.setRed(false);
                    }
                    ancestor.setRed(true);
                }
			}else{
				parent.setRed(false);
                uncle.setRed(false);
                parent.getParent().setRed(true);
                node=parent.getParent();
                parent = node.getParent();
			}
		}
		getRoot().makeBlack();
		getRoot().setParent(null);
	}
	private void rotateRight(RBTreeNode<K,V> node){
        RBTreeNode<K,V> left = node.getLeft();
        if(left==null){
            throw new java.lang.IllegalStateException("left node is null");
        }
        RBTreeNode<K,V> parent = node.getParent();
        node.setLeft(left.getRight());
        setParent(left.getRight(),node);

        left.setRight(node);
        setParent(node,left);

        if(parent==null){
            root.setLeft(left);
            setParent(left,null);
        }else{
            if(parent.getLeft()==node){
                parent.setLeft(left);
            }else{
                parent.setRight(left);
            }
            setParent(left,parent);
        }
    }
	private void rotateLeft(RBTreeNode<K,V> node){
        RBTreeNode<K,V> right = node.getRight();
        if(right==null){
            throw new java.lang.IllegalStateException("right node is null");
        }
        RBTreeNode<K,V> parent = node.getParent();
        node.setRight(right.getLeft());
        setParent(right.getLeft(),node);
        right.setLeft(node);
        setParent(node,right);

        if(parent==null){//node pointer to root
            //right  raise to root node
            root.setLeft(right);
            setParent(right,null);
        }else{
            if(parent.getLeft()==node){
                parent.setLeft(right);
            }else{
                parent.setRight(right);
            }
            //right.setParent(parent);
            setParent(right,parent);
        }
    }
	private RBTreeNode<K,V> getUncle(RBTreeNode<K,V> node) {
		// TODO Auto-generated method stub
		RBTreeNode<K,V> parent = node.getParent();
		RBTreeNode<K,V> gradeParent = parent.getParent();
		if(gradeParent != null){
			if(gradeParent.getLeft() == parent){
				return gradeParent.getRight();
			}
			return gradeParent.getLeft();
		}
		return null;
	}
	private RBTreeNode<K,V> findParentNode(RBTreeNode<K,V> node) {
		// TODO Auto-generated method stub
		RBTreeNode<K,V> dataRoot = getRoot();
		RBTreeNode<K,V> child = dataRoot;
		while(child != null){
			
			int cmp = child.getKey().compareTo(node.getKey());
			if(cmp < 0){
				dataRoot = child;
				child = child.getRight();
			}else if(cmp > 0){
				dataRoot = child;
				child = child.getLeft();
			}else{
				return child;
			}
		}		
		return dataRoot;
	}
	private void setParent(RBTreeNode<K,V> node, RBTreeNode<K,V> parent) {
		// TODO Auto-generated method stub
		if(node != null){
			node.setParent(parent);
			if(parent == root){
				node.setParent(null);
			}
		}
	}
	public Result<V> find(K key){
		RBTreeNode<K,V> dataroot = getRoot();
		while(dataroot != null){
			int cmp = dataroot.getKey().compareTo(key);
			if(cmp > 0){
				dataroot = dataroot.getLeft();
			}else if(cmp < 0){
				dataroot = dataroot.getRight();
			}else{
				return new Result<V>(true, dataroot.getValue());
			}
		}
		return new Result<V>(false,null );
	}
	
	public Result<V> remove(K key){
		RBTreeNode<K,V> dataRoot = getRoot();
        RBTreeNode<K,V> parent = root;
        while(dataRoot != null){
        	int cmp = dataRoot.getKey().compareTo(key);
        	if(cmp > 0){
        		parent = dataRoot;
        		dataRoot = dataRoot.getLeft();
        	}else if(cmp < 0){
        		parent = dataRoot;
        		dataRoot = dataRoot.getRight();
        	}else{
        		if(dataRoot.getRight() != null ){
        			RBTreeNode<K,V> min = removeMin(dataRoot.getLeft());
        			RBTreeNode<K,V> x = min.getLeft() != null ? min.getParent() : min.getRight(); 
        			boolean isParent = min.getRight()==null;
        			
        			min.setLeft(dataRoot.getLeft());
        			setParent(dataRoot.getLeft(),min);
        			if(parent.getLeft()==dataRoot){
                        parent.setLeft(min);
                    }else{
                        parent.setRight(min);
                    }
        			setParent(min,parent);
        			boolean curMinIsBlack = min.isBlack();
        			min.setRed(dataRoot.isRed());
        			if(min != dataRoot.getRight()){
        				min.setRight(dataRoot.getRight());
        				setParent(min.getRight(), min);
        			}
        			if(curMinIsBlack){
        				if(min!=dataRoot.getRight()){
                            fixRemove(x,isParent);
                        }else if(min.getRight()!=null){
                            fixRemove(min.getRight(),false);
                        }else{
                            fixRemove(min,true);
                        }
        			}
        		}
        		return new Result<V>(true,null);
        	}
        }
        return new Result<V>(true,null);
	}
	
	private void fixRemove(RBTreeNode<K,V> node, boolean isParent) {
		// TODO Auto-generated method stub
		RBTreeNode<K,V> cur = isParent ? null : node;
        boolean isRed = isParent ? false : node.isRed();
        RBTreeNode<K,V> parent = isParent ? node : node.getParent();

        while(!isRed && !isRoot(cur)){
            RBTreeNode<K,V> sibling = getSibling(cur,parent);
            //sibling is not null,due to before remove tree color is balance

            //if cur is a left node
            boolean isLeft = parent.getRight()==sibling;
            if(sibling.isRed() && !isLeft){//case 1
                //cur in right
                parent.makeRed();
                sibling.makeBlack();
                rotateRight(parent);
            }else if(sibling.isRed() && isLeft){
                //cur in left
                parent.makeRed();
                sibling.makeBlack();
                rotateLeft(parent);
            }else if(isBlack(sibling.getLeft()) && isBlack(sibling.getRight())){//case 2
                sibling.makeRed();
                cur = parent;
                isRed = cur.isRed();
                parent=parent.getParent();
            }else if(isLeft && !isBlack(sibling.getLeft()) 
                                    && isBlack(sibling.getRight())){//case 3
                sibling.makeRed();
                sibling.getLeft().makeBlack();
                rotateRight(sibling);
            }else if(!isLeft && !isBlack(sibling.getRight()) 
                                            && isBlack(sibling.getLeft()) ){
                sibling.makeRed();
                sibling.getRight().makeBlack();
                rotateLeft(sibling);
            }else if(isLeft && !isBlack(sibling.getRight())){//case 4
                sibling.setRed(parent.isRed());
                parent.makeBlack();
                sibling.getRight().makeBlack();
                rotateLeft(parent);
                cur=getRoot();
            }else if(!isLeft && !isBlack(sibling.getLeft())){
                sibling.setRed(parent.isRed());
                parent.makeBlack();
                sibling.getLeft().makeBlack();
                rotateRight(parent);
                cur=getRoot();
            }
        }
        if(isRed){
            cur.makeBlack();
        }
        if(getRoot()!=null){
            getRoot().setRed(false);
            getRoot().setParent(null);
        }
	}
	private RBTreeNode<K, V> getSibling(RBTreeNode<K, V> node, RBTreeNode<K, V> parent) {
		// TODO Auto-generated method stub
		parent = node==null ? parent : node.getParent();
        if(node==null){
            return parent.getLeft()==null ? parent.getRight() : parent.getLeft();
        }
        if(node==parent.getLeft()){
            return parent.getRight();
        }else{
            return parent.getLeft();
        }
	}
	private boolean isBlack(RBTreeNode<K, V> left) {
		// TODO Auto-generated method stub
		return left.isBlack();
	}
	private boolean isRoot(RBTreeNode<K, V> cur) {
		// TODO Auto-generated method stub
		return root.getLeft() == cur;
	}
	private RBTreeNode<K,V> removeMin(RBTreeNode<K,V> node) {
		// TODO Auto-generated method stub
		RBTreeNode<K,V> parent = node;
		while(node != null && node.getLeft() != null){
			parent = node;
			node = node.getLeft();
		}
		if(node == parent){
			return node;
		}
		parent.setLeft(node.getRight());
		setParent(node.getRight(), parent);
		return node;
	}
	public void printTree(RBTreeNode<K,V> root){
        java.util.LinkedList<RBTreeNode<K,V>> queue =new java.util.LinkedList<RBTreeNode<K,V>>();
        java.util.LinkedList<RBTreeNode<K,V>> queue2 =new java.util.LinkedList<RBTreeNode<K,V>>();
        if(root==null){
            return ;
        }
        queue.add(root);
        boolean firstQueue = true;

        while(!queue.isEmpty() || !queue2.isEmpty()){
            java.util.LinkedList<RBTreeNode<K,V>> q = firstQueue ? queue : queue2;
            RBTreeNode<K,V> n = q.poll();

            if(n!=null){
                String pos = n.getParent()==null ? "" : ( n == n.getParent().getLeft() 
                                                                        ? " LE" : " RI");
                String pstr = n.getParent()==null ? "" : n.getParent().toString();
                String cstr = n.isRed()?"R":"B";
                cstr = n.getParent()==null ? cstr : cstr+" ";
                System.out.print(n+"("+(cstr)+pstr+(pos)+")"+"\t");
                if(n.getLeft()!=null){
                    (firstQueue ? queue2 : queue).add(n.getLeft());
                }
                if(n.getRight()!=null){
                    (firstQueue ? queue2 : queue).add(n.getRight());
                }
            }else{
                System.out.println();
                firstQueue = !firstQueue;
            }
        }
    }
}
