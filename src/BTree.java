public class BTree<T> {
	
	private int degree;
	private int minKeys;
	private int maxKeys;
	private BTreeNode<T> root;
	
	public BTree (int degree)
	{
		this.degree = degree;
		this.minKeys = degree - 1;
		this.maxKeys = (2*degree) - 1;
		root = new BTreeNode<T>(degree);
	}
	
	public void buildTree(TreeObject<T>[] objects)
	{
		for(TreeObject<T> o: objects) {
			insertObject(o);
		}
	}
	public void buildTree(T[] keys)
	{
		for(T k: keys) {
			insertKey(k);
		}
	}
	
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
			//try to add object, otherwise split
			try {
				n.addObject(obj);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println(e.getMessage());
				/*if(n == root) {
					root.setParent(new BTreeNode<T>(degree));
					root.getParent().addChild(root);
					splitNode(root);
					root = root.getParent();
					root.setLeaf(false);
					insertObject(obj);
				}
				else {
					splitNode(n);
					insertObject(obj);
				}*/
				splitNode(n);
				insertObject(obj);
			}
			
		
	}

	public void insertKey(T key) {
		insertObject(new TreeObject<T>(key));
	}
	
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
	
	private void splitNode(BTreeNode<T> node) {
		if(this.root.getNumObjects() == this.maxKeys)
		{
			splitRoot();
		}
		else if (node.getParent().getNumObjects() == this.maxKeys)
		{
			splitNode(node.getParent());
		}
		else {
		BTreeNode<T> newChild = new BTreeNode<T>(degree);
		while(node.getNumObjects() > maxKeys/2+1)
		{
			newChild.addObject(node.removeLast());
		}
		TreeObject<T> midObj = node.removeLast();
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
	
	@SuppressWarnings("unchecked")
	private void splitRoot() {
		root.setParent(new BTreeNode<T>(degree));
		root.getParent().addChild(root);
		BTreeNode<T> left = root;
		root = root.getParent();
		splitNode(left);

		/*
		if(left.getNumChildren() != 0) {
			BTreeNode<T>[] leftChildren = left.getChildren();
			BTreeNode<T>[] rightChildren = (BTreeNode<T>[])new BTreeNode[degree*2];
			
			int totalChildren = left.getNumChildren();
			int rightPosition = left.getNumChildren() / 2;
			for(;rightPosition < totalChildren; rightPosition ++)
			{
				BTreeNode<T> move = left.removeChild(rightPosition);
				move.setParent(root.getChild(1));
				root.getChild(1).addChild(move);
			}
		}
		*/
	}
	@SuppressWarnings("unchecked")
	private void splitInternal(BTreeNode<T> node) {
		BTreeNode<T> left = node;
		BTreeNode<T> parent = node.getParent();
		splitNode(left);

		if(left.getNumChildren() != 0) {
			BTreeNode<T>[] leftChildren = left.getChildren();
			BTreeNode<T>[] rightChildren = (BTreeNode<T>[])new BTreeNode[degree*2];
			
			int totalChildren = left.getNumChildren();
			int rightPosition = left.getNumChildren() / 2;
			for(;rightPosition < totalChildren; rightPosition ++)
			{
				BTreeNode<T> move = left.removeChild(rightPosition);
				move.setParent(root.getChild(1));
				root.getChild(1).addChild(move);
			}
		}
	}
}
