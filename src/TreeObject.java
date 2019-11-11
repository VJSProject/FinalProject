public class TreeObject<T> {

    private T key;
    private int frequency;

    public TreeObject(T key){
        this.key = key;
    }

    public T getKey(){
        return this.key;
    }

    public void incFrequency(){
        frequency++;
    }
}
