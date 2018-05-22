package cn.scu.edu.tree;


/**
 * 主要实现了红黑树的插入和删除
 * @author luo hui
 *
 * @param <T>
 */
public class RBTree<T extends Comparable<T>> {
	
	private RBNode<T> root; //根节点
	
	private static final boolean RED = false; //定义红色节点
	
	private static final boolean BLACK = true; //定义黑色节点
	
	class RBNode<T extends Comparable<T>>{
		T key;          //树节点存储的数据		
		RBNode<T> left; //树节点的左子节点		
		RBNode<T> right; //树的右子节点		
		RBNode<T> parent; //树的父亲节点		
		boolean color; //树节点的颜色
		
		public RBNode(T key, boolean color, RBNode<T> left,RBNode<T> right,RBNode<T> parent){
			this.key = key;
			this.color = color;
			this.left = left;
			this.right = right;
			this.parent = parent;
		}
		
		public T getKey(){
			return key;
		}
		
		@Override
		public String toString() {			
			return  "" + key + (color == RED ? "R" : "B");
		}
		
	}
	
	public RBNode<T> parentOf(RBNode<T> node){ //获取node节点的父亲节点
		return node != null ? node.parent : null;
	}
	
	public void setParent(RBNode<T> node, RBNode<T> parent){//设置node的父亲节点
		if(node != null){
			node.parent = parent;
		}
	}
	
	public RBNode<T> leftOf(RBNode<T> node){
		return node != null ? node.left : null;
	}
	
	public RBNode<T> rightOf(RBNode<T> node){
		return node != null ? node.right : null;
	}
	
	public boolean colorOf(RBNode<T> node){ //获取node节点的颜色
		return node != null ? node.color : BLACK;
	}
	
	public void setColor(RBNode<T> node, boolean color){ //设置node节点的颜色
		if(node != null){
			node.color = color;
		}
	}
	
	public boolean isBlack(RBNode<T> node){ //判断node节点的颜色是否为黑色
		return node.color == BLACK;
	}
	
	public boolean isRed(RBNode<T> node){ //判断node节点的颜色是否为红色
		return node.color == RED;
	}
	
	//将x节点左旋
	private void leftRotate(RBNode<T> x){ 
		//得到x的右子节点
		RBNode<T> y = x.right;
		//将y的左孩子节点设置为x的右孩子节点
		x.right = y.left;
		//设置y节点的父亲节点
		if(y.left != null){
			y.left.parent = x;			
		}
		//将y的父亲节点设置为x的父亲节点
		y.parent = x.parent;
		//y是根节点 将x设置为根节点
		if(x.parent == null){
			root = y;
		}else{
			if(x == x.parent.left){
				x.parent.left = y;
			}else{
				x.parent.right = y;
			}
		}
		
		//将x设置为y的左孩子节点
		y.left = x;
		//将x的父亲节点设置为y
		x.parent = y;		
	}
	
	//将x节点右旋基本和左旋的做法相同
    private void rightRotate(RBNode<T> x){ 
    	RBNode<T> y = x.left;
    	x.left = y.right;
    	if(y.right != null){
    		y.right.parent = x;
    	}
    	y.parent = x.parent;
    	if(x.parent == null){
    		root = y;
    	}else{
    		if(x == x.parent.left){
    			x.parent.left = y;
    		}else{
    			x.parent.right = y;
    		}
    	}
    	y.right = x;
    	x.parent = y;    	
	}
    
