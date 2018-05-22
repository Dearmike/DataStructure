package cn.scu.edu.tree;
/**
 * 主要实现了平衡二叉树的插入和删除
 * @author luo hui
 *
 * @param <T>
 */
public class AVLTree<T extends Comparable<T>> {
	
	private AVLTreeNode<T> root;
	
	public AVLTree(){
		root = null;
	}
	
	public void insert(T element){
		root = insert(element, root);
	}
	
	public AVLTreeNode<T> remove(T element){
		root = remove(element, root);
		return root;
	}
	
	public void printTree(){
		printTree(root);
		System.out.println();
	}
	
    private AVLTreeNode<T> remove(T element, AVLTreeNode<T> root){
    	if(root == null){
    		return null;
    	}
    	int cmp = element.compareTo(root.data);
    	if(cmp < 0){
    		//向左递归处理
    		root.lchild = remove(element, root.lchild);
    		//右高左低
    		if(height(root.rchild) - height(root.lchild) == 2){
    			AVLTreeNode<T> currentNode = root.rchild;
    			if(height(currentNode.rchild) >= height(currentNode.lchild)){
    				//符合RR类型
    				root = leftRotate(root);
    			}else{
    				//符合RL类型
    				root = rightLeftRotate(root);
    			}			
    		}
    	}else if(cmp > 0){
    		//向右递归处理
    		root.rchild = remove(element, root.rchild);
    		//左高右低
    		if(height(root.lchild) - height(root.rchild) == 2){
    			AVLTreeNode<T> currentNode = root.lchild;
    			if(height(currentNode.lchild) >= height(currentNode.rchild)){
    				//符合LL类型
    				root = rightRotate(root);
    			}else{
    				//符合LR类型
    				root = leftRightRotate(root);
    			}			
    		}
    	}else{
    		//找到了要删除的节点
    		//共分为3种情况 1.要删除的节点的左右子树都不为空 2.左子树不为空 3.右子树不为空
    		if(root.lchild != null && root.rchild != null){
    			//找到右子树的最左节点来替代被删除的节点
    			AVLTreeNode<T> replaceNode = root.rchild;
    			while(replaceNode.lchild != null){
    				replaceNode = replaceNode.lchild;
    			}
    			root.data = replaceNode.data;
    			root.rchild = remove(replaceNode.data, root.rchild); 			
    		}else{
    			root = root.lchild != null ? root.lchild : root.rchild;
    		}
    	}
    	//更新高度
    	if(root != null){
    		root.height = Math.max(height(root.lchild), height(root.rchild)) + 1;
    	}		
		return root;
	} 
	
	private void printTree(AVLTreeNode<T> root){
		if(root != null){
			System.out.print(root.data + " ");
			printTree(root.lchild);
			printTree(root.rchild);
		}
	}
	
	private AVLTreeNode<T> insert(T element, AVLTreeNode<T> root){
		if(root == null){
			root =  new AVLTreeNode<T>(element);
		}else{
			int cmp = element.compareTo(root.data);
			//不允许插入重复的值
			if(cmp < 0){ //插入到左子树
				root.lchild = insert(element, root.lchild);
				if(height(root.lchild) - height(root.rchild) == 2){
					if(element.compareTo(root.lchild.data) < 0){
						root = rightRotate(root);
					}else{
						root = leftRightRotate(root);
					}
					
				}
			}else if(cmp > 0){//插入到右子树
				root.rchild = insert(element, root.rchild);
				if(height(root.rchild) - height(root.lchild) == 2){
					if(element.compareTo(root.rchild.data) > 0){
						root = leftRotate(root);
					}else{
						root = rightLeftRotate(root);
					}					
				}
			}			
		}
		root.height = Math.max(height(root.lchild), height(root.rchild)) + 1;
		return root;
	}
	
	private int height(AVLTreeNode<T> node){
		if(node != null){
			return node.height;
		}
		return 0;
	}
	
	//以proot进行左旋
	private AVLTreeNode<T> leftRotate(AVLTreeNode<T> proot){
		AVLTreeNode<T> prchild = proot.rchild;
		proot.rchild = prchild.lchild;
		prchild.lchild = proot;
		//节点高度更新
		proot.height = Math.max(height(proot.lchild), height(proot.rchild)) + 1;
		prchild.height = Math.max(height(prchild.lchild), height(prchild.rchild)) + 1;
		return prchild;
	}
	//以proot进行右旋
	private AVLTreeNode<T> rightRotate(AVLTreeNode<T> proot){
		AVLTreeNode<T> plchild = proot.lchild;
		proot.lchild = plchild.rchild;
		plchild.rchild = proot;
		//节点高度更新
		proot.height = Math.max(height(proot.lchild), height(proot.rchild)) + 1;
		plchild.height = Math.max(height(plchild.lchild), height(plchild.rchild)) + 1;
		return plchild;
	}
	
	//先左旋后右旋
	private AVLTreeNode<T> leftRightRotate(AVLTreeNode<T> proot){
		proot.lchild = leftRotate(proot.lchild);
		return rightRotate(proot);
	}
	
	//先右旋后左旋
	private AVLTreeNode<T> rightLeftRotate(AVLTreeNode<T> proot){
		proot.rchild = rightRotate(proot.rchild);
		return leftRotate(proot);
	}
	
    class AVLTreeNode<T extends Comparable<T>>{
		T data;     //当前节点存储的数据
		int height; //作为子树时节点的高度
		AVLTreeNode<T> lchild; //当前节点的左孩子节点
		AVLTreeNode<T> rchild;//当前节点的右孩子节点
		
		public AVLTreeNode(T data){
			this.data = data;			
			this.lchild = null;
			this.rchild = null;
		}
		
		public AVLTreeNode(T data, AVLTreeNode<T> lchild, AVLTreeNode<T> rchild){
			this.data = data;			
			this.lchild = lchild;
			this.rchild = rchild;
		}
		
		
	}
    
    public static void main(String[] args) {
		AVLTree<Integer> tree = new AVLTree<Integer>();
		tree.insert(10);
		tree.insert(1);
		tree.insert(6);
		tree.insert(4);
		tree.insert(3);	
		tree.insert(15);
		tree.insert(20);
		tree.insert(9);
		tree.insert(8);
		tree.printTree();
		tree.remove(6);
		//tree.remove(4);
		tree.printTree();
	}
	
	

}
