import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

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
	private int numNodes;
	private Cache<TreeObject<T>> cache;
	private boolean usingCache;
	
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
		this.numNodes = 1;
		usingCache = false;
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
		
		boolean inCache = false;
		if(usingCache) {
			inCache = checkCache(obj);
		}
				
		if(!inCache)
		{
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
			numNodes++;
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
			numNodes++;
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
	
	public void enableCache(int cacheSize)
	{
		cache = new Cache<TreeObject<T>>(cacheSize);
		usingCache = true;
	}
	
	private boolean checkCache(TreeObject<T> check) 
	{
		LinkedList<TreeObject<T>> cacheList = cache.getCacheList();
		//boolean searching = true;
		Iterator<TreeObject<T>> iterator = cacheList.iterator();
		//TreeObject<T> currentObj;
		
		for(TreeObject<T> o: cacheList)
		{
			if(o.compareTo(check) == 0)
			{
				o.incFrequency();
				cache.getObject(o);
				return true;
			}
		}
		cache.addObject(check);
		return false;
	}
	
	public int getDegree()
	{
		return this.degree;
	}
	
	public int getNumNodes()
	{
		return this.numNodes;
	}
	
	public void writeLongsToBinary(String fileName, int keyByteSize) throws FileNotFoundException
	{
		RandomAccessFile file = new RandomAccessFile(fileName, "rw");
		try {
			// BTree metadata
			file.writeInt(degree);		//4 bytes
			file.writeInt(numNodes);	//4 bytes
			file.writeInt(keyByteSize);	//4 bytes
			
			
			ArrayList<BTreeNode<T>> orderedList = getInOrderNodeArray();
			int byteOffset = 12;
			int nodeSize = 4 + 4 + (4*(2*this.degree + 1)) + (keyByteSize*(2*this.degree - 1));	
			int pointerLocation = 12;
			
			for(BTreeNode<T> n: orderedList)
			{
				int parentOffset = byteOffset + (orderedList.indexOf(n.getParent())*nodeSize);
				
				//move to write pointers
				file.seek(pointerLocation);
				file.writeInt(parentOffset);							//4 for parent
				for(int i = 0; i < n.getNumChildren(); i++)				//4*2t for children
				{
					file.writeInt(byteOffset + (orderedList.indexOf(n.getChild(i))*nodeSize));
				}
				
				//move after pointers
				file.seek(pointerLocation+(4*(2*this.degree + 1)));
				
				file.writeInt(n.getNumObjects());	//number of keys	//4
				file.writeInt(pointerLocation);		//this location		//4
				
				
				TreeObject<T>[] objects = n.getObjects();//all objects in node	//8*(2t-1)

				for(int o = 0; o < n.getNumObjects(); o++)
				{
					file.writeLong((long) objects[o].getKey()); 
				}
				
				pointerLocation += nodeSize;
			}
			//file.writeInt()
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readFromBinary(String fileName) throws FileNotFoundException
	{
		RandomAccessFile file = new RandomAccessFile(fileName, "r");
		
		try {
			int readDegree = file.readInt();
			int readNumNodes = file.readInt();
			int readKeySize = file.readInt();
			
			int nodeStart = 12;
			int nodeSize = 4 + 4 + (4*(2*readDegree + 1)) + (readKeySize*(2*readDegree - 1));
			
			int firstParent = file.readInt();
			file.seek(12+(4*(2*this.degree + 1))+4 + 4 + (4*(2*this.degree + 1)) + (8*(2*this.degree - 1)));
			int firstNumKeys = file.readInt();
			file.readInt();
			float firstKey = file.readFloat();
			
			System.out.println("Degree: " + readDegree);
			System.out.println("NumNodes: " + readNumNodes);
			System.out.println("KeySize: " + readKeySize);
			System.out.println("firstNumKeys: " + firstNumKeys);

			System.out.println("firstKey: " + firstKey);


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<TreeObject<T>> getInOrderObjectArray()
	{
		ArrayList<TreeObject<T>> array = new ArrayList<TreeObject<T>>();
		addSubtreeObjects(root, array);
		return array;
	}
	public ArrayList<BTreeNode<T>> getInOrderNodeArray()
	{
		ArrayList<BTreeNode<T>> array = new ArrayList<BTreeNode<T>>();
		addSubtreeNodes(root, array);
		return array;
	}
	
	private void addSubtreeObjects(BTreeNode<T> node, ArrayList<TreeObject<T>> array)
	{
		int i;
		for(i = 0; i < node.getNumObjects(); i++)
		{
			if(!node.isLeaf())
				addSubtreeObjects(node.getChild(i), array);
			array.add(node.getObjects()[i]);
		}
		if(!node.isLeaf())
			addSubtreeObjects(node.getChild(i), array);
	}
	
	private void addSubtreeNodes(BTreeNode<T> node, ArrayList<BTreeNode<T>> array)
	{
		int i;
		for(i = 0; i < node.getNumObjects(); i++)
		{
			if(!node.isLeaf())
				addSubtreeNodes(node.getChild(i), array);
		}
		array.add(node);
		if(!node.isLeaf())
			addSubtreeNodes(node.getChild(i), array);
	}
	
	
}
