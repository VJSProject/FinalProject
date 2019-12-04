import java.util.LinkedList;

/**
 * Simulates a cache storage using a doubly linked list.
 * 
 * @author Skyler Chase
 *
 * @param <T> - type of object to be stored in the cache
 */
public class Cache<T> {
	//Instance variables//
	private int nr;					//number of references
	private LinkedList<T> cacheList;//stores objects to simulate a cache
	private int maxSize;			//capacity of cache
	private boolean isHit;			//whether last search was a hit

	/**
	 * Constructor. Creates an empty cache.
	 * @param maxSize - The maximum number of objects the cache can hold.
	 */
	public Cache(int maxSize) {
		//sets instance variables
		nr = 0;
		isHit = false;
		this.maxSize = maxSize;
		cacheList = new LinkedList<T>();
	}
	
	/**
	 * Searches the cache for an object using the updateCache method.
	 * Returns the object back.
	 * 
	 * @param obj - the object to search the cache for
	 * @return obj - the object searched for
	 */
	public T getObject(T obj) {
		updateCache(obj);
		return obj;
	}
	
	/**
	 * Adds an object to the front of the list.
	 * @param obj - object to be added
	 */
	public void addObject(T obj) {
		//adds object
		cacheList.addFirst(obj);
		//removes last element if allocated size is exceeded
		if(cacheList.size() > maxSize) {
			cacheList.removeLast();
		}
	}
	
	/**
	 * removes the object from the cache.
	 * @param obj - object to be removed
	 */
	public void removeObject(T obj) {
		cacheList.remove(obj);
	}

	/**
	 * Removes all objects from the cache. Resets hits and references.
	 */
	public void clearCache() {
		cacheList.clear();
		nr = 0;
		isHit = false;
	}
	
	/**
	 * Boolean check if object was found
	 * @return - True if the last search was a hit.
	 */
	public boolean isHit() {
		return isHit;
	}
	
	/**
	 * Returns the total number of references to the cache
	 * @return number of references
	 */
	public int getNumberOfReferences() {
		return nr;
	}

	public LinkedList<T> getCacheList()
	{
		return this.cacheList;
	}
	
	/**
	 * Searches and updates the cache storage and variables. Used in getObject.
	 * @param item - object to be searched for
	 */
	private void updateCache(T item) {
		//increments references. Resets isHit.
		nr++;	
		isHit = false;
		//if item is found in cache...
		if(cacheList.contains(item)) {
			//successful hit
			isHit = true;
			//removes item from current location to be moved to front
			removeObject(item);
		}
		//adds the item to the front of the cache
		addObject(item);
	}
}
