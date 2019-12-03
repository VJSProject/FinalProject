import java.util.ArrayList;

/**
 * Uses BTreeNodes to manage a BTree
 * 
 * @author Skip-Skip
 *
 * @param <T>
 */
public class BTree<T> {
	
	private int degree;
	private int minKeys;
	private int maxKeys;
	private BTreeNode<T> root;
	
	/**
	 * Constructor
	 * @param degree
	 */
	public BTree (int degree)
	{
		this.degree = degree;
		this.minKeys = degree - 1;
		this.maxKeys = (2*degree) - 1;
		root = new BTreeNode<T>(degree);
	}
	
	/**
	 * Builds the tree using an array of generic TreeObjects
	 * @param objects
	 */
	public void buildTree(TreeObject<T>[] objects)
	{
		for(TreeObject<T> o: objects) {
			insertObject(o);
		}
	}
	
	/**
	 * Builds the tree using an array of generic keys
	 * @param keys
	 */
	public void buildTree(T[] keys)
	{
		for(T k: keys) {
			insertKey(k);
		}
	}
	
	public void buildTree(ArrayList<T> keys)
	{
		for (T k: keys) {
			insertKey(k);
		}
	}
	
	/**
	 * Inserts TreeObject into the BTree (in order)
	 * @param obj
	 */
	public void insertObject(TreeObject<T> obj) {
		
			BTreeNode<T> n = root;
			//descends to correct leaf node
			boolean duplicate = false;
			while (!n.isLeaf() && !duplicate) {
				TreeObject<T>[] keys = n.getObjects();
				int i = 0;
				while(i < n.getNumObjects() && keys[i].compareTo(obj) <= 0) {
					if(keys[i].compareTo(obj) == 0) {
						duplicate = true;
					}
					i++;
				}
				if(!duplicate) {
					n = n.getChild(i);
				}
			}
			//try to add object, split if full
			try {
				n.addObject(obj);
			} catch (ArrayIndexOutOfBoundsException e) {
				splitNode(n);
				insertObject(obj);
			}		
	}

	/**
	 * Inserts a key into the BTree (in order)
	 * @param key
	 */
	public void insertKey(T key) {
		insertObject(new TreeObject<T>(key));
	}
	
	@Override
	//TODO - This doesn't print properly yet
	public String toString() {
		String s = "";
		BTreeNode<T> n = root;
		s += n.toString();
		BTreeNode<T>[] children = n.getChildren();
		
		while(!n.isLeaf())
		{
			for(BTreeNode<T> c: children) {
				s += c.toString();
			}
		}
		
		return s;
	}
	
	/**
	 * Splits the node
	 * @param node
	 */
	private void splitNode(BTreeNode<T> node) {
		//if root is full, split it
		if(this.root.getNumObjects() == this.maxKeys)
		{
			splitRoot();
		}
		//if parent is full, split it
		else if (node.getParent().getNumObjects() == this.maxKeys)
		{
			splitNode(node.getParent());
		}
		//split
		else {
			//puts half of objects in new child
			BTreeNode<T> newChild = new BTreeNode<T>(degree);
			while(node.getNumObjects() > maxKeys/2+1)
			{
				newChild.addObject(node.removeLast());
			}
			TreeObject<T> midObj = node.removeLast();
			//puts middle object in parent. Updates pointers
			node.getParent().addObject(midObj);			
			node.getParent().addChild(newChild);
			newChild.setParent(node.getParent());
			//if internal node, split children
			if(node.getNumChildren() != 0) {
				
				int totalChildren = node.getNumChildren();
				int rightPosition = node.getNumChildren() / 2;
				for(;rightPosition < totalChildren; rightPosition ++)
				{
					BTreeNode<T> move = node.removeChild(rightPosition);
					move.setParent(newChild);
					newChild.addChild(move);
				}
			}
		}
	}
	
	/**
	 * Splits root
	 */
	private void splitRoot() {
		root.setParent(new BTreeNode<T>(degree));
		root.getParent().addChild(root);
		BTreeNode<T> left = root;
		root = root.getParent();
		splitNode(left);
	}
}
