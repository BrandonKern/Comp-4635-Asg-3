//pair class gotten from: https://iqcode.com/code/java/java-arraylist-of-pairs

// Purpose: used in game file to store the pair for the index of the word in the puzzle and the actual word.
public class Pair<Key,Value> {
    private Key key;
    private Value value;

    public Pair(Key key, Value value){
        this.key = key;
        this.value = value;
    }

    public Key getKey(){ return this.key; }
    public Value getValue(){ return this.value; }

    public void setKey(Key key){ this.key = key; }
    public void setValue(Value value){ this.value = value; }
}
