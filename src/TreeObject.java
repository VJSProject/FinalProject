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
    
    public int compareTo(TreeObject<T> obj) {
    	
    	if(this.key.toString().compareTo(obj.key.toString()) < 0)
    		return -1;
    	else if(this.key.toString().compareTo(obj.key.toString()) > 0)
    		return 1;
    	else
    		return 0;
    }
    
    public String toString() {
    	return this.key.toString();
    }
}
