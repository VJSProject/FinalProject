public class BTreeNode<T> {
	
	private TreeObject<T>[] objects;
	private BTreeNode<T> parent;
	private BTreeNode<T>[] children;
	private int numChildren;
	private int numObjects;
	private int degree;
	private int maxObjs;
	private boolean isLeaf;
	
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

	public BTreeNode<T> getChild(int index) {
		return children[index];
	}
	public TreeObject<T> removeLast(){
		TreeObject<T> obj = objects[numObjects-1];
		objects[numObjects-1] = null;
		numObjects--;
		return obj;
	}
	
	public int getNumObjects() {
		return this.numObjects;
	}
	
	public int getNumChildren() {
		return this.numChildren;
	}
	public void setParent(BTreeNode<T> node)
	{
		this.parent = node;
	}
	
	public BTreeNode<T> getParent()
	{
		return this.parent;
	}

	public BTreeNode<T>[] getChildren() {
		return this.children;
	}
/*
	public void setChildren(BTreeNode<T>[] children) {
		this.children = children;
		this.numChildren = children.length;
	}
*/
	public boolean isLeaf() {
		return isLeaf;
	}

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
	
	public String toString() {
		String s = "[ ";
		for(int i = 0; i < this.numObjects; i++) {
			s += (this.objects[i].getKey().toString() + " ");
		}
		s += "]";
		return s;
	}
}