    //插入key
    public void insert(T key){
    	RBNode<T>  node = new RBNode<T>(key, RED, null, null, null);
    	//用于保存node应该插入位置的父亲节点
    	RBNode<T> current = null;
    	//不断的搜索
    	RBNode<T> p = root;
    	//找到node需要插入的位置
    	while(p != null){
    		current = p;
    		if(p.key.compareTo(key) > 0){
    			p = p.left;
    		}else{
    			p = p.right;
    		}
    	}
    	//插入node节点，如果current为空说明当前树是空树，直接插入
    	node.parent = current;
    	if(current != null){
    		if(current.key.compareTo(key) > 0){
        		current.left = node;
        	}else{
        		current.right = node;
        	}
    	}else{
    		root = node;
    	}
    	insertFixUp(node);    	   	
    }
    
    
    //插入key后进行颜色调整和旋转操作
    /**
	 * 共存在6种情况，其中有4种情况是两两相同的，相当只有4种情况
	 * 1.父亲节点是黑色 
	 *   直接插入当前节点
	 * 2.父亲节点是红色 叔叔节点也是红色 
	 *   将父亲节点和叔叔节点变成黑色，将祖父节点变成红色
	 * 3.父亲节点是红色 叔叔节点是黑色
	 *    3.1 当父亲节点出现在祖父节点的左支，且node是父亲节点的右支
	 *        以父亲节点进行左旋操作
	 *    3.2当父亲节点出现在祖父节点的左支，且node是父亲节点的左支
	 *        将父亲节点变为黑色，祖父节点变为红色，以祖父节点进行右旋操作
	 *    3.3当父亲节点出现在祖父节点的右支，且node是父亲节点的左支（与3.1相同）
	 *        以父亲节点进行右旋操作
	 *    3.4当父亲节点出现在祖父节点的右支，且node是父亲节点的右支（与3.2相同）
	 *        将父亲节点变为黑色，祖父节点变为红色，以祖父节点进行左旋操作
	 * 需要执行下面流程：
	 *    2 -> 3.1 -> 3.2(或者 2 -> 3.3 -> 3.4)
	 * 需要做最后一步操作将根节点变为黑色
	 */
    private void insertFixUp(RBNode<T> node) { 
    	RBNode<T> parent; //node的父亲节点
    	RBNode<T> gparent;//node的祖父节点
    	//当父亲节点为红色    	
    	while((parent = parentOf(node)) != null && isRed(parent)){
    		//当父亲节点和叔叔节点都为红色
    		gparent = parentOf(parent);
    		//如果父亲节点是祖父节点的左支
    		if(parent == gparent.left){
    			RBNode<T> uncle = gparent.right;
    			//符合情况2
    			if(uncle != null && isRed(uncle)){
    				setColor(parent, BLACK);
    				setColor(uncle, BLACK);
    				setColor(gparent, RED);
    				node = gparent;
    				continue;
    			}
    			//符合情况3.1
    			if(node == parent.right){
    				leftRotate(parent);
    				//将node和parent调换位置
    				RBNode<T> temp = parent;
    				parent = node;
    				node = temp;
    			}
    			//符合情况3.2
    			setColor(parent, BLACK);
    			setColor(gparent, RED);
    			rightRotate(gparent);
    		}else{
    			RBNode<T> uncle = gparent.left;
    			//符合情况2
    			if(uncle != null && isRed(uncle)){
    				setColor(parent, BLACK);
    				setColor(uncle, BLACK);
    				setColor(gparent, RED);
    				node = gparent;
    				continue;
    			}
    			//符合情况3.3
    			if(node == parent.left){
    				rightRotate(parent);
    				//将node和parent调换位置
    				RBNode<T> temp = parent;
    				parent = node;
    				node = temp;
    			}
    			//符合情况3.4
    			setColor(parent, BLACK);
    			setColor(gparent, RED);
    			leftRotate(gparent);
    		}
    	}
    	setColor(root, BLACK);    	
    }
    
    public void remove(T key){
    	RBNode<T> node = search(key);
    	if(node != null){
    		remove(node);
    	}
    }
    
    //删除节点：与排序二叉树的删除相似，多了一个调整的过程
    /**
     * 
     * 共分为4种情况：
     * 1.node的左右子树都不为空
     * 2.node的左子树不为空
     * 3.node的右子树不为空
     * 4.node的左右子树都为空
     */
    private void remove(RBNode<T> node){
    	//node的左右子树均不为空
    	if(node.left != null && node.right != null){
    		//找到右子树的最左节点
    		RBNode<T> p = node.right;
    		while(p.left != null){
    			p = p.left;
    		}
    		node.key = p.key;
    		node = p;
    	}
    	//node的左右子树只有一个为空
		RBNode<T> replaceNode = node.left != null ? node.left : node.right;
		if(replaceNode != null){
			replaceNode.parent = node.parent;
			//要删除的node节点是根节点
			if(node.parent == null){
				root = replaceNode;
			}else if(node == node.parent.left){
				 node.parent.left = replaceNode;
			}else{
				node.parent.right = replaceNode;
			}
			//释放node节点
			node.left = node.right = node.parent = null;
			//如果删除的node节点是黑色节点，需要进行调整
			if(node.color == BLACK){
				deleteFixUp(replaceNode);
			}
		}else if(node.parent == null){ //删除的节点是根节点，此时只剩下一个根节点
			root = null;
		}else{ //node的左右子树均为空
			if(node.color == BLACK){
				deleteFixUp(node);
			}
			if(node.parent != null){
				if(node == node.parent.left){
					node.parent.left = null;
				}else if(node == node.parent.right){
					node.parent.right = null;
				}
				node.parent = null;
			}
		}
		
    }
    
