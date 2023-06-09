package nikola.pavicevic.shoppinglist;

public class JNI {
    static{
        System.loadLibrary("IncrementLibrary");
    }
    public native int increment(int x);
}
