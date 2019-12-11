/**
 * Stores object data and pointers to children/parent nodes
 * 
 * @author Skip-Skip
 *
 * @param <T>
 */
public class BTreeNode<T> {
	
	private TreeObject<T>[] objects;
	private BTreeNode<T> parent;
	private BTreeNode<T>[] children;
	private int numChildren;
	private int numObjects;
	private int degree;
	private int maxObjs;
	private boolean isLeaf;
	
	private int byteOffset;
	private int[] childrenOffsets;
	private int parentOffset;
	
	/**
	 * Constructor
	 * @param obj - TreeObject this node stores
	 * @param numChildren - Maximum number of children
	 */
	@SuppressWarnings("unchecked")
	public BTreeNode (int degree)
	{
		this.degree = degree;
		this.numObjects = 0;
		this.isLeaf = true;
		this.maxObjs = (degree*2)-1;
		this.objects = (TreeObject<T>[]) new TreeObject[maxObjs];
		this.parent = null;
		this.children = (BTreeNode<T>[]) new BTreeNode[(degree*2)];
		
		this.parentOffset = -1;
		this.byteOffset = -1;
	}
	
	/**
	 * This nodes objects
	 * @return - array of TreeObjects
	 */
	public TreeObject<T>[] getObjects() {
		return this.objects;
	}
	
	/**
	 * Attempts to add an object to this node (in order)
	 * @param obj - TreeObject to add
	 * @throws ArrayIndexOutOfBoundsException - if node is full
	 */
	public void addObject(TreeObject<T> obj) throws ArrayIndexOutOfBoundsException{
		if(numObjects >= maxObjs)
			throw new ArrayIndexOutOfBoundsException("BTreeNode: addObject - Failed to add object - Node is full");
		else if (numObjects == 0) {
			objects[0] = obj;
			numObjects++;
		}
		else {
			boolean placedObj = false;
			int i = 0;
			
			while(!placedObj) {
				
				//object is last in sorting order
				if (i == numObjects)
				{
					objects[i] = obj;
					placedObj = true;
					numObjects++;
				}
				//duplicate object
				else if (objects[i].compareTo(obj) == 0)
				{
					objects[i].incFrequency();
					placedObj = true;
				}
				//if an object of greater value is found
				else if(objects[i].compareTo(obj) > 0) {
					//shifts all following objects and inserts the new object
					for(int j = numObjects; j > i; j--)
					{
						objects[j] = objects[j-1];
					}
					objects[i] = obj;
					placedObj = true;
					numObjects++;

				}
				i++;
			}
		}
	}
	
	/**
	 * The child at the specified index
	 * @param index
	 * @return child at index
	 */
	public BTreeNode<T> getChild(int index) {
		return children[index];
	}
	
	/**
	 * Removes the last object from the list of stored objects
	 * @return removed TreeObject
	 */
	public TreeObject<T> removeLast(){
		TreeObject<T> obj = objects[numObjects-1];
		objects[numObjects-1] = null;
		numObjects--;
		return obj;
	}
	
	/**
	 * 
	 * @return Number of keys/TreeObjects stored in this node
	 */
	public int getNumObjects() {
		return this.numObjects;
	}
	
	/**
	 * 
	 * @return Number of children for this node
	 */
	public int getNumChildren() {
		return this.numChildren;
	}
	
	/**
	 * Sets this nodes parent pointer
	 * @param parent
	 */
	public void setParent(BTreeNode<T> parent)
	{
		this.parent = parent;
	}
	
	/**
	 * 
	 * @return BTreeNode representing this nodes parent
	 */
	public BTreeNode<T> getParent()
	{
		return this.parent;
	}

	/**
	 * 
	 * @return array of BTreeNodes representing this nodes children
	 */
	public BTreeNode<T>[] getChildren() {
		return this.children;
	}

	/**
	 * 
	 * @return true if this node has no children
	 */
	public boolean isLeaf() {
		return isLeaf;
	}

	/**
	 * Inserts a new child under this node (in order)
	 * @param node
	 */
	public void addChild(BTreeNode<T> node) {
		if(this.numObjects == 0) {
			this.children[0] = node;
		}
		else {
			TreeObject<T> childObj = node.getObjects()[0];
			int i = 0;
			boolean placedChild = false;
			while(!placedChild) {
				//places at final child position
				if(i == numObjects)
				{
					children[i] = node;
					placedChild = true;
				}
				//if an object of greater value than the childObj is found
				else if(objects[i].compareTo(childObj) > 0) {
					for(int j = numObjects; j > i; j--)
					{
						children[j] = children[j-1];
					}
					children[i] = node;
					placedChild = true;
				}
				i++;
			}
		}
		this.numChildren++;
		if(this.numChildren > 0)
		{
			isLeaf = false;
		}
	}
	
	public void setObjects (TreeObject<T>[] objs)
	{
		this.objects = objs;
		this.numObjects = objs.length;
	}
	
	/**
	 * Removes child from specified index
	 * @param index
	 * @return the child removed
	 */
	public BTreeNode<T> removeChild(int index){
		BTreeNode<T> c = this.children[index];
		this.children[index] = null;
		this.numChildren--;
		if(this.numChildren == 0)
		{
			isLeaf = true;
		}
		return c;
	}
	
	@Override
	public String toString() {
		String s = "[ ";
		for(int i = 0; i < this.numObjects; i++) {
			s += (this.objects[i].getKey().toString() + " ");
		}
		s += "]";
		return s;
	}
	
	public void setBinaryData(int offset, int parent, int[] children)
	{
		this.byteOffset = offset;
		this.parentOffset = parent;
		this.childrenOffsets = children;
	}
	public int getBinaryOffset()
	{
		return this.byteOffset;
	}
	public int getBinaryParent()
	{
		return this.parentOffset;
	}
	public int[] getBinaryChildren()
	{
		return this.childrenOffsets;
	}
	
	
}
