public class TreeObject<T> {

    private T key;
    private int frequency;

    public TreeObject(T key){
        this.key = key;
        this.frequency = 1;
    }
    
    public T getKey(){
        return this.key;
    }

    public void setKey(T key) {
    	this.key = key;
    }
    public void incFrequency(){
        frequency++;
    }
    
    /**
     * compares the keys string values
     * @param obj
     * @return -1 if comparatively less than, 1 if greater, 0 if equal
     */
    public int compareTo(TreeObject<T> obj) {
    	String thisKey = this.key.toString();
        String comparisonKey = obj.key.toString();
        
        if(thisKey.length() > comparisonKey.length())
            return 1;
        else if(thisKey.length() < comparisonKey.length())
            return -1;
        else
        {
            if(thisKey.compareTo(comparisonKey) < 0)
                return -1;
            else if(thisKey.compareTo(comparisonKey) > 0)
                return 1;
            else
                return 0;
        }
    }
    
    @Override
    public String toString() {
    	return this.key.toString();
    }
}