    /**
     * 
     *调整节点颜色与旋转总共分为8种情况，其中4种情况两两相同
     *1.当node节点出现在父亲节点的左支
     *  1.1 兄弟为红色节点
     *      将兄弟变为黑色，父亲节点变为红色，以父亲节点进行左旋操作
     *  1.2 兄弟节点为黑色
     *      1.2.1 兄弟节点的两个子节点都为黑色
     *      	直接将兄弟节点变为红色
     *      1.2.2 兄弟节点的左子节点为红色，右子节点为黑色
     *      	兄弟节点变为红色，左子节点为黑色，以兄弟节点进行右旋操作
     *      1.2.3 兄弟节点的右子节点为红色，左子节点颜色任意
     *      	兄弟节点变为父亲节点的颜色，将父亲节点变为黑色，兄弟节点的右子节点变为黑色，以父亲节点进行左旋操作
     *2.当node节点出现在父亲节点的右支
     *  2.1 兄弟为红色节点
     *  	将兄弟变为黑色，父亲节点变为红色，以父亲节点进行右旋操作
     *  2.2 兄弟节点为黑色
     *  	 2.2.1 兄弟节点的两个子节点都为黑色
     *  		直接将兄弟节点变为红色
     *  	 2.2.2 兄弟节点的右子节点为红色，左子节点为黑色
     *  		兄弟节点变为红色，右子节点为黑色，以兄弟节点进行左旋操作
     *  	 2.2.3 兄弟节点的左子节点为红色，右子节点颜色任意
     *  		兄弟节点变为父亲节点的颜色，将父亲节点变为黑色，兄弟节点 的左子节点变为黑色，以父亲节点进行右旋操作
     */
    private void deleteFixUp(RBNode<T> node){
    	while(node != root && colorOf(node)== BLACK){
    		//当node节点出现在父亲节点的左支
    		if(node == leftOf(parentOf(node))){
    			RBNode<T> sib = rightOf(parentOf(node));
    			//兄弟节点为红 符合情况1.1
    			if(colorOf(sib) == RED){
    				setColor(sib, BLACK);
    				setColor(parentOf(node), RED);
    				leftRotate(parentOf(node));
    				sib = rightOf(parentOf(node));
    			}    			
    			
    			//兄弟节点的两个子节点都为黑色 符合情况1.2.1
    			if(colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK){
    				setColor(sib, RED);
    				node = parentOf(node);
    			}else{
    				//兄弟节点的左子节点为红色，右子节点为黑色 符合情况1.2.2
    				if(colorOf(rightOf(sib)) == BLACK){
    					setColor(leftOf(sib), BLACK);
    					setColor(sib, RED);
    					rightRotate(sib);
    					sib = rightOf(parentOf(node));
    				}
    				//兄弟节点的右子节点为红色，左子节点颜色任意 符合情况1.2.3
    				setColor(sib, colorOf(parentOf(node)));
    				setColor(parentOf(node), BLACK);
    				setColor(rightOf(sib), BLACK);
    				leftRotate(parentOf(node));
    				node = root;    				
    			}
    		}else{//当node节点出现在父亲节点的右支
    			RBNode<T> sib = leftOf(parentOf(node));
    			//兄弟节点为红 符合情况2.1
    			if(colorOf(sib) == RED){
    				setColor(sib, BLACK);
    				setColor(parentOf(node), RED);
    				rightRotate(parentOf(node));
    				sib = leftOf(parentOf(node));
    			}    			
    			
    			//兄弟节点的两个子节点都为黑色 符合情况2.2.1
    			if(colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK){
    				setColor(sib, RED);
    				node = parentOf(node);
    			}else{
    				//兄弟节点的右子节点为红色，左子节点为黑色 符合情况2.2.2
    				if(colorOf(rightOf(sib)) == BLACK){
    					setColor(rightOf(sib), BLACK);
    					setColor(sib, RED);
    					leftRotate(sib);
    					sib = leftOf(parentOf(node));
    				}
    				//兄弟节点的右子节点为红色，左子节点颜色任意 符合情况1.2.3
    				setColor(sib, colorOf(parentOf(node)));
    				setColor(parentOf(node), BLACK);
    				setColor(leftOf(sib), BLACK);
    				rightRotate(parentOf(node));
    				node = root;    				
    			}
    		}
    	}
    	setColor(node, BLACK);
    }
    
    //找到该节点
    private RBNode<T> search(T key){
    	RBNode<T> p = root;
    	while(p != null){
    		if(p.key.compareTo(key) > 0){
    			p = p.left;
    		}else if(p.key.compareTo(key) == 0){
    			return p;
    		}else{
    			p = p.right;
    		}
    	}
    	return null;
    }
    
    //打印红黑树
    public void print() {  
        if(root != null) {        	
        	 print(root);
        }  
    }  
    
    private void print(RBNode<T> root) {  
        if(root != null) {  
            System.out.print(root + " ");
            print(root.left);  
            print(root.right);  
        }  
    }  
    
    
    
	public static void main(String[] args) {
		int array[] = {10, 40, 30, 60, 90, 70, 20, 50, 80};
		RBTree<Integer> tree = new RBTree<Integer>();
		for(int i = 0; i < array.length; i++){
			tree.insert(array[i]);
		}
		tree.print();
		System.out.println();
		tree.remove(30);
		tree.print();
		System.out.println();
		
	}
	
	
	

}
